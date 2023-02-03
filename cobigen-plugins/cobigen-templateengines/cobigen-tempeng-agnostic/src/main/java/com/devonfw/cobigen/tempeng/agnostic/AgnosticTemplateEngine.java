package com.devonfw.cobigen.tempeng.agnostic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devonfw.cobigen.api.annotation.Name;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenModelDefault;
import com.devonfw.cobigen.api.model.VariableSyntax;
import com.devonfw.cobigen.tempeng.velocity.constant.VelocityMetadata;

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

  private static final Pattern PATTERN_COBIGEN = Pattern.compile("CobiGen([\\p{L}0-9])*");

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
      CobiGenModelDefault model = new CobiGenModelDefault(modelAsMap);
      process(template, model, out);
    } catch (Throwable e) {
      throw new CobiGenRuntimeException("An unkonwn error occurred while generating the template."
          + template.getAbsoluteTemplatePath() + "(Agnostic v" + VelocityMetadata.VERSION + ")", e);
    }
  }

  private void process(TextTemplate template, CobiGenModel model, Writer code) {

    Path templatePath = template.getAbsoluteTemplatePath();
    try (BufferedReader reader = Files.newBufferedReader(templatePath)) {
      boolean todo = true;
      while (todo) {
        String line = reader.readLine();
        if (line != null) {
          line = processLine(line, model, code);
          if (line != null) {
            code.write(line);
            code.write('\n');
          }
        } else {
          todo = false;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error while instantiation template.");
    }
  }

  private String processLine(String line, CobiGenModel model, Writer code) {

    Matcher matcher = PATTERN_COBIGEN.matcher(line);
    if (matcher.find()) {
      String cobiGenType = matcher.group();
      if (!line.trim().startsWith("import ")) {
        CobiGenAgnosticRegistry.get().generate(cobiGenType, line, model, code);
      }
      return null;
    }
    return model.resolve(line, '.', VariableSyntax.AGNOSTIC);
  }
}
