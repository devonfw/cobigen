package com.devonfw.cobigen.eclipse.generator;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;

/** Common wrapper holding the cobigen instance. */
public abstract class AbstractCobiGenWrapper {

  /** Target Project for the generation */
  private IProject targetProject;

  /** Reference to native {@link CobiGen} API */
  protected CobiGen cobiGen;

  /**
   * Creates a new generator instance
   *
   * @param cobiGen initialized {@link CobiGen} instance
   * @param inputSourceProject project the input files have been selected from
   *
   * @throws GeneratorProjectNotExistentException if the generator configuration project "RF-Generation" is not existent
   * @throws InvalidConfigurationException if the context configuration is not valid
   */
  public AbstractCobiGenWrapper(CobiGen cobiGen, IProject inputSourceProject)
      throws GeneratorProjectNotExistentException, InvalidConfigurationException {

    this.targetProject = inputSourceProject;
    this.cobiGen = cobiGen;
  }

  /**
   * @return the generation target project
   */
  public IProject getGenerationTargetProject() {

    return this.targetProject;
  }

  /**
   * @return the generation target project
   */
  public Path getGenerationTargetProjectPath() {

    return Paths.get(this.targetProject.getProject().getLocationURI());
  }
}
