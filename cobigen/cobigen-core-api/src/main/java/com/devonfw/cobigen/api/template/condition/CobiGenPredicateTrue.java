package com.devonfw.cobigen.api.template.condition;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Implementation of {@link CobiGenPredicate} that always returns {@code true}.
 */
public class CobiGenPredicateTrue implements CobiGenPredicate {

  @Override
  public boolean test(CobiGenModel model) {

    return true;
  }

}
