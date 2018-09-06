package com.devonfw.cobigen.eclipse.common.tools;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;

/** Util for NPE save access of {@link ResourcesPlugin} utils */
public class ResourcesPluginUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesPluginUtil.class);

    /**
     * Refreshes the configuration project from the file system.
     */
    public static void refreshConfigurationProject() {
        try {
            getGeneratorConfigurationProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        } catch (CoreException e) {
            MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
                "Could not refresh the CobiGen configuration project automatically. " + "Please try it again manually");
            LOG.warn("Configuration project refresh failed", e);
        }
    }

    /**
     * Returns the generator configuration project if it exists. If the project is closed, the project will be
     * opened automatically
     * @return the generator configuration {@link IProject}
     * @throws GeneratorProjectNotExistentException
     *             if no generator configuration project called {@link ResourceConstants#CONFIG_PROJECT_NAME}
     *             exists
     * @throws CoreException
     *             if an existing generator configuration project could not be opened
     */
    public static IProject getGeneratorConfigurationProject()
        throws GeneratorProjectNotExistentException, CoreException {

        IProject generatorProj =
            ResourcesPlugin.getWorkspace().getRoot().getProject(ResourceConstants.CONFIG_PROJECT_NAME);
        if (!generatorProj.exists()) {
            throw new GeneratorProjectNotExistentException();
        }
        if (!generatorProj.isOpen()) {
            generatorProj.open(new NullProgressMonitor());
        }

        return generatorProj;
    }
}
