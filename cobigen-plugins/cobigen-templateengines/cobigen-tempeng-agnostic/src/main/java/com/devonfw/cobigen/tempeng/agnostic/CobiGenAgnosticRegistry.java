package com.devonfw.cobigen.tempeng.agnostic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.template.generator.CobiGenGenerator;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeEtoSuperClass;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeFields;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeGetters;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeSetters;

/**
 * Central registry for CobiGen constructs supported by language agnostic templates.
 */
public final class CobiGenAgnosticRegistry {

  private static final CobiGenAgnosticRegistry INSTANCE = new CobiGenAgnosticRegistry();

  private final Map<String, CobiGenGenerator> generators;

  private CobiGenAgnosticRegistry() {

    super();
    this.generators = new HashMap<>();
    add(new CobiGenGeneratorJavaTypeEtoSuperClass());
    add(new CobiGenGeneratorJavaTypeFields());
    add(new CobiGenGeneratorJavaTypeGetters());
    add(new CobiGenGeneratorJavaTypeSetters());
  }

  private void add(CobiGenGenerator generator) {

    this.generators.put(generator.getClass().getSimpleName(), generator);
  }

  public String generate(String cobiGenType, String line, CobiGenModel model) {

    StringBuilder sb = new StringBuilder();
    generate(cobiGenType, line, model, sb);
    return sb.toString();
  }

  public void generate(String cobiGenType, String line, CobiGenModel model, Appendable code) {

    try {
      CobiGenGenerator generator = getGenerator(cobiGenType);
      // if (generator != null) {
      generator.generate(model, code);
      // }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error whilst running " + cobiGenType, e);
    }
  }

  public CobiGenGenerator getGenerator(String cobiGenType) {

    CobiGenGenerator generator = this.generators.get(cobiGenType);
    if (generator == null) {
      throw new IllegalArgumentException("Undefined generator " + cobiGenType);
    }
    return generator;
  }

  /**
   * @return the singleton instance.
   */
  public static CobiGenAgnosticRegistry get() {

    return INSTANCE;
  }

}
