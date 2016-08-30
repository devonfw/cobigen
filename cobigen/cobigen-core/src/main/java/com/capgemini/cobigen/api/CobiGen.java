package com.capgemini.cobigen.api;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.ModelBuilder;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.impl.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.impl.exceptions.InvalidConfigurationException;

/**
 * The {@link CobiGen} provides the API for generating Code/Files from FreeMarker templates.
 */
public interface CobiGen extends ConfigurationInterpreter {

    /**
     * @see #generate(Object, List, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, List<GenerableArtifact> generableArtifacts);

    /**
     * @see #generate(Object, List, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride);

    /**
     * @see #generate(Object, List, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, List<GenerableArtifact> generableArtifacts, boolean forceOverride,
        List<Class<?>> logicClasses);

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
        List<Class<?>> logicClasses, Map<String, Object> additionalModelValues);

    /**
     * @see #generate(Object, GenerableArtifact, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, GenerableArtifact generableArtifact);

    /**
     * @see #generate(Object, GenerableArtifact, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride);

    /**
     * @see #generate(Object, GenerableArtifact, boolean, List, Map)
     */
    @SuppressWarnings("javadoc")
    public void generate(Object input, GenerableArtifact generableArtifact, boolean forceOverride,
        List<Class<?>> logicClasses);

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
        List<Class<?>> logicClasses, Map<String, Object> additionalModelValues);

    /**
     * Set a {@link ContextSetting}
     *
     * @param contextSetting
     *            {@link ContextSetting} to be set
     * @param value
     *            to be set
     */
    public void setContextSetting(ContextSetting contextSetting, String value);

    /**
     * Returns the requested context setting
     *
     * @param contextSetting
     *            requested {@link ContextSetting}
     */
    public void getContextSetting(ContextSetting contextSetting);

    /**
     * Returns a new {@link ModelBuilder} instance for the given input object and its matching trigger id
     *
     * @param generatorInput
     *            object, models should be created for
     * @param triggerId
     *            which has been activated by the given object
     * @return a new {@link ModelBuilder} instance
     */
    public ModelBuilder getModelBuilder(Object generatorInput, String triggerId);

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
     */
    public ModelBuilder getModelBuilder(Object generatorInput, Object matcherInput, String triggerId);
}
