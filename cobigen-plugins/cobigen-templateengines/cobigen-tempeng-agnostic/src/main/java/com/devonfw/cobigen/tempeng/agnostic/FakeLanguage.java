package com.devonfw.cobigen.tempeng.agnostic;

import java.io.IOException;

import com.devonfw.cobigen.api.util.StringUtil;

import io.github.mmm.code.api.CodeFile;
import io.github.mmm.code.api.expression.CodeVariable;
import io.github.mmm.code.api.item.CodeItem;
import io.github.mmm.code.api.language.JavaLanguage;
import io.github.mmm.code.api.statement.CodeLocalVariable;

/**
 * Fake implementation of {@link io.github.mmm.code.api.language.CodeLanguage}.
 */
public class FakeLanguage extends JavaLanguage {

  private final String extension;

  private final String name;

  /**
   * The constructor.
   *
   * @param name the language name and extension.
   */
  public FakeLanguage(String name) {

    this(StringUtil.capFirst(name), "." + name);
  }

  /**
   * The constructor.
   *
   * @param name the {@link #getLanguageName() language name}.
   * @param extension the file extension.
   */
  public FakeLanguage(String name, String extension) {

    super();
    this.name = name;
    this.extension = extension;
  }

  @Override
  public String getLanguageName() {

    return this.name;
  }

  @Override
  public String getVariableNameThis() {

    return "this"; // may be wrong, just fake
  }

  @Override
  public String getKeywordForVariable(CodeLocalVariable variable) {

    String keyword = "";
    if (variable.isFinal()) {
      keyword = "final ";
    }
    if (variable.getType() == null) {
      keyword = keyword + "var ";
    }
    return keyword;
  }

  @Override
  public void writeDeclaration(CodeVariable variable, Appendable sink) throws IOException {

    variable.writeReference(sink, false);
    sink.append(' ');
    sink.append(variable.getName());
  }

  @Override
  public String getFileFilename(CodeFile file) {

    return file.getSimpleName() + this.extension;
  }

  @Override
  protected boolean isRevervedKeyword(String word, CodeItem item) {

    return false;
  }

}
