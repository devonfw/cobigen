package com.capgemini.cobigen.impl;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.ConfigurationInterpreter;
import com.capgemini.cobigen.api.extension.ModelBuilder;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.annotation.ProxyFactory;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.capgemini.cobigen.impl.config.ContextConfiguration;
import com.capgemini.cobigen.impl.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.impl.model.ModelBuilderImpl;
import com.google.common.collect.Lists;

import freemarker.template.Configuration;

/**
 * Implementation of the CobiGen API.
 */
public class CobiGenImpl implements CobiGen {

    /** The FreeMarker configuration */
    private Configuration freeMarkerConfig;

    /** CobiGen Configuration Cache */
    private ConfigurationHolder configurationHolder;

    /**
     * {@link ConfigurationInterpreter} which holds a cache to improve performance for multiple requests for
     * the same input
     */
    private ConfigurationInterpreter configurationInterpreter;

    /**
     * Creates a new {@link CobiGen} with a given {@link ContextConfiguration}.
     *
     * @param templateEngineConfiguration
     *            Configuration of the template Engine.
     * @param configurationHolder
     *            {@link ConfigurationHolder} holding CobiGen's configuration
     */
    public CobiGenImpl(Configuration templateEngineConfiguration, ConfigurationHolder configurationHolder) {
        freeMarkerConfig = templateEngineConfiguration;
        this.configurationHolder = configurationHolder;

        // Create proxy cached proxy of ConfigurationInterpreter to cache method calls
        ConfigurationInterpreterImpl impl = new ConfigurationInterpreterImpl(configurationHolder);
        configurationInterpreter = ProxyFactory.getProxy(impl);
    }

    @Override
    public void generate(Object input, List<GenerableArtifact> generableArtifacts) {
        generate(input, generableArtifacts, false, null, null);
    }

    @Override
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride) {
        generate(input, generableArtifacts, forceOverride, null, null);
    }

    @Override
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride,
        List<Class<?>> logicClasses) {
        generate(input, generableArtifacts, forceOverride, logicClasses, null);
    }

    @Override
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride,
        List<Class<?>> logicClasses, Map<String, Object> additionalModelValues) {
        GenerationProcessor gp = new GenerationProcessor(configurationHolder, freeMarkerConfig, input,
            generableArtifacts, forceOverride, logicClasses, additionalModelValues);
        gp.generate();
    }

    @Override
    public void generate(Object input, GenerableArtifact generableArtifact) {
        generate(input, generableArtifact, false, null, null);
    }

    @Override
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride) {
        generate(input, generableArtifact, forceOverride, null, null);
    }

    @Override
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride,
        List<Class<?>> logicClasses) {
        generate(input, generableArtifact, forceOverride, logicClasses, null);
    }

    @Override
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride,
        List<Class<?>> logicClasses, Map<String, Object> additionalModelValues) {
        GenerationProcessor gp = new GenerationProcessor(configurationHolder, freeMarkerConfig, input,
            Lists.newArrayList(generableArtifact), forceOverride, logicClasses, additionalModelValues);
        gp.generate();
    }

    @Override
    public void setContextSetting(ContextSetting contextSetting, String value) {
        configurationHolder.readContextConfiguration().set(contextSetting, value);
    }

    @Override
    public void getContextSetting(ContextSetting contextSetting) {
        configurationHolder.readContextConfiguration().get(contextSetting);
    }

    @Override
    public ModelBuilder getModelBuilder(Object generatorInput, String triggerId) {

        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(triggerId);
        if (trigger == null) {
            throw new IllegalArgumentException("Unknown Trigger with id '" + triggerId + "'.");
        }
        return new ModelBuilderImpl(generatorInput, trigger);
    }

    @Override
    public boolean combinesMultipleInputs(Object input) {
        return configurationInterpreter.combinesMultipleInputs(input);
    }

    @Override
    public List<IncrementTo> getMatchingIncrements(Object matcherInput) throws InvalidConfigurationException {
        return configurationInterpreter.getMatchingIncrements(matcherInput);
    }

    @Override
    public List<TemplateTo> getMatchingTemplates(Object matcherInput) throws InvalidConfigurationException {
        return configurationInterpreter.getMatchingTemplates(matcherInput);
    }

    @Override
    public List<String> getMatchingTriggerIds(Object matcherInput) {
        return configurationInterpreter.getMatchingTriggerIds(matcherInput);
    }
}
