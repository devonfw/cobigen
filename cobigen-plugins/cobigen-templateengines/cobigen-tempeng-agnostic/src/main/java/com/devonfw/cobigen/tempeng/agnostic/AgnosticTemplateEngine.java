package com.devonfw.cobigen.tempeng.agnostic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Name;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.tempeng.velocity.constant.VelocityMetadata;

import io.github.mmm.base.text.CaseSyntax;

/**
 * Template engine for language-agnostic-templates.<br>
 * The idea is that templates are written in the syntax and language of the target file to generate. That is, e.g. a
 * Java file is generated from a template with the package and name of the file to generate and simply has a ".java"
 * extension. It is containing valid Java code and the Java compiler is used to validate its syntax. The IDE of your
 * choice can be used for auto-completion, refactoring, code-formatting, etc. In order to represent variables in the
 * template, you simply put them in a specific syntax that is compliant with any
 */
@Name("Agnostic")
public class AgnosticTemplateEngine implements TextTemplateEngine {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(AgnosticTemplateEngine.class);

  private static final Pattern PATTERN_VARIABLE = Pattern.compile("(\\.?)([xX]_([a-zA-Z][a-zA-Z0-9_$-]*)_[xX])");

  /**
   * Constructor.
   */
  public AgnosticTemplateEngine() {

    super();
  }

  @Override
  public void setTemplateFolder(Path templateFolderPath) {

    // pointless
  }

  @Override
  public String getTemplateFileEnding() {

    return null;
  }

  @Override
  public void process(TextTemplate template, Map<String, Object> modelAsMap, Writer out, String outputEncoding) {

    try {
      CobiGenModelImpl model = new CobiGenModelImpl();
      model.addAll(modelAsMap);
      process(template, model, out);
    } catch (Throwable e) {
      throw new CobiGenRuntimeException("An unkonwn error occurred while generating the template."
          + template.getAbsoluteTemplatePath() + "(Agnostic v" + VelocityMetadata.VERSION + ")", e);
    }
  }

  private void process(TextTemplate template, CobiGenModel model, Writer out) {

    Path templatePath = template.getAbsoluteTemplatePath();
    try (BufferedReader reader = Files.newBufferedReader(templatePath)) {
      boolean todo = true;
      while (todo) {
        String line = reader.readLine();
        line = processLine(line, model);
        if (line != null) {
          out.write(line);
          out.write('\n');
        } else {
          todo = false;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error while instantiation template.");
    }
  }

  private String processLine(String line, CobiGenModel model) {

    if ((line == null) || line.contains("CobiGen")) {
      return null;
    }
    Matcher m = PATTERN_VARIABLE.matcher(line);
    if (!m.find()) {
      return line;
    }
    StringBuilder sb = new StringBuilder(line.length());
    do {
      String dot = m.group(1);
      String var = m.group(3);
      String replacement;
      Object value = model.getValue(var);
      if (value instanceof String) {
        replacement = value.toString();
        if (!replacement.isBlank()) {
          CaseSyntax caseSyntax = CaseSyntax.ofExample(var, true);
          if (caseSyntax != CaseSyntax.LOWERCASE) {
            replacement = caseSyntax.convert(replacement);
          }
        }
        if (!dot.isEmpty() && !replacement.isEmpty()) {
          replacement = dot + replacement;
        }
      } else {
        LOG.warn("Undefined variable {}", var);
        replacement = m.group();
      }
      m.appendReplacement(sb, replacement);
    } while (m.find());
    m.appendTail(sb);
    return sb.toString();
  }
}
