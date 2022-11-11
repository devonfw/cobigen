package com.devonfw.cobigen.impl.generator.api;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.GenerationReportTo;

/** Class handling the actual code generation logic. */
public interface GenerationProcessor {

  /**
   * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input. This method/class
   * is NOT thread-safe.
   *
   * @param input generator input object
   * @param generableArtifacts a {@link List} of artifacts to be generated
   * @param targetRootPath target root path to generate to (to be used to resolve the dependent template destination
   *        paths)
   * @param forceOverride if <code>true</code> and the destination path is already existent, the contents will be
   *        overwritten by the generated ones iff there is no merge strategy defined by the templates configuration.
   *        (default: {@code false})
   * @param rawModel externally adapted model to be used for generation.
   * @param progressCallback expects the progress in percent as Integer
   * @return {@link GenerationReportTo the GenerationReport}
   */
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride, Map<String, Object> rawModel,
      BiConsumer<String, Integer> progressCallback);
}
