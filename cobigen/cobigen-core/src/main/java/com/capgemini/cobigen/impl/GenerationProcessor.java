package com.capgemini.cobigen.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.PluginRegistry;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.capgemini.cobigen.impl.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.impl.config.TemplatesConfiguration;
import com.capgemini.cobigen.impl.config.entity.ContainerMatcher;
import com.capgemini.cobigen.impl.config.entity.Matcher;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.io.AccumulationType;
import com.capgemini.cobigen.impl.config.nio.NioFileSystemTemplateLoader;
import com.capgemini.cobigen.impl.exceptions.CobiGenRuntimeException;
import com.capgemini.cobigen.impl.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.impl.exceptions.PluginProcessingException;
import com.capgemini.cobigen.impl.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.impl.model.ModelBuilderImpl;
import com.capgemini.cobigen.impl.validator.InputValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

/**
 * Generation processor. Caches calculations and thus should be newly created on each request.
 */
public class GenerationProcessor {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(GenerationProcessor.class);

    /** {@link ConfigurationHolder} for configuration caching purposes */
    private ConfigurationHolder configurationHolder;

    /** FreeMarker configuration to utilize */
    private Configuration freeMarkerConfig;

    /** States, whether existing contents should be overwritten by generation */
    private boolean forceOverride;

    /** Input to process generation for */
    private Object input;

    /** Artifacts to generate, e.g. templates or increments */
    private List<GenerableArtifact> generableArtifacts;

    /** Java classes to be served by the model implementing template logic */
    private List<Class<?>> logicClasses;

    /** Externally provided model to be used for generation */
    private Map<String, Object> rawModel;

    /** Report to be returned after generation processing */
    private GenerationReportTo generationReport;

    /**
     * Creates a new {@link GenerationProcessor} instance. Due to caching, one instance should only be used
     * for one generation request.
     *
     * @param configurationHolder
     *            {@link ConfigurationHolder} instance
     * @param freeMarkerConfig
     *            FreeMarker configuration
     * @param input
     *            generator input object
     * @param generableArtifacts
     *            a {@link List} of artifacts to be generated
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @param rawModel
     *            externally adapted model to be used for generation.
     */
    public GenerationProcessor(ConfigurationHolder configurationHolder, Configuration freeMarkerConfig,
        Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride,
        List<Class<?>> logicClasses, Map<String, Object> rawModel) {

        InputValidator.validateInputsUnequalNull(input, generableArtifacts);

        this.configurationHolder = configurationHolder;
        this.freeMarkerConfig = freeMarkerConfig;
        this.forceOverride = forceOverride;
        this.input = input;
        this.generableArtifacts = generableArtifacts;
        this.logicClasses = logicClasses;
        this.rawModel = rawModel;

        generationReport = new GenerationReportTo();
    }

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     * @return {@link GenerationReportTo the GenerationReport}
     */
    GenerationReportTo generate() {

        Collection<TemplateTo> templatesToBeGenerated = flatten(generableArtifacts);

        for (TemplateTo template : templatesToBeGenerated) {
            try {
                Trigger trigger =
                    configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
                TriggerInterpreter triggerInterpreter =
                    PluginRegistry.getTriggerInterpreter(trigger.getType());
                InputValidator.validateTriggerInterpreter(triggerInterpreter,
                    configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId()));
                generate(template, triggerInterpreter);
            } catch (CobiGenRuntimeException e) {
                generationReport.addError(e.getMessage(), e);
            } catch (Throwable e) {
                generationReport.addError("Something unexpected happened. [" + e.getClass().getSimpleName()
                    + ": " + e.getMessage() + "]", e);
            }
        }

