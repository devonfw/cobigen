package com.devonfw.cobigen.templates.oasp4j.utils;

/**
 * Connector is one association between classes in a class diagram. This class is used for storing that data, for later
 * developing templates.
 */
public class Connector {

  private String className;

  private String multiplicity;

  private String targetOrSource;

  /**
   * @param source The source of the connector
   * @param target The target of the connector
   * @param multiplicity The multiplicity of the target of this connector
   */
  public Connector(String source, String multiplicity, String targetOrSource) {

    this.className = source;
    this.multiplicity = multiplicity;
    this.targetOrSource = targetOrSource;
  }

  public String getClassName() {

    return this.className;
  }

  public void setClassName(String className) {

    className = className;
  }

  public String getMultiplicity() {

    return this.multiplicity;
  }

  public void setMultiplicity(String multiplicity) {

    this.multiplicity = multiplicity;
  }

  /**
   * @return targetOrSource
   */
  public String getTargetOrSource() {

    return this.targetOrSource;
  }

  /**
   * @param targetOrSource new value of {@link #gettargetOrSource}.
   */
  public void setTargetOrSource(String targetOrSource) {

    this.targetOrSource = targetOrSource;
  }

}
