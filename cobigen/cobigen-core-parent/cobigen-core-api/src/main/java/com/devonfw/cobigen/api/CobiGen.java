package com.capgemini.cobigen.api;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.annotation.ExceptionFacade;
import com.capgemini.cobigen.api.extension.ModelBuilder;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.api.to.GenerationReportTo;

/**
 * The {@link CobiGen} provides the API for generating Code/Files from FreeMarker templates.
 */
@ExceptionFacade
public interface CobiGen extends ConfigurationInterpreter, InputInterpreter {

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifacts
     *            a {@link List} of artifacts to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
        Path targetRootPath);

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifacts
     *            a {@link List} of artifacts to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
        Path targetRootPath, boolean forceOverride);

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifacts
     *            a {@link List} of artifacts to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
        Path targetRootPath, boolean forceOverride, List<Class<?>> logicClasses);

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifacts
     *            a {@link List} of artifacts to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @param rawModel
     *            externally adapted model to be used for generation.
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
        Path targetRootPath, boolean forceOverride, List<Class<?>> logicClasses, Map<String, Object> rawModel);

    /**
     * Generates code by processing the {@link GenerableArtifact} for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifact
     *            the artifact to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath);

    /**
     * Generates code by processing the {@link GenerableArtifact} for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifact
     *            the artifact to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
        boolean forceOverride);

    /**
     * Generates code by processing the {@link GenerableArtifact} for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifact
     *            the artifact to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
        boolean forceOverride, List<Class<?>> logicClasses);

    /**
     * Generates code by processing the {@link GenerableArtifact} for the given input.
     *
     * @param input
     *            generator input object
     * @param generableArtifact
     *            the artifact to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @param rawModel
     *            externally adapted model to be used for generation.
     * @return The {@link GenerationReportTo generation report} covering the actual status of success, a list
     *         of warnings, as well as a list of error messages.
     */
    public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
        boolean forceOverride, List<Class<?>> logicClasses, Map<String, Object> rawModel);

    /**
     * Returns a new {@link ModelBuilder} instance for the given input object. <i>Caution: this method will
     * retrieve the first matching trigger to resolve the input reader. Thus, in an environment with multiple
     * trigger with different input reader for the same data format, this method may end up in a
     * non-deterministic result. As long as there is no use case for that, this is neglectable.</i>
     *
     * @param generatorInput
     *            object, models should be created for
     * @return a new {@link ModelBuilder} instance
     */
    public ModelBuilder getModelBuilder(Object generatorInput);

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
}
