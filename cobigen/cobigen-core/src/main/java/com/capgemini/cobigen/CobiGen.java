/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.IModelBuilder;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.MatcherTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.model.JaxenXPathSupportNodeModel;
import com.capgemini.cobigen.model.ModelBuilder;
import com.capgemini.cobigen.model.ModelConverter;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.util.SystemUtil;
import com.capgemini.cobigen.validator.InputValidator;
import com.google.common.collect.Lists;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;

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
    public static final String CURRENT_VERSION = "1.0.0";

    /**
     * The {@link ContextConfiguration} for this instance
     */
    private ContextConfiguration contextConfiguration;

    /**
     * The FreeMarker configuration
     */
    private Configuration freeMarkerConfig;

    /**
     * Creates a new {@link CobiGen} with a given {@link ContextConfiguration}. Beside the
     * {@link ContextSetting#GeneratorProjectRootPath} all context variables can be changed during runtime
     * affecting the generation mechanisms.
     *
     * @param rootConfigFolder
     *            the root folder containing the context.xml and all templates, configurations etc.
     * @throws IOException
     *             if the configured {@link ContextSetting#GeneratorProjectRootPath} cannot be accessed
     * @author mbrunnli (05.02.2013)
     */
    public CobiGen(File rootConfigFolder) throws IOException {

        contextConfiguration = new ContextConfiguration(rootConfigFolder);
        freeMarkerConfig = new Configuration();
        freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
        freeMarkerConfig.clearEncodingMap();
        freeMarkerConfig.setDefaultEncoding("UTF-8");

        // ClasspathScanner.scanClasspathAndRegisterPlugins(); //TODO implement
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
        boolean forceOverride, Map<String, Object> rawModel) throws IOException, TemplateException,
        MergeException {

        Trigger trigger = contextConfiguration.getTrigger(template.getTriggerId());
        freeMarkerConfig.setDirectoryForTemplateLoading(new File(contextConfiguration
            .get(ContextSetting.GeneratorProjectRootPath)
            + SystemUtil.FILE_SEPARATOR
            + trigger.getTemplateFolder()));

        IInputReader inputReader = triggerInterpreter.getInputReader();
        List<Object> inputObjects = Lists.newArrayList(input);
        if (inputReader.combinesMultipleInputObjects(input)) {
            inputObjects = inputReader.getInputObjects(input, trigger.getInputCharset());
            Iterator<Object> it = inputObjects.iterator();
            InputObjectsLoop:
            while (it.hasNext()) {
                Object next = it.next();
                trigger = contextConfiguration.getTrigger(template.getTriggerId());
                for (Matcher m : trigger.getMatcher()) {
                    if (triggerInterpreter.getMatcher().matches(
                        new MatcherTo(m.getType(), m.getValue(), next))) {
                        continue InputObjectsLoop;
                    }
                }
                // InputObjectsLoop has not been continued --> object does not match (remove it)
                it.remove();
            }
        }

        Template templateIntern = getTemplate(template, triggerInterpreter, input);
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
            LOG.info("Generating template '{}' with input '{}' ...", templateIntern.getId(), targetInput);

            if (originalFile.exists()) {
                if (forceOverride || templateIntern.getMergeStrategy() == null) {
                    generateTemplateAndWriteFile(originalFile, templateIntern, model, targetCharset);
                } else {
                    try (Writer out = new StringWriter()) {
                        generateTemplateAndWritePatch(out, templateIntern, model, targetCharset);
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
                            LOG.error("Fehler beim Mergen der Datei {}", originalFile.getName(), e);
                            throw new MergeException("Fehler beim Mergen der Datei " + originalFile.getName()
                                + ":\n" + e.getMessage());
                        }

                        if (result != null) {
                            LOG.debug("Merge {} with charset {}", originalFile.getName(), targetCharset);
                            FileUtils.writeStringToFile(originalFile, result, targetCharset);
                        }
                    }
                }
            } else {
                LOG.info("Create new File {} with charset {}", originalFile.getName(), targetCharset);
                generateTemplateAndWriteFile(originalFile, templateIntern, model, targetCharset);
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

        List<String> matchingTrigger = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            matchingTrigger.add(trigger.getId());
        }
        return matchingTrigger;
    }

    /**
     * Returns all matching increments for a given input object
     *
     * @param matcherInput
     *            object
     * @return this {@link List} of matching increments
     * @author mbrunnli (09.04.2014)
     */
    public List<IncrementTo> getMatchingIncrements(Object matcherInput) {

        List<IncrementTo> increments = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
            increments.addAll(convertIncrements(templatesConfiguration.getAllGenerationPackages(),
                templatesConfiguration.getTrigger(), templatesConfiguration.getTriggerInterpreter()));
        }
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

        List<Trigger> matchingTrigger = Lists.newLinkedList();
        for (Trigger trigger : contextConfiguration.getTriggers()) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);

            try {
                if (triggerInterpreter.getInputReader().isValidInput(matcherInput)) {
                    for (Matcher matcher : trigger.getMatcher()) {
                        MatcherTo matcherTo =
                            new MatcherTo(matcher.getType(), matcher.getValue(), matcherInput);
                        if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                            matchingTrigger.add(trigger);
                            break;
                        }
                    }

                    // if a match has been found do not check container matchers in addition for performance
                    // issues.
                    if (matchingTrigger.isEmpty()) {
                        FOR_CONTAINERMATCHER:
                        for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
                            MatcherTo matcherTo =
                                new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(),
                                    matcherInput);
                            if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                                // the charset does not matter as we only want to see whether there is one
                                // matcher for one of the container resources
                                List<Object> containerResources =
                                    triggerInterpreter.getInputReader().getInputObjects(matcherInput,
                                        Charsets.UTF_8);
                                for (Matcher matcher : trigger.getMatcher()) {
                                    for (Object resource : containerResources) {
                                        matcherTo =
                                            new MatcherTo(matcher.getType(), matcher.getValue(), resource);
                                        if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                                            matchingTrigger.add(trigger);
                                            break FOR_CONTAINERMATCHER;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                LOG.error("The TriggerInterpreter for type '{}' exited abruptly",
                    triggerInterpreter.getType(), e);
            }
        }
        return matchingTrigger;
    }

    /**
     * Returns the {@link List} of matching templates for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching templates
     * @author mbrunnli (09.04.2014)
     */
    public List<TemplateTo> getMatchingTemplates(Object matcherInput) {

        List<TemplateTo> templates = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
            for (Template template : templatesConfiguration.getAllTemplates()) {
                templates.add(new TemplateTo(template.getId(), template.getUnresolvedDestinationPath(),
                    template.getMergeStrategy(), templatesConfiguration.getTrigger(), templatesConfiguration
                        .getTriggerInterpreter()));
            }
        }
        return templates;
    }

    /**
     * Returns the {@link List} of matching {@link TemplatesConfiguration}s for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching {@link TemplatesConfiguration}s
     * @author mbrunnli (09.04.2014)
     */
    private List<TemplatesConfiguration> getMatchingTemplatesConfigurations(Object matcherInput) {

        List<TemplatesConfiguration> templateConfigurations = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter);

            IInputReader inputReader = triggerInterpreter.getInputReader();
            if (inputReader.combinesMultipleInputObjects(matcherInput)) {
                List<Object> containerChildren = inputReader.getInputObjects(matcherInput, Charsets.UTF_8);
                for (Object child : containerChildren) {
                    TemplatesConfiguration childTemplatesConfiguration =
                        createTemplatesConfiguration(child, trigger, triggerInterpreter);
                    if (childTemplatesConfiguration != null) {
                        templateConfigurations.add(childTemplatesConfiguration);
                    }
                }
            } else {
                TemplatesConfiguration childTemplatesConfiguration =
                    createTemplatesConfiguration(matcherInput, trigger, triggerInterpreter);
                if (childTemplatesConfiguration != null) {
                    templateConfigurations.add(childTemplatesConfiguration);
                }
            }
        }
        return templateConfigurations;
    }

    /**
     * Creates a new templates configuration while resolving the context variables dependend on the given
     * input. The context variables will be retrieved from the given {@link Trigger} resp.
     * {@link ITriggerInterpreter trigger interpreter}.
     * @param input
     *            to derive the context variables from
     * @param trigger
     *            to get matcher declarations from
     * @param triggerInterpreter
     *            to get the matcher implementation from
     * @return the {@link ContextConfiguration} for the given input or <code>null</code> if the context
     *         variables could not be resolved.
     * @author mbrunnli (14.10.2014)
     */
    private TemplatesConfiguration createTemplatesConfiguration(Object input, Trigger trigger,
        ITriggerInterpreter triggerInterpreter) {
        File templatesConfigurationFolder =
            new File(contextConfiguration.get(ContextSetting.GeneratorProjectRootPath)
                + SystemUtil.FILE_SEPARATOR + trigger.getTemplateFolder());
        return new TemplatesConfiguration(templatesConfigurationFolder, trigger, triggerInterpreter);
    }

    /**
     * Returns the {@link Template} for a given {@link TemplateTo}
     *
     * @param templateTo
     *            which should be found as internal representation
     * @param triggerInterpreter
     *            to be used for variable resolving (for the final destination path)
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the recovered {@link Template} object
     * @throws InvalidConfigurationException
     *             if at least one of the destination path variables could not be resolved
     * @author mbrunnli (09.04.2014)
     */
    private Template getTemplate(TemplateTo templateTo, ITriggerInterpreter triggerInterpreter,
        Object matcherInput) throws InvalidConfigurationException {

        Trigger trigger = contextConfiguration.getTrigger(templateTo.getTriggerId());
        File templatesConfigurationFolder =
            new File(contextConfiguration.get(ContextSetting.GeneratorProjectRootPath)
                + SystemUtil.FILE_SEPARATOR + trigger.getTemplateFolder());

        TemplatesConfiguration tConfig =
            new TemplatesConfiguration(templatesConfigurationFolder, trigger, triggerInterpreter);
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
        String outputCharset) throws FileNotFoundException, TemplateException, IOException {

        try (Writer out = new StringWriter()) {
            generateTemplateAndWritePatch(out, template, model, outputCharset);
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
     * @param outputCharset
     *            charset the target file should be written with
     * @throws TemplateException
     *             if an exception occurs during template processing
     * @throws IOException
     *             if an I/O exception occurs (during writing to the writer)
     * @author mbrunnli (12.03.2013)
     */
    private void generateTemplateAndWritePatch(Writer out, Template template, Document model,
        String outputCharset) throws TemplateException, IOException {

        freemarker.template.Template fmTemplate;
        try {
            fmTemplate = freeMarkerConfig.getTemplate(template.getTemplateFile());
        } catch (IOException e) {
            LOG.error("Error while retrieving template with id '{}'", template.getId(), e);
            throw new IOException("Template " + template.getId() + ":\n" + e.getMessage(), e);
        }

        Environment env = fmTemplate.createProcessingEnvironment(model, out);
        env.setOutputEncoding(outputCharset);
        env.setCurrentVisitorNode(new JaxenXPathSupportNodeModel(model));
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
     * Reloads the configuration from source. This function might be called if the configuration file has
     * changed in a running system
     *
     * @throws IOException
     *             if the file could not be accessed
     * @throws InvalidConfigurationException
     *             thrown if the {@link File} is not valid with respect to the context.xsd
     * @author sbasnet (15.04.2014)
     */
    public void reloadContextConfigurationFromFile() throws IOException, InvalidConfigurationException {

        contextConfiguration.reloadConfigurationFromFile();
    }

}
