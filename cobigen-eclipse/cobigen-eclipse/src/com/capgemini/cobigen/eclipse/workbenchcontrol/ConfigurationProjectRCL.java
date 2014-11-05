package com.capgemini.cobigen.eclipse.workbenchcontrol;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

import com.capgemini.cobigen.eclipse.Activator;
import com.capgemini.cobigen.eclipse.common.constants.ConfigResources;

/**
 * {@link ConfigurationProjectRCL} for starting / stopping the {@link SelectionServiceListener}
 * @author mbrunnli (08.04.2013)
 */
public class ConfigurationProjectRCL implements IResourceChangeListener {

    /**
     * Checks whether the given {@link IResourceChangeEvent} has closed the generator configuration project
     */
    private boolean closedBefore = false;

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2013)
     */
    @Override
    public void resourceChanged(IResourceChangeEvent event) {

        // /////////////
        // PRE_CLOSE //
        // /////////////
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            if (event.getResource() instanceof IProject) {
                IProject proj = (IProject) event.getResource();
                if (proj.getName().equals(ConfigResources.CONFIG_PROJECT_NAME)) {
                    closedBefore = true;
                }
            }
        }

        // //////////////
        // POST_CLOSE //
        // //////////////
        if (event.getType() == IResourceChangeEvent.POST_BUILD && closedBefore) {
            closedBefore = false;
            Activator.getDefault().stopSelectionServiceListener();
        }

        // //////////////
        // POST_CHANGE //
        // //////////////
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            IResourceDelta[] affectedProjects = event.getDelta().getAffectedChildren(IResourceDelta.CHANGED);
            for (IResourceDelta projDelta : affectedProjects) {
                if (projDelta.getResource().getName().equals(ConfigResources.CONFIG_PROJECT_NAME)) {
                    Activator.getDefault().startSelectionServiceListener();
                }
            }
        }
    }
}
