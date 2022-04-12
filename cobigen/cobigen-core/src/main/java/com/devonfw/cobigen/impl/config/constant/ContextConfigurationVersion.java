package com.devonfw.cobigen.impl.config.constant;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Version steps of the templates configuration.
 *
 * @author mbrunnli (Jun 22, 2015)
 */
public enum ContextConfigurationVersion {

  /**
   * Initial release.
   */
  v1_0(1f, false),

  /**
   * ChangeLog:
   * <ul>
   * <li>new recursiveInputRetrieval attribute added for containerMatcher (#81)</li>
   * <li>new accumulationType attribute added for matchers (#93)</li>
   * </ul>
   */
  v2_0(2f, false),

  /**
   * ChangeLog:
   * <ul>
   * <li>target namespace changed</li>
   * </ul>
   */
  v2_1(2.1f, false),

  /**
   * ChangeLog:
   * <ul>
   * <li>added links and tags, made templateFolder optional</li>
   * </ul>
   */
  v3_0(3.0f, true);

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
  private ContextConfigurationVersion(float floatRepresentation, boolean backwardCompatible) {

    this.floatRepresentation = floatRepresentation;
    this.backwardCompatible = backwardCompatible;
  }

  /**
   * @return the comparable float representation value of the version.
   * @author mbrunnli (May 17, 2016)
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
   * Get latest context configuration floatRepresentation supported by this CobiGen release.
   *
   * @return latest context configuration floatRepresentation supported by this CobiGen release.
   */
  public static ContextConfigurationVersion getLatest() {

    return values()[values().length - 1];
  }

  /**
   * Returns the sorted float representations of the enum's values.
   *
   * @return a sorted List
   */
  public static Map<Float, Boolean> valuesSorted() {

    Map<Float, Boolean> floatVersions = Maps.newTreeMap();
    for (ContextConfigurationVersion v : values()) {
      floatVersions.put(v.getFloatRepresentation(), v.isBackwardCompatible());
    }
    return floatVersions;
  }

  @Override
  public String toString() {

    return name().replace("_", ".");
  };
}
