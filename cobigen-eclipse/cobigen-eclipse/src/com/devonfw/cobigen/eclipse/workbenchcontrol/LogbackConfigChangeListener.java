package com.devonfw.cobigen.eclipse.workbenchcontrol;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * {@link IResourceChangeListener} for the generator configuration project
 */
public class LogbackConfigChangeListener implements IResourceChangeListener {

  /**
   * Assigning logger to LogbackConfigChangeListener
   */
  private final static Logger LOG = LoggerFactory.getLogger(LogbackConfigChangeListener.class);

  /** Logback configuration file name */
  public final static String LOGBACK_FILENAME = "logback.xml";

  /**
   * Generator configuration project
   */
  private IProject generatorConfProj;

  /**
   * logback.xml of the given project
   */
  private IFile logbackXmlFile;

  /**
   * Creates a new resource change listener for the given generator configuration project. The resource change listener
   * will track the context.xml and logback.xml files to be reloaded, when they change.
   *
   * @param generatorConfProj generator configuration folder
   */
  public LogbackConfigChangeListener(IProject generatorConfProj) {

    this.generatorConfProj = generatorConfProj;
    this.logbackXmlFile = generatorConfProj.getFile(LOGBACK_FILENAME);
    try {
      loadLogbackConfiguration(this.logbackXmlFile.getRawLocation().toString());
    } catch (IOException | JoranException e) {
      LOG.error("Logback file could not be read or written.", e);
    }
  }

  @Override
  public void resourceChanged(IResourceChangeEvent event) {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

    if (event.getDelta() != null) {
      IResourceDelta[] affectedProjects = event.getDelta().getAffectedChildren();
      for (IResourceDelta projDelta : affectedProjects) {
        if (projDelta.getResource().equals(this.generatorConfProj)) {
          IResourceDelta[] affectedChildren = projDelta.getAffectedChildren();
          for (IResourceDelta fileDelta : affectedChildren) {
            if (fileDelta.getResource().equals(this.logbackXmlFile)) {
              try {
                loadLogbackConfiguration(this.logbackXmlFile.getRawLocation().toString());
                LOG.info("The Logback logback.xml has been changed and reloaded.");
              } catch (IOException e) {
                LOG.error("Unable to read config file", e);
              } catch (JoranException e) {
                MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning",
                    "The " + ConfigurationConstants.CONTEXT_CONFIG_FILENAME
                        + " of the generator configuration was changed into an invalid state.\n"
                        + "The generator might not behave as intended:\n" + e.getMessage());
                LOG.error(
                    "The {} of the generator configuration was changed into an invalid state.\nThe generator might not behave as intended.",
                    ConfigurationConstants.CONTEXT_CONFIG_FILENAME, e);
              }
            }
          }
        }
      }
    }
    MDC.remove(InfrastructureConstants.CORRELATION_ID);
  }

  /**
   * (Re-)Loads Logback configuration
   *
   * @param externalConfigFileLocation location of the new logback.xml
   * @throws IOException if the file could not be read or written
   * @throws JoranException if the file could not be handled by log4j
   */
  public void loadLogbackConfiguration(String externalConfigFileLocation) throws IOException, JoranException {

    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

    File externalConfigFile = new File(externalConfigFileLocation);
    if (!externalConfigFile.exists()) {
      throw new IOException("Logback External Config File Parameter does not reference a file that exists");
    } else {
      if (!externalConfigFile.isFile()) {
        throw new IOException("Logback External Config File Parameter exists, but does not reference a file");
      } else {
        if (!externalConfigFile.canRead()) {
          throw new IOException("Logback External Config File exists and is a file, but cannot be read.");
        } else {
          JoranConfigurator configurator = new JoranConfigurator();
          configurator.setContext(lc);
          lc.reset();
          configurator.doConfigure(externalConfigFileLocation);
          LOG.info("Configured Logback with config file from: {}", externalConfigFileLocation);
        }
      }
    }
  }
}
