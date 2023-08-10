package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.entity.TemplateSet;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

public class TemplateSetsConfigReader extends ConfigurationReader {

  /**
   * Logger instance.
   */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetsConfigReader.class);

  private final Map<String, TemplateSet> templateSets = new HashMap<>();

  /**
   * Need to cache template set reader as they read context and templates configuration in one go, while both is queried
   * separately by the engine. It is fully initialized after context configuration read time.
   */
  private final Map<String, TemplateSetReader> templateSetReaderCache = Collections.synchronizedMap(new HashMap<>());

  public TemplateSetsConfigReader(Path configRoot) {

    super(configRoot);
    readTemplateSets(configRoot);
  }

  private void readTemplateSets(Path configRoot) {

    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (Files.exists(templateSetsDownloaded)) {
      loadTemplateSetFilesDownloaded(templateSetsDownloaded);
    }
    if (Files.exists(templateSetsAdapted)) {
      loadTemplateSetFilesAdapted(templateSetsAdapted);
    }
  }

  /**
   * Reads the {@link ContextConfiguration} from cache or from file if not present in cache.
   *
   * @return the {@link ContextConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  @Override
  public ContextConfiguration readContextConfiguration() {

    if (templateSets.isEmpty()) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find any template-set configuration file in the given folder.");
    }
    return templateSets.values().parallelStream().map(ts -> {
      TemplateSetReader tsReader;
      synchronized (templateSetReaderCache) {
        tsReader = templateSetReaderCache.get(ts.getName());
      }
      if (tsReader == null) {
        tsReader = new TemplateSetReader(ts.getPath(), this);
        synchronized (templateSetReaderCache) {
          templateSetReaderCache.put(ts.getName(), tsReader);
        }
      }
      return tsReader.readContextConfiguration();
    }).collect(ContextConfigurationCollector.toContextConfiguration());
  }

  @Override
  public TemplatesConfiguration readTemplatesConfiguration(String triggerOrTemplateSet) {

    TemplateSetReader templateSetReader = templateSetReaderCache.get(triggerOrTemplateSet);
    if (templateSetReader == null)
      throw new InvalidConfigurationException(
          "A template set with name '" + triggerOrTemplateSet + "' was referenced, but could not be found.");

    return templateSetReader
        .readTemplatesConfiguration(templateSetReader.readContextConfiguration().getTrigger(triggerOrTemplateSet));
  }

  /**
   * Search for configuration files in the sub folders of adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   */
  protected void loadTemplateSetFilesAdapted(Path configRoot) {

    List<Path> templateSetDirectories = retrieveTemplateSetDirectories(configRoot);

    for (Path templateDirectory : templateSetDirectories) {
      Path templateSetFilePath = templateDirectory.resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      // makes sure that only valid template set folders get added
      if (Files.exists(templateSetFilePath)) {
        TemplateSet templateSet = templateSets.get(templateDirectory.getFileName().toString());
        if (templateSet != null) {
          templateSet.setExtractedPath(templateDirectory);
        } else {
          templateSets.put(templateDirectory.getFileName().toString(), new TemplateSet(templateDirectory));
        }
      } else {
        LOG.info("Ignoring folder {} as template set as it does not contain {} on top-level.", templateDirectory,
            templateSetFilePath.getFileName());
      }
    }
  }

  /**
   * Retrieves a list of template set directories
   *
   * @param configRoot List of template set directories
   */
  private List<Path> retrieveTemplateSetDirectories(Path configRoot) {

    List<Path> templateSetDirectories = new ArrayList<>();
    try (Stream<Path> files = Files.list(configRoot)) {
      files.forEach(path -> {
        if (Files.isDirectory(path)) {
          templateSetDirectories.add(path);
        }
      });
    } catch (IOException e) {
      throw new InvalidConfigurationException(configRoot, "Could not read configuration root directory.", e);
    }

    return templateSetDirectories;
  }

  /**
   * Search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   */
  protected void loadTemplateSetFilesDownloaded(Path configRoot) {

    // TODO: add check for valid template set jar util
    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    for (Path jarPath : templateJars) {
      Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
      Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      // makes sure that only valid template set jars get added
      if (Files.exists(templateSetFilePath)) {
        // TODO clarify on invariant, that downloaded jars are stored without version suffix
        this.templateSets.put(jarPath.getFileName().toString(),
            new TemplateSet(jarPath.getFileName().toString(), jarPath));
      } else {
        LOG.info("Ignoring jar {} as template set as it does not contain {} on top-level.", jarPath,
            templateSetFilePath.getFileName());
      }
    }
  }

}
