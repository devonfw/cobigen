package com.devonfw.cobigen.impl;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.HealthCheck;
import com.devonfw.cobigen.api.TemplateAdapter;
import com.devonfw.cobigen.api.exception.DeprecatedMonolithicConfigurationException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.adapter.TemplateAdapterImpl;
import com.devonfw.cobigen.impl.aop.BeanFactory;
import com.devonfw.cobigen.impl.aop.ProxyFactory;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.ConfigurationProperties;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.healthcheck.HealthCheckImpl;
import com.devonfw.cobigen.impl.util.ConfigurationClassLoaderUtil;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
import com.devonfw.cobigen.retriever.ArtifactRetriever;

/**
 * CobiGen's Factory to create new instances of {@link CobiGen}.
 */
public class CobiGenFactory {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(CobiGenFactory.class);

  /**
   * Creates a new {@link CobiGen} while searching a valid configuration at the given classpath
   *
   * @param classloader a classloader which should contain
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be found and read properly.
   */
  public static CobiGen create(ClassLoader classloader) throws InvalidConfigurationException {

    Objects.requireNonNull(classloader, "The classloader cannot not be null.");

    URL contextConfigurationLocation = ConfigurationClassLoaderUtil.getContextConfiguration(classloader);
    URI configFile = URI.create(contextConfigurationLocation.getFile().toString().split("!")[0]);
    LOG.debug("Reading configuration from file " + configFile.toString());
    return create(configFile);
  }

  /**
   * Creates a new {@link CobiGen} while searching a valid configuration at the given path
   *
   * @param configFileOrFolder the root folder containing the context.xml and all templates, configurations etc.
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be read properly.
   */
  public static CobiGen create(URI configFileOrFolder) throws InvalidConfigurationException {

    return create(configFileOrFolder, false);
  }

  /**
   * Creates a new {@link CobiGen} while searching a valid configuration at the given path
   *
   * @param configFileOrFolder the root folder containing the context.xml and all templates, configurations etc.
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be read properly.
   */
  public static CobiGen create() throws InvalidConfigurationException {

    return create(false);
  }

  /**
   * Creates a new {@link CobiGen} while searching a valid configuration at the given path and also start downloading
   * templates defined in the properties
   *
   * @param configFileOrFolder the root folder containing the context.xml and all templates, configurations etc.
   * @param allowMonolithicConfiguration ignores deprecated monolithic template folder structure and if found does not
   *        throw a DeprecatedMonolithicConfigurationException
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be read properly.
   */
  public static CobiGen create(URI configFileOrFolder, boolean allowMonolithicConfiguration)
      throws InvalidConfigurationException {

    Objects.requireNonNull(configFileOrFolder, "The URI pointing to the configuration could not be null.");

    ConfigurationHolder configurationHolder = new ConfigurationHolder(configFileOrFolder);
    BeanFactory beanFactory = new BeanFactory();
    beanFactory.addManuallyInitializedBean(configurationHolder);
    CobiGen createBean = beanFactory.createBean(CobiGen.class);
    // Notifies all plugins of new template root path
    PluginRegistry.notifyPlugins(configurationHolder.getConfigurationPath());

    if (!allowMonolithicConfiguration && !configurationHolder.isTemplateSetConfiguration()) {
      throw new DeprecatedMonolithicConfigurationException(Paths.get(configFileOrFolder));
    }
    // install Template Sets defined in .properties file
    if (configurationHolder.isTemplateSetConfiguration()) {
      ConfigurationProperties config = configurationHolder.getConfigurationProperties();
      // if installed template sets property was not empty, install found template sets
      if (!config.getTemplateSetsInstalled().isEmpty()) {
        Path templatesLocation = configurationHolder.getConfigurationPath();
        List<String> downloadUrls = ArtifactRetriever.retrieveTemplateSetJarDownloadURLs(config.getGroupIds(),
            config.getTemplateSetsInstalled());
        for (String downloadUrl : downloadUrls) {
          TemplatesJarUtil.downloadJarFromURL(downloadUrl, templatesLocation);
        }
      }
    }
    return createBean;
  }

  /**
   * Creates a new {@link CobiGen}
   *
   * @param allowMonolithicConfiguration ignores deprecated monolithic template folder structure and if found does not
   *        throw a DeprecatedMonolithicConfigurationException
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be read properly.
   */
  public static CobiGen create(boolean allowMonolithicConfiguration) throws InvalidConfigurationException {

    URI configFileOrFolder = ConfigurationFinder.findTemplatesLocation();
    if (configFileOrFolder == null) {
      throw new InvalidConfigurationException(
          "No valid templates can be found. Please configure your cobigen configuration file properly or place the templates in cobigen home directory. Creating CobiGen instance aborted.");
    }

    return create(configFileOrFolder, allowMonolithicConfiguration);
  }

  /**
   * Creates a new {@link HealthCheck}.
   *
   * @return a new {@link HealthCheck} instance
   */
  public static HealthCheck createHealthCheck() {

    return ProxyFactory.getProxy(new HealthCheckImpl());
  }

  /**
   * Upgrades the given template configuration.
   *
   * @param configurationPath path to the templates Configuration
   * @return the new path to the new template-sets
   */
  public static Path startTemplatesUpgrader(Path configurationPath) {

    TemplateAdapter templateAdapter = new TemplateAdapterImpl(configurationPath);
    return templateAdapter.upgradeMonolithicTemplates(configurationPath);
  }
}
