package com.devonfw.cobigen.eclipse.workbenchcontrol;

import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.ResourceConstants;

/**
 * {@link ConfigurationProjectListener} for starting / stopping the {@link LogbackConfigChangeListener}. This class is
 * an abstract, due to the potential need of having multiple nested {@link IResourceChangeListener} for the CobiGen
 * configuration.
 *
 * @author mbrunnli (08.04.2013)
 */
public class ConfigurationProjectListener implements IResourceChangeListener {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProjectListener.class);

  /**
   * Resource change listener for the logback configuration.
   */
  private LogbackConfigChangeListener logbackConfigListener;

  /**
   * Object for synchronization purposes regarding the logback configuration listener
   */
  private Object logbackConfigListenerSync = new Object();

  /**
   * Initializes the {@link ConfigurationProjectListener} by registering all nested resource listener if the folder
   * already exists.
   */
  public ConfigurationProjectListener() {

    IProject cobigenConfigProject = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(ResourceConstants.CONFIG_PROJECT_NAME);
    if (cobigenConfigProject.exists() && cobigenConfigProject.isOpen()) {
      registerLogbackConfigListenerIfNotAlreadyDone(cobigenConfigProject);
    }
  }

  @Override
  public void resourceChanged(IResourceChangeEvent event) {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

    if (event.getType() == IResourceChangeEvent.PRE_CLOSE || event.getType() == IResourceChangeEvent.PRE_DELETE) {
      if (event.getResource() instanceof IProject) {
        IProject proj = (IProject) event.getResource();
        if (proj.getName().equals(ResourceConstants.CONFIG_PROJECT_NAME)) {
          synchronized (this.logbackConfigListenerSync) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.logbackConfigListener);
            this.logbackConfigListener = null;
            LOG.info("Logback configuration listener unregistered.");
          }
        }
      }
    }

    if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
      IResourceDelta[] affectedProjects = event.getDelta().getAffectedChildren(IResourceDelta.CHANGED);
      for (IResourceDelta projDelta : affectedProjects) {
        if (projDelta.getResource() instanceof IProject
            && projDelta.getResource().getName().equals(ResourceConstants.CONFIG_PROJECT_NAME)
            && projDelta.getResource().exists()) {
          registerLogbackConfigListenerIfNotAlreadyDone((IProject) projDelta.getResource());
        }
      }
    }

    MDC.remove(InfrastructureConstants.CORRELATION_ID);
  }

  /**
   * Registers the {@link LogbackConfigChangeListener} if not already set
   *
   * @param cobigenConfigFolder CobiGens configuration project in the workspace
   */
  private void registerLogbackConfigListenerIfNotAlreadyDone(IProject cobigenConfigFolder) {

    synchronized (this.logbackConfigListenerSync) {
      if (this.logbackConfigListener == null
          && cobigenConfigFolder.getFile(LogbackConfigChangeListener.LOGBACK_FILENAME).exists()) {
        this.logbackConfigListener = new LogbackConfigChangeListener(cobigenConfigFolder);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.logbackConfigListener);
      }
      LOG.info("Logback configuration listener registered.");
    }
  }
}