        return generationReport;
    }

    /**
     * Flattens the {@link GenerableArtifact}s to a list of {@link TemplateTo}s also removing duplicates.
     * @param generableArtifacts
     *            {@link List} of {@link GenerableArtifact}s to be flattened
     * @return {@link Collection} of collected {@link TemplateTo}s
     */
    private Collection<TemplateTo> flatten(List<GenerableArtifact> generableArtifacts) {

        // create Map to remove duplicates by ID
        Map<String, TemplateTo> templateIdToTemplateMap = Maps.newHashMap();

        for (GenerableArtifact artifact : generableArtifacts) {
            if (artifact instanceof TemplateTo) {
                TemplateTo template = (TemplateTo) artifact;
                checkAndAddToTemplateMap(templateIdToTemplateMap, template);
            } else if (artifact instanceof IncrementTo) {
                for (TemplateTo template : ((IncrementTo) artifact).getTemplates()) {
                    checkAndAddToTemplateMap(templateIdToTemplateMap, template);
                }
            } else {
                throw new IllegalArgumentException(
                    "Unknown GenerableArtifact type '" + artifact.getClass().getCanonicalName() + "'.");
            }
        }

        return templateIdToTemplateMap.values();
    }

    /**
     * Checks whether the template already exists in the templateIdToTemplateMap. If so, a warning will be
     * generated and the template will be overwritten in the map.
     * @param templateIdToTemplateMap
     *            Mapping from template ID to template
     * @param template
     *            {@link TemplateTo} to be added.
     */
    private void checkAndAddToTemplateMap(Map<String, TemplateTo> templateIdToTemplateMap,
        TemplateTo template) {
        if (templateIdToTemplateMap.containsKey(template.getId())) {
            String oldTriggerId = templateIdToTemplateMap.get(template.getId()).getTriggerId();
            if (oldTriggerId == template.getTriggerId()) {
                generationReport.addWarning("Template with ID '" + template.getId()
                    + "' has been triggered by two different triggers ['" + oldTriggerId + "','"
                    + template.getTriggerId() + "']. This might lead to unintended generation results"
                    + " if the trigger's variableAssignments differ.");
            }
        }
        templateIdToTemplateMap.put(template.getId(), template);
    }

    /**
     * Generates code for the given input with the given template and the given {@link TriggerInterpreter} to
     * the destination specified by the templates configuration.
     *
     * @param template
     *            to be processed for generation
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to be used for reading the input and creating the model
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     */
    private void generate(TemplateTo template, TriggerInterpreter triggerInterpreter) {

        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
        ((NioFileSystemTemplateLoader) freeMarkerConfig.getTemplateLoader())
            .setTemplateRoot(configurationHolder.readContextConfiguration().getConfigurationPath()
                .resolve(trigger.getTemplateFolder()));

        List<Object> inputObjects = collectInputObjects(input, triggerInterpreter, trigger);
        Template templateIntern = getTemplate(template, triggerInterpreter);
        InputReader inputReader = triggerInterpreter.getInputReader();

        for (Object generatorInput : inputObjects) {

            Map<String, Object> model = buildModel(triggerInterpreter, trigger, generatorInput);

            String targetCharset = templateIntern.getTargetCharset();
            LOG.info("Generating template '{}' ...", templateIntern.getName(), generatorInput);

            File originalFile = getDestinationFile(templateIntern.resolveDestinationPath(generatorInput));
            if (originalFile.exists()) {
                if (forceOverride || templateIntern.getMergeStrategy() == null) {
                    generateTemplateAndWriteFile(originalFile, templateIntern, model, targetCharset,
                        inputReader, generatorInput);
                } else {
                    try (Writer out = new StringWriter()) {
                        generateTemplateAndWritePatch(out, templateIntern, model, targetCharset, inputReader,
                            generatorInput);
                        String result = null;
                        Merger merger = PluginRegistry.getMerger(templateIntern.getMergeStrategy());
                        if (merger != null) {
                            result = merger.merge(originalFile, out.toString(), targetCharset);
                        } else {
                            throw new InvalidConfigurationException("No merger for merge strategy '"
                                + templateIntern.getMergeStrategy() + "' found.");
                        }

                        if (result != null) {
                            LOG.debug("Merge {} with char set {}.", originalFile.getName(), targetCharset);
                            FileUtils.writeStringToFile(originalFile, result, targetCharset);
                        } else {
                            throw new PluginProcessingException("Merger " + merger.getType()
                                + " returned null on merge(...), which is not allowed.");
                        }
                    } catch (IOException e) {
                        throw new CobiGenRuntimeException(
                            "Could not write file " + originalFile.toURI().toString() + " after merge.", e);
                    }
                }
            } else {
                LOG.info("Create new File {} with charset {}.", originalFile.toURI(), targetCharset);
                generateTemplateAndWriteFile(originalFile, templateIntern, model, targetCharset, inputReader,
                    generatorInput);
            }
        }
    }

    /**
     * Builds the model for he given input.
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to be used
     * @param trigger
     *            activated {@link Trigger}
     * @param generatorInput
     *            input for generation to retrieve information from.
     * @return the object model for generation.
     */
    private Map<String, Object> buildModel(TriggerInterpreter triggerInterpreter, Trigger trigger,
        Object generatorInput) {
        ModelBuilderImpl modelBuilderImpl = new ModelBuilderImpl(generatorInput, trigger);
        Map<String, Object> model;

        if (rawModel != null) {
            model = rawModel;
        } else {
            model = modelBuilderImpl.createModel(triggerInterpreter);
        }
        if (logicClasses != null) {
            modelBuilderImpl.enrichByLogicBeans(model, logicClasses);
        }
        return model;
    }

    /**
     * Collects all input objects. Especially, resolves container inputs.
     * @param input
     *            object
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to be used
     * @param trigger
     *            {@link Trigger} to be used
     * @return the {@link List} of collected input objects.
     */
    private List<Object> collectInputObjects(Object input, TriggerInterpreter triggerInterpreter,
        Trigger trigger) {

        InputReader inputReader = triggerInterpreter.getInputReader();
        List<Object> inputObjects = Lists.newArrayList(input);

        if (inputReader.combinesMultipleInputObjects(input)) {

            // check whether the inputs should be retrieved recursively
            boolean retrieveInputsRecursively = false;
            for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
                MatcherTo matcherTo =
                    new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(), input);
                if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                    if (!retrieveInputsRecursively) {
                        retrieveInputsRecursively = containerMatcher.isRetrieveObjectsRecursively();
                    } else {
                        break;
                    }
                }
            }

            if (retrieveInputsRecursively) {
                inputObjects = inputReader.getInputObjectsRecursively(input, trigger.getInputCharset());
            } else {
                inputObjects = inputReader.getInputObjects(input, trigger.getInputCharset());
            }

            // Remove non matching inputs
            Iterator<Object> it = inputObjects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (!matches(next, trigger.getMatcher(), triggerInterpreter)) {
                    it.remove();
                }
            }
        }
        return inputObjects;
    }

    /**
     * Checks whether the list of matches matches the matcher input according to the given trigger
     * interpreter.
     * @param matcherInput
     *            input for the matcher
     * @param matcherList
     *            list of matchers to be checked
     * @param triggerInterpreter
     *            to called for checking retrieving the matchers matching result
     * @return <code>true</code> if the given matcher input matches the matcher list<br>
     *         <code>false</code>, otherwise
     * @author mbrunnli (22.02.2015)
     */
    public static boolean matches(Object matcherInput, List<Matcher> matcherList,
        TriggerInterpreter triggerInterpreter) {
        boolean matcherSetMatches = false;
        LOG.info("Check matchers for TriggerInterpreter[type='{}'] ...", triggerInterpreter.getType());
        MATCHER_LOOP:
        for (Matcher matcher : matcherList) {
            MatcherTo matcherTo = new MatcherTo(matcher.getType(), matcher.getValue(), matcherInput);
            LOG.debug("Check {} ...", matcherTo);
            if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                switch (matcher.getAccumulationType()) {
                case NOT:
                    LOG.debug("NOT Matcher matches -> trigger match fails.");
                    matcherSetMatches = false;
                    break MATCHER_LOOP;
                case OR:
                case AND:
                    LOG.debug("Matcher matches.");
                    matcherSetMatches = true;
                    break;
                default:
                }
            } else {
                if (matcher.getAccumulationType() == AccumulationType.AND) {
                    LOG.debug("AND Matcher does not match -> trigger match fails.");
                    matcherSetMatches = false;
                    break MATCHER_LOOP;
                }
            }
        }
        LOG.info(
            "Matcher declarations " + (matcherSetMatches ? "match the input." : "do not match the input."));
        return matcherSetMatches;
    }

    /**
     * Generates the given template contents using the given model and writes the contents into the given
     * {@link File}
     *
     * @param output
     *            {@link File} to be written
     * @param template
     *            FreeMarker template which will generate the contents
     * @param model
     *            to generate with
     * @param outputCharset
     *            charset the target file should be written with
     * @param inputReader
     *            the input reader the model was built with
     * @param input
     *            generator input object
     */
    private void generateTemplateAndWriteFile(File output, Template template, Map<String, Object> model,
        String outputCharset, InputReader inputReader, Object input) {

        try (Writer out = new StringWriter()) {
            generateTemplateAndWritePatch(out, template, model, outputCharset, inputReader, input);
            FileUtils.writeStringToFile(output, out.toString(), outputCharset);
        } catch (IOException e) {
            throw new CobiGenRuntimeException(
                "Could not write file while processing template with id '" + template.getName() + "'", e);
        }
    }

    /**
     * Generates the given template contents using the given model and writes the contents into the given
     * {@link Writer}
     *
     * @param out
     *            {@link Writer} in which the contents will be written (the {@link Writer} will be flushed and
     *            closed)
     * @param template
     *            FreeMarker template which will generate the contents
     * @param model
     *            Object model for FreeMarker template generation
     * @param inputReader
     *            the input reader the model was built with
     * @param outputCharset
     *            charset the target file should be written with
     * @param input
     *            generator input object
     */
    private void generateTemplateAndWritePatch(Writer out, Template template, Map<String, Object> model,
        String outputCharset, InputReader inputReader, Object input) {

        freemarker.template.Template fmTemplate = null;
        try {
            fmTemplate = freeMarkerConfig.getTemplate(template.getTemplateFile());
        } catch (Throwable e) {
            String message = "An error occured while retrieving the FreeMarker template with id '"
                + template.getName() + "' from the FreeMarker configuration.";
            throw new CobiGenRuntimeException(message, e);
        }

        if (fmTemplate != null) {

            Map<String, Object> templateMethods = null;
            try {
                templateMethods = inputReader.getTemplateMethods(input);
            } catch (Throwable e) {
                throw new PluginProcessingException(e);
            }

            try {
                Environment env = fmTemplate.createProcessingEnvironment(model, out);
                env.setOutputEncoding(outputCharset);

                if (templateMethods != null) {
                    for (String key : templateMethods.keySet()) {
                        env.setVariable(key, (TemplateModel) templateMethods.get(key));
                    }
                }
                env.process();
            } catch (Throwable e) {
                throw new CobiGenRuntimeException(
                    "An error occured while processing FreeMarkers generation environment for generating the template with id '"
                        + template.getName() + "'.",
                    e);
            }
        }
    }

    /**
     * Returns the {@link Template} for a given {@link TemplateTo}
     *
     * @param templateTo
     *            which should be found as internal representation
     * @param triggerInterpreter
     *            to be used for variable resolving (for the final destination path)
     * @return the recovered {@link Template} object
     * @throws InvalidConfigurationException
     *             if at least one of the destination path variables could not be resolved
     */
    private Template getTemplate(TemplateTo templateTo, TriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {

        Trigger trigger =
            configurationHolder.readContextConfiguration().getTrigger(templateTo.getTriggerId());
        TemplatesConfiguration tConfig =
            configurationHolder.readTemplatesConfiguration(trigger, triggerInterpreter);
        Template template = tConfig.getTemplate(templateTo.getId());
        if (template == null) {
            throw new UnknownTemplateException(templateTo.getId());
        }
        return template;
    }

    /**
     * Determines the destination file and creates the path to the file if necessary
     *
     * @param relDestinationPath
     *            relative destination path from {@link ContextSetting#GenerationTargetRootPath}
     * @return the destination file (might not be existent)
     */
    private File getDestinationFile(String relDestinationPath) {

        String rootPath =
            configurationHolder.readContextConfiguration().get(ContextSetting.GenerationTargetRootPath);
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }

        String relDest = relDestinationPath;
        int i = relDest.lastIndexOf("/");
        String relFolderPath = relDest.substring(0, (i == -1) ? 0 : i);

        File folder = new File(rootPath + relFolderPath);
        folder.mkdirs();

        File originalFile = new File(rootPath + relDest);
        return originalFile;
    }
}
