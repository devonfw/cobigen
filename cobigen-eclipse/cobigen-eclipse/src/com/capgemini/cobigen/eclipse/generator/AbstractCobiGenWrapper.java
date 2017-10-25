package com.capgemini.cobigen.eclipse.generator;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;

/** Common wrapper holding the cobigen instance. */
public abstract class AbstractCobiGenWrapper {

    /** Target Project for the generation */
    private IProject targetProject;

    /** Reference to native {@link CobiGen} API */
    protected CobiGen cobiGen;

    /**
     * Creates a new generator instance
     * @param cobiGen
     *            initialized {@link CobiGen} instance
     * @param inputSourceProject
     *            project the input files have been selected from
     *
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public AbstractCobiGenWrapper(CobiGen cobiGen, IProject inputSourceProject)
        throws GeneratorProjectNotExistentException, InvalidConfigurationException {
        targetProject = inputSourceProject;
        this.cobiGen = cobiGen;
    }

    /**
     * @return the generation target project
     */
    public IProject getGenerationTargetProject() {

        return targetProject;
    }

    /**
     * @return the generation target project
     */
    public Path getGenerationTargetProjectPath() {

        return Paths.get(targetProject.getProject().getLocationURI());
    }
}
