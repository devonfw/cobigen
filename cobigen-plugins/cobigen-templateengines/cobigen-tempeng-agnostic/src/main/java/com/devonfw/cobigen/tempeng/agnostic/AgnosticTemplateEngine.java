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
import com.devonfw.cobigen.api.template.generator.CobiGenGenerator;
import com.devonfw.cobigen.tempeng.velocity.constant.VelocityMetadata;

import io.github.mmm.base.text.CaseConversion;
import io.github.mmm.code.base.BaseContext;
import io.github.mmm.code.base.BaseFile;
import io.github.mmm.code.base.BasePackage;
import io.github.mmm.code.base.source.BaseSourceImpl;
import io.github.mmm.code.base.type.BaseType;

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

    BaseType type = createType(template, model);
    model = new CobiGenModelDefault(model);
    CobiGenVariableDefinitions.GENERATED_TYPE.setValue(model, type);

    ImportStatements importStatements = new ImportStatements(type.getFile(), this.classLoader);
    Path templatePath = template.getAbsoluteTemplatePath();
    try (BufferedReader reader = Files.newBufferedReader(templatePath)) {
      boolean todo = true;
      while (todo) {
        String line = reader.readLine();
        if (line != null) {
          line = processLine(line, model, code, importStatements);
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

  private BaseType createType(TextTemplate template, CobiGenModel model) {

    String path = template.getRelativeTemplatePath();
    path = model.resolve(path, '.', VariableSyntax.AGNOSTIC);
    int lastDot = path.lastIndexOf('.');
    String extension = "";
    if (lastDot > 0) {
      extension = CaseConversion.LOWER_CASE.convert(path.substring(lastDot + 1));
      path = path.substring(0, lastDot);
    } else {
      LOG.warn("Missing file extension for template {}", path);
    }
    BaseSourceImpl source = new BaseSourceImpl(null, template.getAbsoluteTemplatePath().toFile(), "Fake-" + extension,
        null, null);
    try (BaseContext context = new FakeContext(new FakeLanguage(extension), source)) {
      path = path.replace('/', '.');
      lastDot = path.lastIndexOf('.');
      String fileName = "";
      if (lastDot > 0) {
        fileName = path.substring(lastDot + 1);
        path = path.substring(0, lastDot);
      }
      BasePackage rootPackage = new BasePackage(source);
      BasePackage pkg = createPackage(path, rootPackage);
      BaseFile file = new BaseFile(pkg, fileName);
      BaseType type = new BaseType(file, null);
      return type;
    }
  }

  private BasePackage createPackage(String path, BasePackage rootPackage) {

    if (path.startsWith("src/main/resources/")) {
      path = path.substring(19);
    }
    if (path.startsWith("templates/")) {
      path = path.substring(10);
    }
    path = path.replace('/', '.');
    BasePackage pkg = rootPackage;
    int start = 0;
    while (true) {
      int i = path.indexOf('.', start);
      if (i > 0) {
        String segment = path.substring(start, i);
        pkg = new BasePackage(pkg, segment, null, null, false);
        start = i + 1;
      } else {
        return pkg;
      }
    }
  }

  private String processLine(String line, CobiGenModel model, Writer code, ImportStatements imports)
      throws IOException {

    boolean isImport = imports.visitLine(line);
    Matcher matcher = PATTERN_COBIGEN.matcher(line);
    boolean cgFound = matcher.find();
    if (cgFound) {
      if (isImport) {
        return null;
      }
      StringBuilder sb = null;
      do {
        String cobiGenString = matcher.group();
        String cobiGenType = matcher.group(1);
        CobiGenGenerator generator;
        if (cobiGenString.startsWith("@")) {
          if (sb == null) {
            sb = new StringBuilder(line.length());
          }
          String cobiGenArgs = matcher.group(3);
          Objects.requireNonNull(cobiGenArgs, cobiGenString);
          if (cobiGenType.equals(CobiGenDynamicType.class.getSimpleName())) {
            cobiGenArgs = cobiGenArgs.trim().replaceAll("value\\s*=", "").replace(".class", "").trim();
            generator = imports.getGenerator(cobiGenArgs);
            String replacement = generator.generate(model);
            String typeName = matcher.group(4);
            if (typeName == null) {
              LOG.warn(
                  "Missing type when replacing '{}' with '{}' check your auto-formatter and prevent line-wrapping between annotation and type.",
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
          generator = imports.getGenerator(cobiGenType);
          generator.generate(model, code);
          return null;
        }
      } while (matcher.find());
      matcher.appendTail(sb);
      line = sb.toString();
    }
    return model.resolve(line, '.', VariableSyntax.AGNOSTIC);
  }
}
