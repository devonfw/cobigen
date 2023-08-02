package com.devonfw.cobigen.impl.config.constant;

import java.util.Map;

import com.google.common.collect.Maps;

/** Version steps of the templates configuration. */
public enum TemplateSetConfigurationVersion implements ConfigurationVersionEnum {

  /**
   * Initial release. Merges the content of the context.xml and the templates.xml into a new template-set.xml file.
   */
  v6_0(6f, false);

  /** Comparable float representation of the version number. */
  private float floatRepresentation;

  /** States whether the configuration is backward compatible to the previous version */
  private boolean backwardCompatible;

  /**
   * The constructor.
   *
   * @param floatRepresentation comparable float representation of the version number.
   * @param backwardCompatible whether the configuration is backward compatible to the previous version
   */
  private TemplateSetConfigurationVersion(float floatRepresentation, boolean backwardCompatible) {

    this.floatRepresentation = floatRepresentation;
    this.backwardCompatible = backwardCompatible;
  }

  /**
   * @return the comparable float representation value of the version.
   */
  public float getFloatRepresentation() {

    return this.floatRepresentation;
  }

  /**
   * @return whether the configuration is backward compatible to the previous version.
   */
  public boolean isBackwardCompatible() {

    return this.backwardCompatible;
  }

  /**
   * Returns the sorted float representations of the enum's values.
   *
   * @return a sorted List
   */
  public static Map<Float, Boolean> valuesSorted() {

    Map<Float, Boolean> floatVersions = Maps.newTreeMap();
    for (TemplateSetConfigurationVersion v : values()) {
      floatVersions.put(v.getFloatRepresentation(), v.isBackwardCompatible());
    }
    return floatVersions;
  }

  @Override
  public String toString() {

    return name().replace("_", ".");
  };
}
