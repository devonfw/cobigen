package com.devonfw.cobigen.impl.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;

public class ArtifactRetrieverUtil {

  private static final Logger LOG = LoggerFactory.getLogger(ArtifactRetrieverUtil.class);

  /** Path to artifact cache folder **/
  private static Path artifactCachePath = CobiGenPaths.getTemplateSetsFolderPath()
      .resolve(ConfigurationConstants.TEMPLATE_SET_ARTIFACT_CACHE_FOLDER);

  /**
   * Downloads template set artifacts from given URLs
   *
   * @param artifactUrls List of URLs
   * @return List of artifact Paths
   */
  public static List<Path> downloadArtifactsFromUrls(List<URL> artifactUrls) {

    List<Path> artifactPaths = new ArrayList<>();
    for (URL url : artifactUrls) {
      artifactPaths.add(Paths.get(TemplatesJarUtil.downloadJarFromURL(url.toString(), artifactCachePath)));

    }

    return artifactPaths;
  }

  /**
   * Retrieves the artifact cache path
   *
   * @return Path to artifact cache folder
   */
  public static Path retrieveArtifactCachePath() {

    Path artifactCacheFolder = CobiGenPaths.getTemplateSetsFolderPath()
        .resolve(ConfigurationConstants.TEMPLATE_SET_ARTIFACT_CACHE_FOLDER);
    return artifactCacheFolder;
  }

  /**
   * Checks is a directory is empty
   *
   * @param path directory to check
   * @return true if empty, false if not
   * @throws IOException
   */
  private static boolean isEmpty(Path path) throws IOException {

    if (Files.isDirectory(path)) {
      try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
        return !directory.iterator().hasNext();
      } catch (IOException e) {
        LOG.debug("An error occurred while checking if the directory {} was empty", path, e);
      }
    }

    return false;
  }

  /**
   * Retrieves a list of {@link TemplateSetConfiguration} from the template set artifact cache
   *
   * @param cachedArtifacts List of template set artifact paths
   *
   * @return List of {@link TemplateSetConfiguration}
   */
  public static List<TemplateSetConfiguration> retrieveArtifactsFromCache(List<Path> cachedArtifacts) {

    List<TemplateSetConfiguration> templateSetConfigurations = new ArrayList<>();

    if (cachedArtifacts == null) {

      Path artifactCacheFolder = retrieveArtifactCachePath();
      try {
        if (!Files.exists(artifactCacheFolder) || isEmpty(artifactCacheFolder)) {
          return null;
        }
      } catch (IOException e) {
        LOG.error("An error occurred while checking the artifact cache directory {}", artifactCacheFolder, e);
        return null;
      }

      List<File> artfactList = Arrays.asList(artifactCacheFolder.toFile().listFiles());
      for (File file : artfactList) {
        TemplateSetConfigurationReader reader = new TemplateSetConfigurationReader(file.toPath());
        reader.readConfiguration(file.toPath());

        TemplateSetConfiguration templateSetConfiguration = reader.getTemplateSetConfiguration();
        templateSetConfigurations.add(templateSetConfiguration);
      }
      return templateSetConfigurations;
    }

    for (Path file : cachedArtifacts) {
      TemplateSetConfigurationReader reader = new TemplateSetConfigurationReader(file);
      reader.readConfiguration(file);

      TemplateSetConfiguration templateSetConfiguration = reader.getTemplateSetConfiguration();
      templateSetConfigurations.add(templateSetConfiguration);
    }

    return templateSetConfigurations;

  }
}
