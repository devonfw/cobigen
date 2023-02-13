package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.template.out.CobiGenOutput;

/**
 * Abstract base implementation for {@link CobiGenGenerator} that only generates into {@link CobiGenOutput}.
 */
public abstract class CobiGenOutputGenerator implements CobiGenGenerator {

  @Override
  public final String generate(CobiGenModel model) {

    CobiGenOutput out = CobiGenVariableDefinitions.OUT.getValue(model);
    doGenerate(out, model);
    return "";
  }

  @Override
  public final void generate(CobiGenModel model, Appendable code) throws IOException {

    CobiGenOutput out = CobiGenVariableDefinitions.OUT.getValue(model);
    doGenerate(out, model);
  }

  /**
   * @param out the {@link CobiGenOutput} where to write to.
   * @param model the {@link CobiGenModel}.
   */
  protected abstract void doGenerate(CobiGenOutput out, CobiGenModel model);

}
