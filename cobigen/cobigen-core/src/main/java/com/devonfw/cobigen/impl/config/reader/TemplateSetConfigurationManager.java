package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

/**
 * This class takes care of finding either adapted or downloaded template-set files.
 *
 * TODO: Move into TemplateSetConfigurationReader, see: https://github.com/devonfw/cobigen/issues/1668
 */
public class TemplateSetConfigurationManager {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetConfigurationManager.class);

  /** List with the paths of the configuration locations for the template-set.xml files */
  private Map<Path, Path> configLocations;

  /**
   * The constructor.
   */
  public TemplateSetConfigurationManager() {

    this.configLocations = new HashMap<>();

  }

  /**
   * @return configLocations
   */
  public Map<Path, Path> getConfigLocations() {

    return this.configLocations;
  }

  /**
   * Search for configuration files in the sub folders of adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   * @return List of Paths to the adapted templateSetFiles
   */
  public List<Path> loadTemplateSetFilesAdapted(Path configRoot) {

    List<Path> templateSetDirectories = retrieveTemplateSetDirectories(configRoot);

    // Create a map to hold template set info and their paths
    Map<String, TemplateSetInfo> templateSetInfoMap = new HashMap<>();
    
    List<Path> adaptedTemplateSets = new ArrayList<>();
    for (Path templateDirectory : templateSetDirectories) {
      Path templateSetFilePath = templateDirectory.resolve(ConfigurationConstants.MAVEN_CONFIGURATION_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      // makes sure that only valid template set folders get added
      if (Files.exists(templateSetFilePath)) {
        
        // Parse POM to get Maven coordinates
        MavenCoordinate mavenCoordinate = parsePomFromTemplateSet(templateDirectory);
        if (mavenCoordinate != null) {
          String key = mavenCoordinate.getGroupId() + ":" + mavenCoordinate.getArtifactId();
          TemplateSetInfo newInfo = new TemplateSetInfo(templateSetFilePath, templateDirectory, mavenCoordinate);
          
          // Check if we already have a template set with the same groupId:artifactId
          TemplateSetInfo existingInfo = templateSetInfoMap.get(key);
          if (existingInfo == null || compareVersions(mavenCoordinate.getVersion(), existingInfo.coordinate.getVersion()) > 0) {
            // This version is newer or it's the first one we've seen
            templateSetInfoMap.put(key, newInfo);
            LOG.debug("Found template set {}:{}:{} at {}", 
                mavenCoordinate.getGroupId(), 
                mavenCoordinate.getArtifactId(), 
                mavenCoordinate.getVersion(), 
                templateDirectory);
          } else {
            LOG.debug("Skipping older template set {}:{}:{} at {} in favor of version {}", 
                mavenCoordinate.getGroupId(), 
                mavenCoordinate.getArtifactId(), 
                mavenCoordinate.getVersion(), 
                templateDirectory,
                existingInfo.coordinate.getVersion());
          }
        } else {
          // Fallback: if POM parsing fails, include it anyway
          LOG.warn("Could not parse Maven coordinates from template set at {}, including anyway", templateDirectory);
          adaptedTemplateSets.add(templateSetFilePath);
          this.configLocations.put(templateSetFilePath, templateDirectory);
        }
      }
    }

    // Add the final selected template sets
    for (TemplateSetInfo info : templateSetInfoMap.values()) {
      adaptedTemplateSets.add(info.templateSetFilePath);
      this.configLocations.put(info.templateSetFilePath, info.templateDirectory);
    }

    return adaptedTemplateSets;
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
   * @return List of Paths to the downloaded templateSetFiles
   */
  public List<Path> loadTemplateSetFilesDownloaded(Path configRoot) {

    // TODO: add check for valid templatesetjar util
    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    List<Path> downloadedTemplateSets = new ArrayList<>();
    if (!templateJars.isEmpty()) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
        Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        // makes sure that only valid template set jars get added
        if (Files.exists(templateSetFilePath)) {
          downloadedTemplateSets.add(templateSetFilePath);
          this.configLocations.put(templateSetFilePath, jarPath);
        }

      }
    }

    return downloadedTemplateSets;
  }

  /**
   * Parses the pom.xml file from a template set directory to extract Maven coordinates
   *
   * @param templateDirectory the template set directory
   * @return MavenCoordinate or null if parsing fails
   */
  private MavenCoordinate parsePomFromTemplateSet(Path templateDirectory) {
    
    Path pomPath = templateDirectory.resolve("pom.xml");
    if (!Files.exists(pomPath)) {
      return null;
    }

    try (InputStream is = Files.newInputStream(pomPath)) {
      MavenXpp3Reader reader = new MavenXpp3Reader();
      Model model = reader.read(is);
      
      String groupId = model.getGroupId();
      String artifactId = model.getArtifactId();
      String version = model.getVersion();
      
      // Handle parent POM inheritance
      if (groupId == null && model.getParent() != null) {
        groupId = model.getParent().getGroupId();
      }
      if (version == null && model.getParent() != null) {
        version = model.getParent().getVersion();
      }
      
      if (groupId != null && artifactId != null && version != null) {
        return new MavenCoordinate(groupId, artifactId, version);
      }
      
    } catch (IOException | XmlPullParserException e) {
      LOG.warn("Failed to parse POM file at {}: {}", pomPath, e.getMessage());
    }
    
    return null;
  }

  /**
   * Compares two version strings. Returns positive if version1 > version2, negative if version1 < version2, 0 if equal.
   * 
   * This is a simple version comparison that handles semantic versioning and snapshot versions.
   * 
   * @param version1 first version to compare
   * @param version2 second version to compare
   * @return comparison result
   */
  private int compareVersions(String version1, String version2) {
    
    if (version1.equals(version2)) {
      return 0;
    }
    
    // Remove snapshot suffix for comparison
    String v1 = version1.replace("-SNAPSHOT", "");
    String v2 = version2.replace("-SNAPSHOT", "");
    
    String[] parts1 = v1.split("\\.");
    String[] parts2 = v2.split("\\.");
    
    int maxLength = Math.max(parts1.length, parts2.length);
    
    for (int i = 0; i < maxLength; i++) {
      String part1 = i < parts1.length ? parts1[i] : "0";
      String part2 = i < parts2.length ? parts2[i] : "0";
      
      try {
        int num1 = Integer.parseInt(part1);
        int num2 = Integer.parseInt(part2);
        int result = Integer.compare(num1, num2);
        if (result != 0) {
          return result;
        }
      } catch (NumberFormatException e) {
        // Fall back to string comparison if parts are not numeric
        int result = part1.compareTo(part2);
        if (result != 0) {
          return result;
        }
      }
    }
    
    // If all numeric parts are equal, prefer non-snapshot over snapshot
    if (version1.contains("-SNAPSHOT") && !version2.contains("-SNAPSHOT")) {
      return -1;
    } else if (!version1.contains("-SNAPSHOT") && version2.contains("-SNAPSHOT")) {
      return 1;
    }
    
    return 0;
  }

  /**
   * Helper class to hold template set information
   */
  private static class TemplateSetInfo {
    
    final Path templateSetFilePath;
    final Path templateDirectory;
    final MavenCoordinate coordinate;
    
    TemplateSetInfo(Path templateSetFilePath, Path templateDirectory, MavenCoordinate coordinate) {
      this.templateSetFilePath = templateSetFilePath;
      this.templateDirectory = templateDirectory;
      this.coordinate = coordinate;
    }
  }

}
