package com.devonfw.cobigen.api.template.out;

import com.devonfw.cobigen.api.template.out.java.JavaCobiGenOutput;

import io.github.mmm.base.text.CaseHelper;

/**
 * Factory for {@link CobiGenOutput}.
 */
public final class CobiGenOutputFactory {

  private static final CobiGenOutputFactory INSTANCE = new CobiGenOutputFactory();

  private CobiGenOutputFactory() {

    super();
  }

  public CobiGenOutput create(String filename) {

    int lastDot = filename.lastIndexOf('.');
    String extension = "";
    if (lastDot > 0) {
      extension = CaseHelper.toLowerCase(filename.substring(lastDot + 1));
    }
    if ("java".equals(extension)) {
      return new JavaCobiGenOutput(filename);
    }
    return new StreamingCobiGenOutput(filename);
  }

  /**
   * @return the singleton instance.
   */
  public static CobiGenOutputFactory get() {

    return INSTANCE;
  }

}
