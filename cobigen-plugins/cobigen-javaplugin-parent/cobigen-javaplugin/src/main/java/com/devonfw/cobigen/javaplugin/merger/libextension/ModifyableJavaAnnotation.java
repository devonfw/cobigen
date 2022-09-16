package com.devonfw.cobigen.javaplugin.merger.libextension;

import java.util.Map;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

/**
 * TODO mdukhan This type ...
 *
 */
public class ModifyableJavaAnnotation implements JavaAnnotation {

  private Map<String, AnnotationValue> propertyMap;

  private JavaClass type;

  private AnnotationValue property;

  private int lineNumber;

  private Map<String, Object> namedParameterMap;

  private String codeBlock;

  private Object namedParameter;

  /**
   * The constructor.
   *
   * @param propertyMap
   * @param type
   * @param property
   * @param lineNumber
   * @param namedParameterMap
   * @param codeBlock
   * @param namedParameter
   */
  public ModifyableJavaAnnotation(Map<String, AnnotationValue> propertyMap, JavaClass type, AnnotationValue property,
      int lineNumber, Map<String, Object> namedParameterMap, String codeBlock, Object namedParameter) {

    super();
    this.propertyMap = propertyMap;
    this.type = type;
    this.property = property;
    this.lineNumber = lineNumber;
    this.namedParameterMap = namedParameterMap;
    this.codeBlock = codeBlock;
    this.namedParameter = namedParameter;
  }

  @SuppressWarnings("javadoc")
  public ModifyableJavaAnnotation(int lineNumber, String codeBlock, JavaClass type,
      Map<String, Object> namedParameterMap) {

    this.lineNumber = lineNumber;
    this.codeBlock = codeBlock;
    this.type = type;
    this.namedParameterMap = namedParameterMap;

  }

  @SuppressWarnings("javadoc")
  public ModifyableJavaAnnotation(int lineNumber, String codeBlock, JavaClass type) {

    this.lineNumber = lineNumber;
    this.codeBlock = codeBlock;
    this.type = type;

  }

  @Override
  public String toString() {

    return this.codeBlock;
  }

  @Override
  public String getCodeBlock() {

    return this.codeBlock;
  }

  @Override
  public int getLineNumber() {

    return this.lineNumber;
  }

  @Override
  public JavaClass getType() {

    return this.type;
  }

  @Override
  public Map<String, AnnotationValue> getPropertyMap() {

    return this.propertyMap;
  }

  @Override
  public AnnotationValue getProperty(String name) {

    return null;
  }

  @Override
  public Map<String, Object> getNamedParameterMap() {

    return this.namedParameterMap;
  }

  @Override
  public Object getNamedParameter(String key) {

    return null;
  }

  /**
   * @return property
   */
  public AnnotationValue getProperty() {

    return this.property;
  }

  /**
   * @param property new value of {@link #getproperty}.
   */
  public void setProperty(AnnotationValue property) {

    this.property = property;
  }

  /**
   * @return namedParameter
   */
  public Object getNamedParameter() {

    return this.namedParameter;
  }

  /**
   * @param namedParameter new value of {@link #getnamedParameter}.
   */
  public void setNamedParameter(Object namedParameter) {

    this.namedParameter = namedParameter;
  }

  /**
   * @param propertyMap new value of {@link #getpropertyMap}.
   */
  public void setPropertyMap(Map<String, AnnotationValue> propertyMap) {

    this.propertyMap = propertyMap;
  }

  /**
   * @param type new value of {@link #gettype}.
   */
  public void setType(JavaClass type) {

    this.type = type;
  }

  /**
   * @param lineNumber new value of {@link #getlineNumber}.
   */
  public void setLineNumber(int lineNumber) {

    this.lineNumber = lineNumber;
  }

  /**
   * @param namedParameterMap new value of {@link #getnamedParameterMap}.
   */
  public void setNamedParameterMap(Map<String, Object> namedParameterMap) {

    this.namedParameterMap = namedParameterMap;
  }

  /**
   * @param codeBlock new value of {@link #getcodeBlock}.
   */
  public void setCodeBlock(String codeBlock) {

    this.codeBlock = codeBlock;
  }

}
