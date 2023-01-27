package com.devonfw.cobigen.tempeng.agnostic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation of {@link CobiGenModel}.
 */
public class CobiGenModelImpl implements CobiGenModel {

  private final Map<String, CobiGenVariable> map;

  /**
   * The constructor.
   */
  public CobiGenModelImpl() {

    super();
    this.map = new HashMap<>();
  }

  @Override
  public CobiGenVariable getVariable(String variableName) {

    return this.map.get(CobiGenVariable.normalize(variableName));
  }

  /**
   * @param variable the {@link CobiGenVariable} to add.
   * @return the previous {@link CobiGenVariable} that has been replaced because it has the same
   *         {@link CobiGenVariable#normalize(String) normalized} {@link CobiGenVariable#getName() name}.
   */
  public CobiGenVariable addVariable(CobiGenVariable variable) {

    return this.map.put(CobiGenVariable.normalize(variable.getName()), variable);
  }

  /**
   * @param name the {@link CobiGenVariable#getName() name} of the {@link CobiGenVariable} to add.
   * @param value the {@link CobiGenVariable#getValue() value} of the {@link CobiGenVariable} to add.
   * @return the previous {@link CobiGenVariable} that has been replaced because it has the same
   *         {@link CobiGenVariable#normalize(String) normalized} {@link CobiGenVariable#getName() name}.
   */
  public CobiGenVariable addVariable(String name, Object value) {

    return addVariable(new CobiGenVariable(name, value));
  }

  /**
   * @param modelAsMap the raw model as plain {@link Map}.
   */
  public void addAll(Map<String, Object> modelAsMap) {

    for (Entry<String, Object> entry : modelAsMap.entrySet()) {
      String name = entry.getKey();
      Object value = entry.getValue();
      CobiGenVariable duplicate = addVariable(name, value);
      assert (duplicate == null);
    }
  }

}
