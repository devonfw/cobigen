package com.devonfw.cobigen.javaplugin.merger.libextension;

import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.ExpressionVisitor;

public class ModifyableAnnotationValue implements AnnotationValue {

  Object parameterValue;

  @Override
  public Object getParameterValue() {

    return this.parameterValue;
  }

  /**
   * @param parameterValue
   */
  public void setParameterValue(Object parameterValue) {

    this.parameterValue = parameterValue;
  }

  @Override
  public Object accept(ExpressionVisitor visitor) {

    // TODO Auto-generated method stub
    return null;
  }

}