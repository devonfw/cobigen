package com.devonfw.cobigen.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class CobiGenPaths {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(CobiGenPaths.class);

  /**
   * Returns the CobiGen home directory, or creates a new one if it does not exist
   *
   * @return {@link Path} of the CobiGen home directory
   */
  public static Path getCobiGenHomePath() {

    String envValue = System.getenv(ConfigurationConstants.CONFIG_ENV_HOME);
    Path cobiGenPath;
    if (!StringUtils.isEmpty(envValue)) {
      LOG.info("Custom configuration folder configured in environment variable {}={}",
          ConfigurationConstants.CONFIG_ENV_HOME, envValue);
      cobiGenPath = Paths.get(envValue);
    } else {
      cobiGenPath = ConfigurationConstants.DEFAULT_HOME;
    }

    // We first check whether we already have a directory
    if (Files.exists(cobiGenPath)) {
      return cobiGenPath;
    }

    try {
      Files.createDirectories(cobiGenPath);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to create cobigen home directory at " + cobiGenPath);
    }
    return cobiGenPath;
  }

  /**
   * Returns the templates home directory (which is located inside CobiGen home folder). The folder is no longer created
   * if it does not exist. Instead CobiGen will switch to the template sets folder.
   *
   * @return {@link Path} of the templates home directory
   */
  public static Path getTemplatesFolderPath() {

    return getTemplatesFolderPath(getCobiGenHomePath());
  }

  /**
   * Returns the templates home directory (which is located inside CobiGen home folder). The folder is no longer created
   * if it does not exist. Instead CobiGen will switch to the template sets folder.
   *
   * @param home cobigen configuration home directory
   * @return {@link Path} of the templates home directory
   */
  public static Path getTemplatesFolderPath(Path home) {

    Path templatesPath = home.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
    return templatesPath;
  }

  /**
   * Returns the template set home directory (which is located inside CobiGen home folder). The directory will not be
   * created
   *
   * @return {@link Path} of the templates home directory
   */
  public static Path getTemplateSetsFolderPath() {

    return getTemplateSetsFolderPath(getCobiGenHomePath(), false);
  }

  /**
   * Returns the template set home directory (which is located inside CobiGen home folder). If createFolder is true, the
   * directory will be created.
   *
   * @param createFolder if true, the directory is also created
   *
   * @return {@link Path} of the templates home directory
   */
  public static Path getTemplateSetsFolderPath(boolean createFolder) {

    return getTemplateSetsFolderPath(getCobiGenHomePath(), createFolder);
  }

  /**
   * Returns the template sets home directory (which is located inside CobiGen home folder). The directory will not be
   * created
   *
   * @param home cobigen configuration home directory
   * @return {@link Path} of the template sets home directory
   */
  public static Path getTemplateSetsFolderPath(Path home) {

    return getTemplateSetsFolderPath(home, false);
  }

  /**
   * Returns the template sets home directory (which is located inside CobiGen home folder). If createFolder is true,
   * the directory will be created.
   *
   * @param home cobigen configuration home directory
   * @param createFolder if true, the folder will be created if it does not already exists
   * @return {@link Path} of the template sets home directory
   */
  public static Path getTemplateSetsFolderPath(Path home, boolean createFolder) {

    Path templatesPath = home.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    if (createFolder) {
      createFolder(templatesPath);
    }
    return templatesPath;
  }

  /**
   * Creates a directory at given path location
   *
   * @param folderPath Path of new folder
   * @return
   */
  private static Path createFolder(Path folderPath) {

    // We first check whether we already have a directory
    if (Files.exists(folderPath)) {
      return folderPath;
    }

    try {
      Files.createDirectories(folderPath);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to create path " + folderPath);
    }

    return folderPath;
  }

  /**
   * Returns the directory where the external processes are located, or creates a new one if it was not present
   *
   * @param processPath name of the process
   * @return {@link Path} of the external processes home directory
   */
  public static Path getExternalProcessesPath(String processPath) {

    Path home = getCobiGenHomePath();

    // We first check whether we already have a directory
    Path externalServersPath = home.resolve(processPath);
    if (Files.exists(externalServersPath)) {
      return externalServersPath;
    }

    if (new File(externalServersPath.toUri()).mkdir()) {
      return externalServersPath;
    } else {
      // Folder has not been created
      return null;
    }

  }

}
