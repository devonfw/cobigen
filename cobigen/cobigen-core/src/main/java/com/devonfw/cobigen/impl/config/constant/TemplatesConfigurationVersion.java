package com.devonfw.cobigen.impl.config.constant;

import java.util.Map;

import com.google.common.collect.Maps;

/** Version steps of the templates configuration. */
public enum TemplatesConfigurationVersion {

  /**
   * Initial release.
   */
  v1_0(1f, false),

  /**
   * ChangeLog:
   * <ul>
   * <li>new nodes template-scan and templateExtension added (#55)</li>
   * </ul>
   */
  v1_2(1.2f, false),

  /**
   * ChangeLog:
   * <ul>
   * <li>new templateScanRef node added as child for increment to reference templateScans (#118)</li>
   * <li>attribute id for templates now optional + new attribute 'name' introduced (#104)</li>
   * <li>target namespace changed</li>
   * </ul>
   */
  v2_1(2.1f, false),

  /**
   * ChangeLog:
   * <ul>
   * <li>new attribute templateEngine (#293)</li>
   * </ul>
   */
  v4_0(4.0f, true),

  /**
   * ChangeLog:
   * <ul>
   * <li>added explanation attribute to increments</li>
   * </ul>
   */
  v5_0(5.0f, true);

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
  private TemplatesConfigurationVersion(float floatRepresentation, boolean backwardCompatible) {

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
   * Get latest context configuration version supported by this CobiGen release.
   *
   * @return latest context configuration version supported by this CobiGen release.
   */
  public static TemplatesConfigurationVersion getLatest() {

    return values()[values().length - 1];
  }

  /**
   * Returns the sorted float representations of the enum's values.
   *
   * @return a sorted List
   */
  public static Map<Float, Boolean> valuesSorted() {

    Map<Float, Boolean> floatVersions = Maps.newTreeMap();
    for (TemplatesConfigurationVersion v : values()) {
      floatVersions.put(v.getFloatRepresentation(), v.isBackwardCompatible());
    }
    return floatVersions;
  }

  @Override
  public String toString() {

    return name().replace("_", ".");
  };
}
