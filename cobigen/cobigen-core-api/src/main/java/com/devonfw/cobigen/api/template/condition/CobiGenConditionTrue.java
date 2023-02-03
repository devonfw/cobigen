package com.devonfw.cobigen.api.template.condition;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Implementation of {@link CobiGenCondition} that always returns {@code true}.
 */
public class CobiGenConditionTrue implements CobiGenCondition {

  @Override
  public boolean test(CobiGenModel model) {

    return true;
  }

}
