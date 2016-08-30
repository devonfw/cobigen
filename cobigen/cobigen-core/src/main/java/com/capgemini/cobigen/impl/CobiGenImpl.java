package com.capgemini.cobigen.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.extension.ModelBuilder;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.capgemini.cobigen.impl.config.ContextConfiguration;
import com.capgemini.cobigen.impl.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.model.ModelBuilderImpl;
import com.google.common.collect.Lists;

import freemarker.template.Configuration;

public class CobiGenImpl implements CobiGen {

    /** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(CobiGen.class);

    /** The FreeMarker configuration */
    private Configuration freeMarkerConfig;

    /** ConfigurationHolder folder of CobiGen. */
    private Path configFolder;

    /** CobiGen Configuration Cache */
    private ConfigurationHolder configurationHolder;

    /**
     * Creates a new {@link CobiGen} with a given {@link ContextConfiguration}.
     *
     * @param configFileOrFolder
     *            the root folder containing the context.xml and all templates, configurations etc.
     * @throws IOException
     *             if the {@link URI} points to a file or folder, which could not be read.
     * @throws InvalidConfigurationException
     *             if the context configuration could not be read properly.
     */
    public CobiGenImpl(Path configFileOrFolder, Configuration templateEngineConfiguration,
        ConfigurationHolder configurationHolder) throws InvalidConfigurationException, IOException {
        configFolder = configFileOrFolder;
        freeMarkerConfig = templateEngineConfiguration;
        configFolder = configFileOrFolder;
    }

    /**
     * @see #generate(Object, List, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, List<GenerableArtifact> generableArtifacts) {
        generate(input, generableArtifacts, false, null, null);
    }

    /**
     * @see #generate(Object, List, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride) {
        generate(input, generableArtifacts, forceOverride, null, null);
    }

    /**
     * @see #generate(Object, List, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride,
        List<Class<?>> logicClasses) {
        generate(input, generableArtifacts, forceOverride, logicClasses, null);
    }

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     *
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
     * @param additionalModelValues
     *            additional template model values, which will be merged with the model created by CobiGen.
     *            Thus, it might be possible to enrich the model by additional values or even overwrite model
     *            values for generation externally.
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     */
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride,
        List<Class<?>> logicClasses, Map<String, Object> additionalModelValues) {
        GenerationProcessor gp = new GenerationProcessor(configurationHolder, freeMarkerConfig);
        gp.generate(input, generableArtifacts, forceOverride, logicClasses, additionalModelValues);
    }

    /**
     * @see #generate(Object, GenerableArtifact, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, GenerableArtifact generableArtifact) {
        generate(input, generableArtifact, false, null, null);
    }

    /**
     * @see #generate(Object, GenerableArtifact, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride) {
        generate(input, generableArtifact, forceOverride, null, null);
    }

    /**
     * @see #generate(Object, GenerableArtifact, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride,
        List<Class<?>> logicClasses) {
        generate(input, generableArtifact, forceOverride, logicClasses, null);
    }

    /**
     * Generates code by processing the {@link GenerableArtifact} for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifact
     *            the artifact to be generated
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @param additionalModelValues
     *            additional template model values, which will be merged with the model created by CobiGen.
     *            Thus, it might be possible to enrich the model by additional values or even overwrite model
     *            values for generation externally.
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     */
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride,
        List<Class<?>> logicClasses, Map<String, Object> additionalModelValues) {
        GenerationProcessor gp = new GenerationProcessor(configurationHolder, freeMarkerConfig);
        gp.generate(input, Lists.newArrayList(generableArtifact), forceOverride, logicClasses,
            additionalModelValues);
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

        configurationHolder.readContextConfiguration().set(contextSetting, value);
    }

    /**
     * Returns the requested context setting
     *
     * @param contextSetting
     *            requested {@link ContextSetting}
     * @author mbrunnli (09.04.2014)
     */
    public void getContextSetting(ContextSetting contextSetting) {

        configurationHolder.readContextConfiguration().get(contextSetting);
    }

    /**
     * Returns a new {@link ModelBuilder} instance for the given input object and its matching trigger id
     *
     * @param generatorInput
     *            object, models should be created for
     * @param triggerId
     *            which has been activated by the given object
     * @return a new {@link ModelBuilder} instance
     * @author mbrunnli (09.04.2014)
     */
    public ModelBuilder getModelBuilder(Object generatorInput, String triggerId) {

        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(triggerId);
        if (trigger == null) {
            throw new IllegalArgumentException("Unknown Trigger with id '" + triggerId + "'.");
        }
        return new ModelBuilderImpl(generatorInput, trigger, null);
    }

    /**
     * Returns a new {@link ModelBuilder} instance for the given input object and its matching trigger id
     *
     * @param generatorInput
     *            object, models should be created for
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @param triggerId
     *            which has been activated by the given object
     * @return a new {@link ModelBuilder} instance
     * @author mbrunnli (09.04.2014)
     */
    public ModelBuilder getModelBuilder(Object generatorInput, Object matcherInput, String triggerId) {

        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(triggerId);
        if (trigger == null) {
            throw new IllegalArgumentException("Unknown Trigger with id '" + triggerId + "'.");
        }
        return new ModelBuilderImpl(generatorInput, trigger, matcherInput);
    }
}
