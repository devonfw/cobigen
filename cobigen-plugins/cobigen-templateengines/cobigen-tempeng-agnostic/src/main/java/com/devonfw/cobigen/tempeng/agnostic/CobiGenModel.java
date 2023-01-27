package com.devonfw.cobigen.tempeng.agnostic;

/**
 * Interface for the model of CobiGen.
 */
public interface CobiGenModel {

  /**
   * @param variableName the {@link CobiGenVariable#getName() name} of the {@link CobiGenVariable} to resolve.
   * @return the {@link CobiGenVariable#getValue() value} of the requested {@link CobiGenVariable} or {@code null} if
   *         not defined.
   */
  default Object getValue(String variableName) {

    CobiGenVariable variable = getVariable(variableName);
    if (variable == null) {
      return null;
    }
    return variable.getValue();
  }

  /**
   * @param variableName the {@link CobiGenVariable#getName() name} of the {@link CobiGenVariable} to resolve.
   * @return the requested {@link CobiGenVariable} or {@code null} if not defined.
   */
  CobiGenVariable getVariable(String variableName);

}
