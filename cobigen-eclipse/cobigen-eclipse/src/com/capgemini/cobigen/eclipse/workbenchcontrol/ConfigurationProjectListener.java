package com.capgemini.cobigen.eclipse.workbenchcontrol;

import java.util.UUID;

import org.apache.log4j.MDC;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.constants.ResourceConstants;

/**
 * {@link ConfigurationProjectListener} for starting / stopping the {@link LogbackConfigChangeListener}
 * @author mbrunnli (08.04.2013)
 */
public class ConfigurationProjectListener implements IResourceChangeListener {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProjectListener.class);

    /**
     * Checks whether the given {@link IResourceChangeEvent} has closed the generator configuration project
     */
    private boolean closedBefore = false;

    /**
     * Resource change listener for the logback configuration.
     */
    private LogbackConfigChangeListener logbackConfigListener;

    /**
     * Object for synchronization purposes regarding the logback configuration listener
     */
    private Object logbackConfigListenerSync = new Object();

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2013), updated by sholzer (29.09.2015) for issue #156
     */
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());

        // /////////////
        // PRE_CLOSE //
        // /////////////
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            if (event.getResource() instanceof IProject) {
                IProject proj = (IProject) event.getResource();
                if (proj.getName().equals(ResourceConstants.CONFIG_PROJECT_NAME)) {
                    closedBefore = true;
                }
            }
        }

        // //////////////
        // POST_CLOSE //
        // //////////////
        if (event.getType() == IResourceChangeEvent.POST_BUILD && closedBefore) {
            closedBefore = false;
            synchronized (logbackConfigListenerSync) {
                ResourcesPlugin.getWorkspace().removeResourceChangeListener(logbackConfigListener);
                logbackConfigListener = null;
                LOG.info("Logback configuration listener registered.");
            }
        }

        // //////////////
        // POST_CHANGE //
        // //////////////
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) { // TODO probably not necessary anymore
            IResourceDelta[] affectedProjects = event.getDelta().getAffectedChildren(IResourceDelta.CHANGED);
            for (IResourceDelta projDelta : affectedProjects) {
                if (projDelta.getResource() instanceof IProject
                    && projDelta.getResource().getName().equals(ResourceConstants.CONFIG_PROJECT_NAME)) {
                    synchronized (logbackConfigListenerSync) {
                        logbackConfigListener =
                            new LogbackConfigChangeListener(((IProject) projDelta.getResource()));
                        ResourcesPlugin.getWorkspace().addResourceChangeListener(logbackConfigListener);
                        LOG.info("Logback configuration listener unregistered.");
                    }
                }
            }
        }

        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }
}
