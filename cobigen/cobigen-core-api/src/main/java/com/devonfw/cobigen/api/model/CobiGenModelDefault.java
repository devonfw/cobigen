package com.devonfw.cobigen.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation of {@link CobiGenModel}.
 */
public class CobiGenModelDefault extends AbstractCobiGenModel {

  private final Map<String, Object> originalMap;

  /**
   * The constructor.
   */
  public CobiGenModelDefault() {

    this(null, new HashMap<>());
  }

  /**
   * The constructor.
   *
   * @param originalMap the {@link #getOriginalMap() original map} with the variables.
   */
  public CobiGenModelDefault(Map<String, Object> originalMap) {

    this(null, originalMap);
  }

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent model} to inherit from.
   */
  public CobiGenModelDefault(CobiGenModel parent) {

    this(parent, new HashMap<>());
  }

  /**
   * The constructor.
   *
   * @param parent the {@link #getParent() parent model} to inherit from.
   * @param originalMap the {@link #getOriginalMap() original map} with the variables.
   */
  public CobiGenModelDefault(CobiGenModel parent, Map<String, Object> originalMap) {

    super(parent);
    this.originalMap = originalMap;
    putAll(originalMap);
  }

  @Override
  public Map<String, Object> getOriginalMap() {

    return this.originalMap;
  }

  public static CobiGenModelDefault fromLegacyMap(Map<String, Object> legacyModel) {

    Map<String, Object> map = new HashMap<>();
    Map<String, Object> variables = null;
    for (Entry<String, Object> entry : legacyModel.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (CobiGenVariableDefinitions.VARIABLES.getName().equals(key)) {
        variables = (Map<String, Object>) value;
      } else {
        map.put(key, value);
      }
    }
    if (variables != null) {
      map.putAll(variables);
    }
    CobiGenModelDefault model = new CobiGenModelDefault(map);
    return model;
  }

}
