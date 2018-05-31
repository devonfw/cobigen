package com.capgemini.cobigen.impl.generator.api;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.api.to.GenerationReportTo;

/** Class handling the actual code generation logic. */
public interface GenerationProcessor {

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input. This
     * method/class is NOT thread-safe.
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
     * @return {@link GenerationReportTo the GenerationReport}
     */
    public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
        Path targetRootPath, boolean forceOverride, List<Class<?>> logicClasses, Map<String, Object> rawModel);
}
