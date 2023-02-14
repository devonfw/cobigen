package com.devonfw.cobigen.tempeng.agnostic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.CobiGenDynamicType;
import com.devonfw.cobigen.api.annotation.Name;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenModelDefault;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.model.VariableSyntax;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorProvider;
import com.devonfw.cobigen.api.template.out.AbstractCobiGenOutput;
import com.devonfw.cobigen.api.template.out.CobiGenOutputFactory;
import com.devonfw.cobigen.api.template.out.CobiGenOutputFallback;

/**
 * Template engine for language-agnostic-templates.<br>
 * The idea is that templates are written in the syntax and language of the target file to generate. That is, e.g. a
 * Java file is generated from a template with the package and name of the file to generate and simply has a ".java"
 * extension. It is containing valid Java code and the Java compiler is used to validate its syntax. The IDE of your
 * choice can be used for auto-completion, refactoring, code-formatting, etc. In order to represent variables in the
 * template, you simply put them in a specific {@link VariableSyntax#AGNOSTIC agnostic variable syntax} that is
 * compliant with any kind of format or programming language as it only uses letters and underscores that are more or
 * less allowed anywhere.
 */
@Name("Agnostic")
public class AgnosticTemplateEngine implements TextTemplateEngine {

  private static final Logger LOG = LoggerFactory.getLogger(AgnosticTemplateEngine.class);

  static final Pattern PATTERN_IMPORT = Pattern.compile(
      // .......1............2.............................................3........4...............5...............6
      "^\\s*import\\s(static)?\\s*(\\{[^}]+\\}|[\\p{L}0-9.]+|[\\\\p{L}0-9.]*\\*)(\\sas\\s([\\p{L}0-9]+))?(\\sfrom\\s[\"']([^\"']+)[\"'])?;");

  private static final Pattern PATTERN_COBIGEN = Pattern.compile( //
      // 1....................2...3...........4
      "@?(CobiGen[\\p{L}0-9]*)(\\(([^)]*)\\))?(\\s+[\\p{L}0-9]+)?");

  private ClassLoader classLoader;

  /**
   * The constructor.
   */
  public AgnosticTemplateEngine() {

    this(null);
  }

  /**
   * Constructor.
   */
  public AgnosticTemplateEngine(ClassLoader classLoader) {

    super();
    this.classLoader = classLoader;
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
  public void process(TextTemplate template, Map<String, Object> modelAsMap, Writer writer, String outputEncoding) {

    try {
      CobiGenModelDefault model = new CobiGenModelDefault(modelAsMap);
      process(template, model, writer);
    } catch (Throwable e) {
      throw new CobiGenRuntimeException("An unkonwn error occurred while generating the template."
          + template.getAbsoluteTemplatePath() + "(Agnostic)", e);
    }
  }

  private void process(TextTemplate template, CobiGenModel model, Writer writer) {

    String path = template.getRelativeTemplatePath();
    path = model.resolve(path, '/', VariableSyntax.AGNOSTIC);
    AbstractCobiGenOutput out = (AbstractCobiGenOutput) CobiGenOutputFactory.get().create(path);
    model = new CobiGenModelDefault(model);
    CobiGenVariableDefinitions.OUT.setValue(model, (out != null) ? out : new CobiGenOutputFallback(path));
    AbstractCobiGenOutput currentOut = out;
    Path templatePath = template.getAbsoluteTemplatePath();
    try (BufferedReader reader = Files.newBufferedReader(templatePath)) {
      boolean todo = true;
      while (todo) {
        String line = reader.readLine();
        if (line != null) {
          line = processLine(line, model, writer, out);
          if (line != null) {
            if (currentOut == null) {
              writer.write(line);
              writer.write('\n');
            } else {
              AbstractCobiGenOutput newOut = currentOut.addLine(line);
              if (newOut != currentOut) {
                currentOut = newOut;
                model = new CobiGenModelDefault(model);
                CobiGenVariableDefinitions.OUT.setValue(model, currentOut);
              }
            }
          }
        } else {
          todo = false;
        }
      }
      if (out != null) {
        out.write(writer);
      }
    } catch (IOException e) {
      throw new IllegalStateException("I/O error while instantiation template.");
    }
  }

  private String processLine(String line, CobiGenModel model, Writer writer, AbstractCobiGenOutput out)
      throws IOException {

    Matcher matcher = PATTERN_COBIGEN.matcher(line);
    boolean cgFound = matcher.find();
    if (cgFound) {
      if (line.trim().startsWith("import")) {
        return null;
      }
      StringBuilder sb = null;
      do {
        String cobiGenString = matcher.group();
        String cobiGenType = matcher.group(1);
        if (cobiGenString.startsWith("@")) {
          if (sb == null) {
            sb = new StringBuilder(line.length());
          }
          String cobiGenArgs = matcher.group(3);
          Objects.requireNonNull(cobiGenArgs, cobiGenString);
          if (cobiGenType.equals(CobiGenDynamicType.class.getSimpleName())) {
            cobiGenArgs = cobiGenArgs.trim().replaceAll("value\\s*=", "").replace(".class", "").trim();
            String replacement = CobiGenGeneratorProvider.get().generate(cobiGenArgs, model);
            String typeName = matcher.group(4);
            if (typeName == null) {
              LOG.warn(
                  "Missing type when replacing '{}' with '{}' - check your auto-formatter and prevent line-wrapping between annotation and type.",
                  cobiGenString, replacement);
            } else {
              if ((replacement == null) || replacement.isBlank()) {
                replacement = typeName; // fallback to parent type if generator result is empty
              }
              LOG.debug("Replacing '{}' with '{}'.", cobiGenString, replacement);
            }
            matcher.appendReplacement(sb, replacement);
          } else {
            LOG.warn("Unsupported annotation {}", cobiGenString);
          }
        } else {
          if (out == null) {
            CobiGenGeneratorProvider.get().generate(cobiGenType, model, writer);
          } else {
            String code = CobiGenGeneratorProvider.get().generate(cobiGenType, model);
            if (code.indexOf('\n') >= 0) {
              for (String codeLine : code.split("\n")) {
                out.addLine(codeLine);
              }
            } else {
              out.addLine(code);
            }
          }
          return null;
        }
      } while (matcher.find());
      matcher.appendTail(sb);
      line = sb.toString();
    }
    return model.resolve(line, '.', VariableSyntax.AGNOSTIC);
  }
}
