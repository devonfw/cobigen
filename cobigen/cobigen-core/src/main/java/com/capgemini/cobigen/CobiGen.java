package com.capgemini.cobigen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.capgemini.cobigen.config.ContextConfiguration;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.config.TemplatesConfiguration;
import com.capgemini.cobigen.config.entity.ContainerMatcher;
import com.capgemini.cobigen.config.entity.Increment;
import com.capgemini.cobigen.config.entity.Matcher;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.entity.io.AccumulationType;
import com.capgemini.cobigen.config.nio.NioFileSystemTemplateLoader;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.IModelBuilder;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.InputReaderV13;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.MatcherTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.model.JaxenXPathSupportNodeModel;
import com.capgemini.cobigen.model.ModelBuilder;
import com.capgemini.cobigen.model.ModelConverter;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.util.FileSystemUtil;
import com.capgemini.cobigen.validator.InputValidator;
import com.google.common.collect.Lists;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * The {@link CobiGen} provides the API for generating Code/Files from FreeMarker templates.
 *
 * @author mbrunnli (05.02.2013)
 */
public class CobiGen {

    /**
     * Assigning logger to CobiGen
     */
    private static final Logger LOG = LoggerFactory.getLogger(CobiGen.class);

    /**
     * Current version of the generation, needed for configuration file validation
     */
    public static final String CURRENT_VERSION = "2.1.0";

    /**
     * The {@link ContextConfiguration} for this instance
     */
    private ContextConfiguration contextConfiguration;

    /**
     * The FreeMarker configuration
     */
    private Configuration freeMarkerConfig;

    /**
     * Configuration folder of CobiGen.
     */
    private Path configFolder;

    /**
     * Creates a new {@link CobiGen} with a given {@link ContextConfiguration}.
     *
     * @param configFileOrFolder
     *            the root folder containing the context.xml and all templates, configurations etc.
     * @throws IOException
     *             if the {@link URI} points to a new {@link FileSystem} to be created (e.g. zip/jar)
     * @throws InvalidConfigurationException
     *             if the context configuration could not be read properly.
     * @author mbrunnli (05.02.2013)
     */
    public CobiGen(URI configFileOrFolder) throws InvalidConfigurationException, IOException {
        if (configFileOrFolder == null) {
            throw new IllegalArgumentException("The configuration file could not be null");
        }

        configFolder = FileSystemUtil.createFileSystemDependentPath(configFileOrFolder);
        contextConfiguration = new ContextConfiguration(configFolder);
        freeMarkerConfig = new Configuration();
        freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
        freeMarkerConfig.clearEncodingMap();
        freeMarkerConfig.setDefaultEncoding("UTF-8");
        freeMarkerConfig.setLocalizedLookup(false);
        freeMarkerConfig.setTemplateLoader(new NioFileSystemTemplateLoader(configFolder));
    }

    /**
     * Runs a generation for each template within the given increment to the destination specified by each
     * template according to its configuration.
     * @param input
     *            generator input object
     * @param increment
     *            to be used for generation
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration
     * @throws IOException
     *             if the output file could not be read or written
     * @throws TemplateException
     *             if an exception occurs during template processing by FreeMarker
     * @throws MergeException
     *             if an exception occurs during content merging
     * @author sbasnet (22.10.2014)
     */
    public void generate(Object input, IncrementTo increment, boolean forceOverride) throws IOException,
        TemplateException, MergeException {
        InputValidator.validateInputsUnequalNull(input, increment);
        for (TemplateTo t : increment.getTemplates()) {
            generate(input, t, forceOverride);
        }
    }

    /**
     * Generates code for the given input with the given template to the destination specified by the
     * templates configuration.
     *
     * @param input
     *            generator input object
     * @param template
     *            to be used for generation
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration
     * @throws IOException
     *             if the output file could not be read or written
     * @throws TemplateException
     *             if an exception occurs during template processing by FreeMarker
     * @throws MergeException
     *             if an exception occurs during content merging
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     * @author mbrunnli (08.04.2014)
     */
    public void generate(Object input, TemplateTo template, boolean forceOverride) throws IOException,
        TemplateException, MergeException {
        Trigger trigger = contextConfiguration.getTrigger(template.getTriggerId());
        ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
        generate(input, template, triggerInterpreter, forceOverride);
    }

