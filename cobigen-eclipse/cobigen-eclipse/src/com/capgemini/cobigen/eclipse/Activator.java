package com.capgemini.cobigen.eclipse;

import java.util.UUID;

import org.apache.log4j.MDC;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.capgemini.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.capgemini.cobigen.eclipse.workbenchcontrol.ConfigurationProjectRCL;
import com.capgemini.cobigen.eclipse.workbenchcontrol.SelectionServiceListener;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.propertyplugin.PropertyMergerPluginActivator;
import com.capgemini.cobigen.textmerger.TextMergerPluginActivator;
import com.capgemini.cobigen.xmlplugin.XmlPluginActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "com.capgemini.cobigen.eclipseplugin"; //$NON-NLS-1$

    /**
     * The shared instance
     */
    private static Activator plugin;

    /** {@link IResourceChangeListener} for the configuration project */
    private IResourceChangeListener resourceChangeListener = new ConfigurationProjectRCL();

    /**
     * {@link SelectionServiceListener} for valid input evaluation for the context menu entries
     */
    private SelectionServiceListener selectionServiceListener;

    /**
     * Current state of the {@link SelectionServiceListener}
     */
    private volatile Boolean selectionServiceListenerStarted = false;

    /**
     * Checks whether the workbench has been initialized (workaround for better user notification about
     * context.xml compile errors)
     */
    private volatile boolean initialized = false;

    /**
     * Assigning logger to Activator
     */
    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    /**
     * The constructor
     */
    public Activator() {
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (14.02.2013)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID());
        super.start(context);
        plugin = this;
        PluginRegistry.loadPlugin(JavaPluginActivator.class);
        PluginRegistry.loadPlugin(XmlPluginActivator.class);
        PluginRegistry.loadPlugin(PropertyMergerPluginActivator.class);
        PluginRegistry.loadPlugin(TextMergerPluginActivator.class);
        startSelectionServiceListener();
        startResourceChangeListener();
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
    }

    /**
     * Starts the ResourceChangeListener
     * @author mbrunnli (08.04.2013)
     */
    private void startResourceChangeListener() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                ResourcesPlugin.getWorkspace().addResourceChangeListener(
                    resourceChangeListener,
                    IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.POST_BUILD
                        | IResourceChangeEvent.POST_CHANGE);
                LOG.info("ResourceChangeListener for configuration project startet.");
            }
        });
    }

    /**
     * Starts the {@link SelectionServiceListener} for valid input evaluation for the context menu entries
     * @author mbrunnli (08.04.2013), adapted by sbasnet(30.10.2014)
     */
    public void startSelectionServiceListener() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                synchronized (selectionServiceListenerStarted) {
                    if (selectionServiceListenerStarted) {
                        return;
                    }
                    LOG.info("Start SelectionServiceListener.");
                    try {
                        selectionServiceListener = new SelectionServiceListener();
                        PlatformUIUtil.getActiveWorkbenchPage().addSelectionListener(JavaUI.ID_PACKAGES,
                            selectionServiceListener);
                        PlatformUIUtil.getActiveWorkbenchPage().addSelectionListener(ProjectExplorer.VIEW_ID,
                            selectionServiceListener);
                        selectionServiceListenerStarted = true;
                        LOG.info("SelectionServiceListener started.");
                    } catch (InvalidConfigurationException e) {
                        if (initialized) {
                            MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
                                "The context.xml of the generator configuration was changed into an invalid state.\n"
                                    + "The generator might not behave as intended:\n" + e.getMessage());
                        }
                        e.printStackTrace();
                    } catch (Exception e) {
                        if (initialized) {
                            MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e
                                .getClass().getSimpleName() + ": " + e.getMessage());
                            LOG.error("An error occured!", e);
                        }

                    } finally {
                        initialized = true;
                    }
                }
            }
        });
    }

    /**
     * Stops the {@link SelectionServiceListener} for valid input evaluation for the context menu entries
     *
     * @author mbrunnli (08.04.2013)
     */
    public void stopSelectionServiceListener() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                synchronized (selectionServiceListenerStarted) {
                    if (!selectionServiceListenerStarted) {
                        return;
                    }
                    PlatformUIUtil.getActiveWorkbenchPage().removeSelectionListener(JavaUI.ID_PACKAGES,
                        selectionServiceListener);
                    PlatformUIUtil.getActiveWorkbenchPage().removeSelectionListener(ProjectExplorer.VIEW_ID,
                        selectionServiceListener);
                    selectionServiceListenerStarted = false;
                    LOG.info("SelectionServiceListener stopped.");
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (14.02.2013)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

}
