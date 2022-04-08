package com.devonfw.cobigen.templates.devon4j.utils.uml;

/**
 * Connector is one association between classes in a class diagram. This class is used for storing that data, for later
 * developing templates.
 */
public class Connector {

  private String counterpartName = "";

  private String counterpartMultiplicity = "";

  private String className;

  private String multiplicity;

  final Boolean ISSOURCE;

  final Boolean ISTARGET;

  /**
   * @param className The name of the connector
   * @param multiplicity The multiplicity of the target of this connector
   * @param isSource True if the connector is the source, false if it is the target
   */
  public Connector(String className, String multiplicity, boolean isSource) {

    this.className = className;
    this.multiplicity = multiplicity;
    this.ISSOURCE = isSource;
    this.ISTARGET = !isSource;
  }

  /**
   * @return className name of the class
   */
  public String getClassName() {

    return this.className;
  }

  /**
   * @param className name of the class
   */
  public void setClassName(String className) {

    this.className = className;
  }

  /**
   * @return multiplicity
   */
  public String getMultiplicity() {

    return this.multiplicity;
  }

  /**
   * @param multiplicity multiplicity of the connection
   */
  public void setMultiplicity(String multiplicity) {

    this.multiplicity = multiplicity;
  }

  /**
   * @return counterpartName
   */
  public String getCounterpartName() {

    return this.counterpartName;
  }

  /**
   * @param counterpartName Name of the counter part entity
   */
  public void setCounterpartName(String counterpartName) {

    this.counterpartName = counterpartName;
  }

  /**
   * @return counterpartMultiplicity
   */
  public String getCounterpartMultiplicity() {

    return this.counterpartMultiplicity;
  }

  /**
   * @param counterpartMuliplicity multiplicity of the counter part entity
   */
  public void setCounterpartMultiplicity(String counterpartMuliplicity) {

    this.counterpartMultiplicity = counterpartMuliplicity;
  }

  @Override
  public String toString() {

    return this.ISSOURCE + " " + this.className + " " + this.multiplicity + " --> " + this.counterpartName + " "
        + this.counterpartMultiplicity + " " + this.ISTARGET;
  }
}