    /**
     * Generates code for the given input with the given template and the given {@link ITriggerInterpreter} to
     * the destination specified by the templates configuration.
     *
     * @param generatorInput
     *            generator input object
     * @param template
     *            to be used for generation
     * @param triggerInterpreter
     *            {@link ITriggerInterpreter} to be used for reading the input and creating the model
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration
     * @throws IOException
     *             if the output file could not be read or written
     * @throws TemplateException
     *             if an exception occurs during template processing by FreeMarker
     * @throws MergeException
     *             if an exception occurs during content merging
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     * @author mbrunnli (06.02.2013) edited by trippl (05.03.2013)
     */
    private void generate(Object generatorInput, TemplateTo template, ITriggerInterpreter triggerInterpreter,
        boolean forceOverride) throws IOException, TemplateException, MergeException,
        InvalidConfigurationException {

        InputValidator.validateInputsUnequalNull(generatorInput, template);
        InputValidator.validateTriggerInterpreter(triggerInterpreter,
            contextConfiguration.getTrigger(template.getTriggerId()));

        generate(generatorInput, template, triggerInterpreter, forceOverride, null);
    }

    /**
     * Generates code for the given input with the given template and the given {@link ITriggerInterpreter} to
     * the destination specified by the templates configuration.
     *
     * @param input
     *            input object for the generation
     * @param template
     *            to be used for generation
     * @param triggerInterpreter
     *            {@link ITriggerInterpreter} to be used for reading the input and creating the model
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration
     * @param rawModel
     *            if provided by the user or <code>null</code> if not
     * @throws IOException
     *             if the output file could not be read or written
     * @throws TemplateException
     *             if an exception occurs during template processing by FreeMarker
     * @throws MergeException
     *             if an exception occurs during content merging
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     * @author mbrunnli (10.04.2014)
     */
    private void generate(Object input, TemplateTo template, ITriggerInterpreter triggerInterpreter,
        boolean forceOverride, Map<String, Object> rawModel) throws IOException, TemplateException {

        Trigger trigger = contextConfiguration.getTrigger(template.getTriggerId());
        ((NioFileSystemTemplateLoader) freeMarkerConfig.getTemplateLoader()).setTemplateRoot(configFolder
            .resolve(trigger.getTemplateFolder()));

        IInputReader inputReader = triggerInterpreter.getInputReader();
        List<Object> inputObjects = Lists.newArrayList(input);
        if (inputReader.combinesMultipleInputObjects(input)) {

            // check whether the inputs should be retieved recursively
            boolean retrieveInputsRecursively = false;
            if (inputReader instanceof InputReaderV13) {
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
            }
            if (retrieveInputsRecursively) {
                inputObjects =
                    ((InputReaderV13) inputReader).getInputObjectsRecursively(input,
                        trigger.getInputCharset());

            } else {
                inputObjects = inputReader.getInputObjects(input, trigger.getInputCharset());
            }

            Iterator<Object> it = inputObjects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                trigger = contextConfiguration.getTrigger(template.getTriggerId());
                if (!matches(next, trigger.getMatcher(), triggerInterpreter)) {
                    it.remove();
                }
            }
        }

