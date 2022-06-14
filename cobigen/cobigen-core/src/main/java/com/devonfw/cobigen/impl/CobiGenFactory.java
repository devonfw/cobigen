package com.devonfw.cobigen.impl;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.HealthCheck;
import com.devonfw.cobigen.api.exception.DeprecatedMonolithicTemplatesException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.aop.BeanFactory;
import com.devonfw.cobigen.impl.aop.ProxyFactory;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.healthcheck.HealthCheckImpl;
import com.devonfw.cobigen.impl.util.ConfigurationClassLoaderUtil;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;

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
   * @param force ignores deprecated template folder structure
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be read properly.
   */
  public static CobiGen create(URI configFileOrFolder, boolean force) throws InvalidConfigurationException {

    Objects.requireNonNull(configFileOrFolder, "The URI pointing to the configuration could not be null.");

    ConfigurationHolder configurationHolder = new ConfigurationHolder(configFileOrFolder);
    BeanFactory beanFactory = new BeanFactory();
    beanFactory.addManuallyInitializedBean(configurationHolder);
    CobiGen createBean = beanFactory.createBean(CobiGen.class);
    // Notifies all plugins of new template root path
    PluginRegistry.notifyPlugins(configurationHolder.getConfigurationPath());

    // Check old_templates and throw if found also in custom templates
    if (!force && !configurationHolder.isTemplateSetConfiguration())
      throw new DeprecatedMonolithicTemplatesException();
    return createBean;
  }

  /**
   * Creates a new {@link CobiGen}
   *
   * @return a new instance of {@link CobiGen}
   * @throws InvalidConfigurationException if the context configuration could not be read properly.
   */
  public static CobiGen create() throws InvalidConfigurationException {

    URI configFileOrFolder = ConfigurationFinder.findTemplatesLocation();
    if (configFileOrFolder == null) {
      throw new InvalidConfigurationException(
          "No valid templates can be found. Please configure your cobigen configuration file properly or place the templates in cobigen home directory. Creating CobiGen instance aborted.");
    }
    return create(configFileOrFolder);
  }

  /**
   * Creates a new {@link HealthCheck}.
   *
   * @return a new {@link HealthCheck} instance
   */
  public static HealthCheck createHealthCheck() {

    return ProxyFactory.getProxy(new HealthCheckImpl());
  }

}
