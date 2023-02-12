package com.devonfw.cobigen.api.template.out.java;

import com.devonfw.cobigen.api.template.out.CobiGenOutputCode;
import com.devonfw.cobigen.api.template.out.ImportStatement;
import com.devonfw.cobigen.api.template.out.QualifiedName;

/**
 * Implementation of {@link com.devonfw.cobigen.api.template.out.CobiGenOutput} for Java.
 */
public class JavaCobiGenOutput extends CobiGenOutputCode {

  /**
   * The constructor.
   *
   * @param parent the parent type output.
   */
  public JavaCobiGenOutput(JavaCobiGenOutput parent) {

    super(parent);
  }

  /**
   * The constructor.
   *
   * @param filename the {@link #getFilename() filename}.
   */
  public JavaCobiGenOutput(String filename) {

    super(filename);
  }

  @Override
  protected JavaCobiGenOutput createChild() {

    return new JavaCobiGenOutput(this);
  }

  @Override
  public boolean addImport(QualifiedName qualifiedName) {

    if (isImportRequired(qualifiedName)) {
      return addImport(new JavaImportStatement(qualifiedName));
    }
    return false;
  }

  @Override
  protected ImportStatement createImportStatement(LineTokenizer tokenizer) {

    String token = tokenizer.next();
    String staticReference = null;
    QualifiedName qname;
    if (KEYWORD_STATIC.equals(token)) {
      token = tokenizer.next();
      qname = parseName(token);
      staticReference = qname.getSimpleName();
      qname = parseName(qname.getNamespace());
    } else {
      qname = parseName(token);
    }
    return new JavaImportStatement(qname, staticReference);
  }

  @Override
  protected boolean isImportRequired(QualifiedName qualifiedName) {

    String pkg = qualifiedName.getNamespace();
    if (pkg.isEmpty() || "java.lang".equals(pkg)) {
      return false;
    }
    return true;
  }

}