        Template templateIntern = getTemplate(template, triggerInterpreter);
        for (Object targetInput : inputObjects) {
            Document model;
            if (rawModel == null) {
                model =
                    new ModelBuilder(targetInput, trigger, input)
                        .createModelAndConvertToDOM(triggerInterpreter);
            } else {
                model = new ModelConverter(rawModel).convertToDOM();
            }
            File originalFile = getDestinationFile(templateIntern.resolveDestinationPath(targetInput));
            String targetCharset = templateIntern.getTargetCharset();
            LOG.info("Generating template '{}' ...", templateIntern.getId(), targetInput);

            if (originalFile.exists()) {
                if (forceOverride || templateIntern.getMergeStrategy() == null) {
                    generateTemplateAndWriteFile(originalFile, templateIntern, model, targetCharset,
                        inputReader, input);
                } else {
                    try (Writer out = new StringWriter()) {
                        generateTemplateAndWritePatch(out, templateIntern, model, targetCharset, inputReader,
                            input);
                        String result = null;
                        try {
                            IMerger merger = PluginRegistry.getMerger(templateIntern.getMergeStrategy());
                            if (merger != null) {
                                result = merger.merge(originalFile, out.toString(), targetCharset);
                            } else {
                                throw new InvalidConfigurationException("No merger for merge strategy '"
                                    + templateIntern.getMergeStrategy() + "' found.");
                            }
                        } catch (Throwable e) {
                            LOG.error("An error occured while merging the file {}", originalFile.toURI(), e);
                            throw new MergeException("'An error occured while merging the file "
                                + originalFile.toURI() + "'!", e);
                        }

                        if (result != null) {
                            LOG.debug("Merge {} with charset {}.", originalFile.getName(), targetCharset);
                            FileUtils.writeStringToFile(originalFile, result, targetCharset);
                        }
                    }
                }
            } else {
                LOG.info("Create new File {} with charset {}.", originalFile.toURI(), targetCharset);
                generateTemplateAndWriteFile(originalFile, templateIntern, model, targetCharset, inputReader,
                    input);
            }
        }
    }

    /**
     * Generates code for the given input with the given template and the given {@link ITriggerInterpreter} to
     * the destination specified by the templates configuration.
     *
     * @param generatorInput
     *            input object for the generation
     * @param template
     *            to be used for generation
     * @param model
     *            to be used for generation
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration
     * @throws IOException
     *             if the output file could not be read or written
     * @throws TemplateException
     *             if an exception occurs during template processing by FreeMarker
     * @throws MergeException
     *             if an exception occurs during content merging
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     * @author mbrunnli (09.04.2014)
     */
    public void generate(Object generatorInput, TemplateTo template, Map<String, Object> model,
        boolean forceOverride) throws InvalidConfigurationException, IOException, TemplateException,
        MergeException {

        InputValidator.validateInputsUnequalNull(generatorInput, template, model);
        Trigger trigger = contextConfiguration.getTrigger(template.getTriggerId());
        ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
        generate(generatorInput, template, triggerInterpreter, forceOverride, model);
    }

    /**
     * Returns all matching trigger ids for a given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching trigger ids
     * @author mbrunnli (09.04.2014)
     */
    public List<String> getMatchingTriggerIds(Object matcherInput) {

        LOG.info("Matching trigger IDs requested.");
        List<String> matchingTriggerIds = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            matchingTriggerIds.add(trigger.getId());
        }
        LOG.info("{} matching trigger IDs found.", matchingTriggerIds.size());
        return matchingTriggerIds;
    }

    /**
     * Returns all matching increments for a given input object
     *
     * @param matcherInput
     *            object
     * @return this {@link List} of matching increments
     * @throws InvalidConfigurationException
     *             if the configuration of CobiGen is not valid
     * @author mbrunnli (09.04.2014)
     */
    public List<IncrementTo> getMatchingIncrements(Object matcherInput) throws InvalidConfigurationException {

        LOG.info("Matching increments requested.");
        List<IncrementTo> increments = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
            increments.addAll(convertIncrements(templatesConfiguration.getAllGenerationPackages(),
                templatesConfiguration.getTrigger(), templatesConfiguration.getTriggerInterpreter()));
        }
        LOG.info("{} matching increments found.", increments.size());
        return increments;
    }

    /**
     * Converts a {@link List} of {@link Increment}s with their parent {@link Trigger} to a {@link List} of
     * {@link IncrementTo}s
     *
     * @param increments
     *            the {@link List} of {@link Increment}s
     * @param trigger
     *            the parent {@link Trigger}
     * @return the {@link List} of {@link IncrementTo}s
     * @param triggerInterpreter
     *            {@link ITriggerInterpreter} the trigger has been interpreted with
     * @author mbrunnli (10.04.2014)
     */
    // TODO create ToConverter
    private List<IncrementTo> convertIncrements(List<Increment> increments, Trigger trigger,
        ITriggerInterpreter triggerInterpreter) {

        List<IncrementTo> incrementTos = Lists.newLinkedList();
        for (Increment increment : increments) {
            List<TemplateTo> templates = Lists.newLinkedList();
            for (Template template : increment.getTemplates()) {
                templates.add(new TemplateTo(template.getId(), template.getUnresolvedDestinationPath(),
                    template.getMergeStrategy(), trigger, triggerInterpreter));
            }
            incrementTos
                .add(new IncrementTo(increment.getId(), increment.getDescription(), trigger.getId(),
                    templates, convertIncrements(increment.getDependentIncrements(), trigger,
                        triggerInterpreter)));
        }
        return incrementTos;
    }

    /**
     * Returns all matching {@link Trigger}s for the given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching {@link Trigger}s
     * @author mbrunnli (09.04.2014)
     */
    private List<Trigger> getMatchingTriggers(Object matcherInput) {

        LOG.debug("Retrieve matching triggers.");
        List<Trigger> matchingTrigger = Lists.newLinkedList();
        for (Trigger trigger : contextConfiguration.getTriggers()) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
            LOG.debug("Check {} to match the input.", trigger);

            try {
                if (triggerInterpreter.getInputReader().isValidInput(matcherInput)) {
                    LOG.debug("Matcher input is marked as valid.");
                    boolean triggerMatches = matches(matcherInput, trigger.getMatcher(), triggerInterpreter);

                    // if a match has been found do not check container matchers in addition for performance
                    // issues.
                    if (!triggerMatches) {
                        LOG.debug("Check container matchers ...");
                        FOR_CONTAINERMATCHER:
                        for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
                            MatcherTo containerMatcherTo =
                                new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(),
                                    matcherInput);
                            LOG.debug("Check {} ...", containerMatcherTo);
                            if (triggerInterpreter.getMatcher().matches(containerMatcherTo)) {
                                LOG.debug("Match! Retrieve objects from container ...", containerMatcherTo);
                                // keep backward-compatibility
                                List<Object> containerResources;
                                if (triggerInterpreter.getInputReader() instanceof InputReaderV13
                                    && containerMatcher.isRetrieveObjectsRecursively()) {
                                    containerResources =
                                        ((InputReaderV13) triggerInterpreter.getInputReader())
                                            .getInputObjectsRecursively(matcherInput, Charsets.UTF_8);
                                } else {
                                    // the charset does not matter as we just want to see whether there is one
                                    // matcher for one of the container resources
                                    containerResources =
                                        triggerInterpreter.getInputReader().getInputObjects(matcherInput,
                                            Charsets.UTF_8);
                                }
                                LOG.debug("{} objects retrieved.", containerResources.size());

                                for (Object resource : containerResources) {
                                    if (matches(resource, trigger.getMatcher(), triggerInterpreter)) {
                                        LOG.debug("At least one object from container matches.");
                                        triggerMatches = true;
                                        break FOR_CONTAINERMATCHER;
                                    }
                                }
                            }
                        }
                    }
                    LOG.info("{} {}", trigger, triggerMatches ? "matches." : "does not match.");
                    if (triggerMatches) {
                        matchingTrigger.add(trigger);
                    }
                }
            } catch (Throwable e) {
                LOG.error("The TriggerInterpreter[type='{}'] exited abruptly!", triggerInterpreter.getType(),
                    e);
            }
        }
        return matchingTrigger;
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
    private boolean matches(Object matcherInput, List<Matcher> matcherList,
        ITriggerInterpreter triggerInterpreter) {
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
        LOG.info("Matcher declarations "
            + (matcherSetMatches ? "match the input." : "do not match the input."));
        return matcherSetMatches;
    }

    /**
     * Returns the {@link List} of matching templates for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching templates
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     * @author mbrunnli (09.04.2014)
     */
    public List<TemplateTo> getMatchingTemplates(Object matcherInput) throws InvalidConfigurationException {

        LOG.info("Matching templates requested.");
        List<TemplateTo> templates = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
            for (Template template : templatesConfiguration.getAllTemplates()) {
                templates.add(new TemplateTo(template.getId(), template.getUnresolvedDestinationPath(),
                    template.getMergeStrategy(), templatesConfiguration.getTrigger(), templatesConfiguration
                        .getTriggerInterpreter()));
            }
        }
        LOG.info("{} matching templates found.", templates.size());
        return templates;
    }

    /**
     * Returns the {@link List} of matching {@link TemplatesConfiguration}s for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching {@link TemplatesConfiguration}s
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     * @author mbrunnli (09.04.2014)
     */
    private List<TemplatesConfiguration> getMatchingTemplatesConfigurations(Object matcherInput)
        throws InvalidConfigurationException {

        LOG.debug("Retrieve matching template configurations.");
        List<TemplatesConfiguration> templateConfigurations = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter);

            TemplatesConfiguration templatesConfiguration =
                createTemplatesConfiguration(trigger, triggerInterpreter);
            if (templatesConfiguration != null) {
                templateConfigurations.add(templatesConfiguration);
            }
        }
        return templateConfigurations;
    }

    /**
     * Creates a new templates configuration while resolving the context variables dependent on the given
     * input. The context variables will be retrieved from the given {@link Trigger} resp.
     * {@link ITriggerInterpreter trigger interpreter}.
     * @param trigger
     *            to get matcher declarations from
     * @param triggerInterpreter
     *            to get the matcher implementation from
     * @return the {@link ContextConfiguration} for the given input or <code>null</code> if the context
     *         variables could not be resolved.
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     * @author mbrunnli (14.10.2014)
     */
    private TemplatesConfiguration createTemplatesConfiguration(Trigger trigger,
        ITriggerInterpreter triggerInterpreter) throws InvalidConfigurationException {
        return new TemplatesConfiguration(configFolder, trigger, triggerInterpreter);
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
     * @author mbrunnli (09.04.2014)
     */
    private Template getTemplate(TemplateTo templateTo, ITriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {

        Trigger trigger = contextConfiguration.getTrigger(templateTo.getTriggerId());
        TemplatesConfiguration tConfig = createTemplatesConfiguration(trigger, triggerInterpreter);
        Template template = tConfig.getTemplate(templateTo.getId());
        if (template == null) {
            throw new UnknownTemplateException("Unknown template with id=" + templateTo.getId()
                + ". Template could not be found in the configuration.");
        }
        return template;
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
     * @throws TemplateException
     *             if an exception occurs during template processing
     * @throws IOException
     *             if an I/O exception occurs (during writing to the writer)
     * @throws FileNotFoundException
     *             if the file exists but is a directory rather than a regular file, does not exist but cannot
     *             be created, or cannot be opened for any other reason
     * @author mbrunnli (21.03.2013)
     */
    private void generateTemplateAndWriteFile(File output, Template template, Document model,
        String outputCharset, IInputReader inputReader, Object input) throws FileNotFoundException,
        TemplateException, IOException {

        try (Writer out = new StringWriter()) {
            generateTemplateAndWritePatch(out, template, model, outputCharset, inputReader, input);
            FileUtils.writeStringToFile(output, out.toString(), outputCharset);
        }
    }

    /**
     * Determines the destination file and creates the path to the file if necessary
     *
     * @param relDestinationPath
     *            relative destination path from {@link ContextSetting#GenerationTargetRootPath}
     * @return the destination file (might not be existent)
     * @author mbrunnli (12.03.2013)
     */
    private File getDestinationFile(String relDestinationPath) {

        String rootPath = contextConfiguration.get(ContextSetting.GenerationTargetRootPath);
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
     * @throws TemplateException
     *             if an exception occurs during template processing
     * @throws IOException
     *             if an I/O exception occurs (during writing to the writer)
     * @author mbrunnli (12.03.2013)
     */
    private void generateTemplateAndWritePatch(Writer out, Template template, Document model,
        String outputCharset, IInputReader inputReader, Object input) throws TemplateException, IOException {

        freemarker.template.Template fmTemplate;
        try {
            fmTemplate = freeMarkerConfig.getTemplate(template.getTemplateFile());
        } catch (IOException e) {
            LOG.error("Error while retrieving template with id '{}'.", template.getId(), e);
            throw new IOException("Template " + template.getId() + ":\n" + e.getMessage(), e);
        }

        Environment env = fmTemplate.createProcessingEnvironment(model, out);
        env.setOutputEncoding(outputCharset);
        env.setCurrentVisitorNode(new JaxenXPathSupportNodeModel(model));

        Map<String, Object> templateMethods = inputReader.getTemplateMethods(input);
        if (templateMethods != null) {
            for (String key : templateMethods.keySet()) {
                env.setVariable(key, (TemplateModel) templateMethods.get(key));
            }
        }

        createModelShortcuts(model, env);
        env.process();
    }

    /**
     * Set a {@link ContextSetting}
     *
     * @param contextSetting
     *            {@link ContextSetting} to be set
     * @param value
     *            to be set
     * @author mbrunnli (09.04.2014)
     */
    public void setContextSetting(ContextSetting contextSetting, String value) {

        contextConfiguration.set(contextSetting, value);
    }

    /**
     * Returns the requested context setting
     *
     * @param contextSetting
     *            requested {@link ContextSetting}
     * @author mbrunnli (09.04.2014)
     */
    public void getContextSetting(ContextSetting contextSetting) {

        contextConfiguration.get(contextSetting);
    }

    /**
     * Creates shortcuts for accessing the model in a more comfortable manner. All top level children of the
     * root node will get a shortcut in the target environment
     *
     * @param doc
     *            {@link Document} to add the shortcuts for
     * @param env
     *            target environment
     * @author mbrunnli (08.04.2014)
     */
    private void createModelShortcuts(Document doc, Environment env) {

        NodeList nodeList = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            env.setVariable(child.getNodeName(), new JaxenXPathSupportNodeModel(child));
        }
    }

    /**
     * Returns a new {@link IModelBuilder} instance for the given input object and its matching trigger id
     *
     * @param generatorInput
     *            object, models should be created for
     * @param triggerId
     *            which has been activated by the given object
     * @return a new {@link IModelBuilder} instance
     * @author mbrunnli (09.04.2014)
     */
    public IModelBuilder getModelBuilder(Object generatorInput, String triggerId) {

        Trigger trigger = contextConfiguration.getTrigger(triggerId);
        if (trigger == null) {
            throw new IllegalArgumentException("Unknown Trigger with id '" + triggerId + "'.");
        }
        return new ModelBuilder(generatorInput, trigger, null);
    }

    /**
     * Returns a new {@link IModelBuilder} instance for the given input object and its matching trigger id
     *
     * @param generatorInput
     *            object, models should be created for
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @param triggerId
     *            which has been activated by the given object
     * @return a new {@link IModelBuilder} instance
     * @author mbrunnli (09.04.2014)
     */
    public IModelBuilder getModelBuilder(Object generatorInput, Object matcherInput, String triggerId) {

        Trigger trigger = contextConfiguration.getTrigger(triggerId);
        if (trigger == null) {
            throw new IllegalArgumentException("Unknown Trigger with id '" + triggerId + "'.");
        }
        return new ModelBuilder(generatorInput, trigger, matcherInput);
    }

    /**
     * Checks whether there is at least one input reader, which interprets the given input as combined input.
     * @param input
     *            object
     * @return <code>true</code> if there is at least one input reader, which interprets the given input as
     *         combined input,<code>false</code>, otherwise
     * @author mbrunnli (03.12.2014)
     */
    public boolean combinesMultipleInputs(Object input) {
        List<Trigger> matchingTriggers = getMatchingTriggers(input);
        for (Trigger trigger : matchingTriggers) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            return triggerInterpreter.getInputReader().combinesMultipleInputObjects(input);
        }
        return false;
    }

    /**
     * Reloads the configuration from source. This function might be called if the configuration file has
     * changed in a running system
     *
     * @throws InvalidConfigurationException
     *             thrown if the {@link File} is not valid with respect to the context.xsd
     * @author sbasnet (15.04.2014)
     */
    public void reloadContextConfigurationFromFile() throws InvalidConfigurationException {

        contextConfiguration.reloadConfigurationFromFile(configFolder);
    }

}
