package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;

/**
 * The {@link ContextConfiguration} is a configuration data wrapper for all information about templates and the target
 * destination for the generated data.
 */
public class ContextConfiguration {

  private BigDecimal version;

  /**
   * All available {@link Trigger}s
   */
  private Map<String, Trigger> triggers;

  /**
   * Path of the configuration. Might point to a folder or a jar or maybe even something different in future.
   */
  private Path configurationPath;

  /**
   * Constructor needed only for {@link com.devonfw.cobigen.impl.config.reader.ContextConfigurationCollector}
   */
  public ContextConfiguration() {

  }

  /**
   * Creates a new {@link ContextConfiguration} with the contents initially loaded from the context.xml
   *
   * @param configRoot root path for the configuration of CobiGen
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */
  public ContextConfiguration(BigDecimal version, Map<String, Trigger> triggers, Path configRoot)
      throws InvalidConfigurationException {

    this.version = version;
    this.configurationPath = configRoot;
    this.triggers = triggers;
  }

  /**
   * Returns all registered {@link Trigger}s
   *
   * @return all registered {@link Trigger}s
   */
  public List<Trigger> getTriggers() {

    return new ArrayList<>(this.triggers.values());
  }

  /**
   * @return the version
   */
  public BigDecimal getVersion() {

    return this.version;
  }

  /**
   * Returns the {@link Trigger} with the given id
   *
   * @param id of the {@link Trigger} to be searched
   * @return the {@link Trigger} with the given id or <code>null</code> if there is no
   */
  public Trigger getTrigger(String id) {

    return this.triggers.get(id);
  }

  /**
   * Merges another context configuration into _this_ context configuration instance
   * 
   * @param contextConfiguration to be merged
   */
  public ContextConfiguration merge(ContextConfiguration contextConfiguration) {

    triggers.putAll(contextConfiguration.triggers);
    return this;
  }
}
