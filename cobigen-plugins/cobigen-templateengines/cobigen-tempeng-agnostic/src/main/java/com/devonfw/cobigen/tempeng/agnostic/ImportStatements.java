package com.devonfw.cobigen.tempeng.agnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.devonfw.cobigen.api.template.generator.CobiGenGenerator;

import io.github.mmm.code.api.imports.CodeImport;
import io.github.mmm.code.api.imports.CodeImportItem;
import io.github.mmm.code.base.BaseFile;
import io.github.mmm.code.base.imports.BaseImport;
import io.github.mmm.code.base.imports.BaseImportItem;

class ImportStatements {

  private final BaseFile file;

  private final ClassLoader classLoader;

  private final Map<String, String> imports;

  private final Map<String, CobiGenGenerator> generators;

  /**
   * The constructor.
   */
  public ImportStatements(BaseFile file, ClassLoader classLoader) {

    super();
    this.file = file;
    if (classLoader == null) {
      this.classLoader = Thread.currentThread().getContextClassLoader();
    } else {
      this.classLoader = classLoader;
    }
    this.imports = new HashMap<>();
    this.generators = new HashMap<>();
  }

  /**
   * @param name the {@link Class#getSimpleName() simple name} of the type to check.
   * @return {@code true} if the specified type name is imported, {@code false} otherwise.
   */
  public boolean hasImport(String name) {

    return this.imports.containsKey(name);
  }

  /**
   * @param name the {@link Class#getSimpleName() simple name} of the type to resolve.
   * @return the qualified name that was imported or {@code null} if not imported.
   */
  public String getImport(String name) {

    return this.imports.get(name);
  }

  public CobiGenGenerator getGenerator(String generator) {

    return this.generators.computeIfAbsent(generator, this::createGenerator);
  }

  private CobiGenGenerator createGenerator(String generator) {

    String fqn = getImport(generator);
    if (fqn == null) {
      throw new IllegalStateException("Undefined generator " + generator);
    }
    try {
      Class<?> clazz = this.classLoader.loadClass(fqn);
      Object instance = clazz.getDeclaredConstructor().newInstance();
      return CobiGenGenerator.class.cast(instance);
    } catch (ReflectiveOperationException | ClassCastException e) {
      throw new IllegalStateException("Failed to instantiate generator " + generator, e);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public boolean visitLine(String line) {

    Matcher m = AgnosticTemplateEngine.PATTERN_IMPORT.matcher(line);
    if (m.find()) {
      boolean staticFlag = (m.group(1) != null);
      String src = m.group(2);
      String alias = m.group(4);
      String from = m.group(6);
      BaseImport importStatement;
      if ((alias == null) && (from == null)) {
        importStatement = new BaseImport(src, staticFlag);
        if (!staticFlag) {
          String simpleName = StringUtils.substringAfterLast(src, '.');
          this.imports.put(simpleName, src);
        }
      } else {
        List<CodeImportItem> items;
        if (src.startsWith("{")) {
          items = new ArrayList<>();
          String csv = src.substring(1, src.length() - 2);
          for (String item : csv.split(",")) {
            items.add(new BaseImportItem(item.trim(), alias));
          }
        } else {
          items = Collections.singletonList(new BaseImportItem(src, alias));
        }
        importStatement = new BaseImport(from, staticFlag, items);
      }
      List<CodeImport> declared = (List) this.file.getImports().getDeclared();
      declared.add(importStatement);
      return true;
    }
    return false;
  }

}