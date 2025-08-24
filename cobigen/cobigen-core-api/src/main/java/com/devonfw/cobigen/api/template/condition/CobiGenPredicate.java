package com.devonfw.cobigen.api.template.condition;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Interface for a condition. Implementations must have a public non-arg constructor.
 */
public interface CobiGenPredicate {

  /**
   * @param model the {@link CobiGenModel}.
   * @return {@code true} if the condition applies, {@code false} otherwise.
   */
  boolean test(CobiGenModel model);

}
