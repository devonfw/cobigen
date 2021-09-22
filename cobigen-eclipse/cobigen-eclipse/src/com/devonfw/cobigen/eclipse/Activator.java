package com.devonfw.cobigen.eclipse;

import java.util.UUID;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.workbenchcontrol.ConfigurationProjectListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  /**
   * The plug-in ID
   */
  public static final String PLUGIN_ID = "com.devonfw.cobigen.eclipseplugin"; //$NON-NLS-1$

  /**
   * The shared instance
   */
  private static Activator plugin;

  /** {@link IResourceChangeListener} for the configuration project */
  private IResourceChangeListener configurationProjectListener;

  /**
   * Current state of the {@link IResourceChangeListener} for the configuration project
   */
  private volatile boolean configurationProjectListenerStarted = false;

  /**
   * Assigning logger to Activator
   */
  private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

  /**
   * The constructor
   */
  public Activator() {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
    this.configurationProjectListener = new ConfigurationProjectListener();
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
  }

  @Override
  public void start(BundleContext context) throws Exception {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
    super.start(context);
    plugin = this;

    startConfigurationProjectListener();
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
  }

  /**
   * Starts the ResourceChangeListener
   */
  public void startConfigurationProjectListener() {

    LOG.info("Start configuration project listener");

    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {

        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        synchronized (Activator.this.configurationProjectListener) {
          if (Activator.this.configurationProjectListenerStarted) {
            return;
          }
          ResourcesPlugin.getWorkspace().addResourceChangeListener(Activator.this.configurationProjectListener,
              IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE);
          Activator.this.configurationProjectListenerStarted = true;
          LOG.info("ResourceChangeListener for configuration project started.");
        }
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
      }
    });
  }

  /**
   * Stops the ResourceChangeListener
   */
  public void stopConfigurationListener() {

    LOG.info("Stop configuration project listener");
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {

        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());
        synchronized (Activator.this.configurationProjectListener) {
          if (!Activator.this.configurationProjectListenerStarted) {
            return;
          }
          ResourcesPlugin.getWorkspace().removeResourceChangeListener(Activator.this.configurationProjectListener);
          Activator.this.configurationProjectListenerStarted = false;
          LOG.info("ResourceChangeListener for configuration project stopped.");
        }
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
      }
    });
  }

  @Override
  public void stop(BundleContext context) throws Exception {

    plugin = null;
    super.stop(context);
  }

  /**
   * @return the shared instance
   */
  public static Activator getDefault() {

    return plugin;
  }

}
