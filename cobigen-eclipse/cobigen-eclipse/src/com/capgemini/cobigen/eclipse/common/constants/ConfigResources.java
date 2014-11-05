package com.capgemini.cobigen.eclipse.common.constants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;

/**
 * This class encloses all relevant resource functionality for the generator configuration
 *
 * @author mbrunnli (08.04.2013)
 */
public class ConfigResources {

    /**
     * Generator Configuration Project Name
     */
    public static final String CONFIG_PROJECT_NAME = "CobiGen_Templates";

    /**
     * Custom Batches configuration file
     */
    public static final String CUSTOM_BATCHES_ROOT_CONFIG = "customBatches.xml";

    /**
     * Returns the generator configuration project if it exists. If the project is closed, the project will be
     * opened automatically
     * @return the generator configuration {@link IProject}
     * @throws GeneratorProjectNotExistentException
     *             if no generator configuration project called {@value #CONFIG_PROJECT_NAME} exists
     * @throws CoreException
     *             if an existing generator configuration project could not be opened
     * @author mbrunnli (08.04.2013)
     */
    public static IProject getGeneratorConfigurationProject() throws GeneratorProjectNotExistentException,
        CoreException {

        IProject generatorProj =
            ResourcesPlugin.getWorkspace().getRoot().getProject(ConfigResources.CONFIG_PROJECT_NAME);
        if (!generatorProj.exists()) {
            throw new GeneratorProjectNotExistentException();
        }
        if (!generatorProj.isOpen()) {
            generatorProj.open(new NullProgressMonitor());
        }

        return generatorProj;
    }
}
