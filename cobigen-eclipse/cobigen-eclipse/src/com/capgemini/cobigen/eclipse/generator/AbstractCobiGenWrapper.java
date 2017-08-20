package com.capgemini.cobigen.eclipse.generator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.ResourcesPluginUtil;
import com.capgemini.cobigen.impl.CobiGenFactory;

/**
 * Common wrapper holding the cobigen instance.
 */
public abstract class AbstractCobiGenWrapper {

    /**
     * Target Project for the generation
     */
    private IProject targetProject;

    /**
     * Reference to native {@link CobiGen} API
     */
    protected CobiGen cobiGen;

    /**
     * Creates a new generator instance
     * @param inputSourceProject
     *            project the input files have been selected from
     *
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws IOException
     *             if the generator project could not be found or read
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public AbstractCobiGenWrapper(IProject inputSourceProject)
        throws GeneratorProjectNotExistentException, CoreException, InvalidConfigurationException, IOException {
        cobiGen = initializeGenerator();
        targetProject = inputSourceProject;
    }

    /**
     * Initializes the {@link CobiGen} with the correct configuration
     *
     * @return the configured{@link CobiGen}
     * @throws CoreException
     *             if an internal eclipse exception occurs
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration folder does not exist
     * @throws IOException
     *             if the generator project could not be found or read
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    private CobiGen initializeGenerator()
        throws GeneratorProjectNotExistentException, CoreException, InvalidConfigurationException, IOException {

        ResourcesPluginUtil.refreshConfigurationProject();
        IProject generatorProj = ResourcesPluginUtil.getGeneratorConfigurationProject();
        return CobiGenFactory.create(generatorProj.getLocationURI());
    }

    /**
     * Returns the generation target project
     *
     * @return the generation target project
     */
    public IProject getGenerationTargetProject() {

        return targetProject;
    }

    /**
     * Returns the generation target project
     *
     * @return the generation target project
     */
    public Path getGenerationTargetProjectPath() {

        return Paths.get(targetProject.getProject().getLocationURI());
    }
}
