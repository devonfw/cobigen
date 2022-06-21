package com.devonfw.cobigen.javaplugin.merger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.util.StringUtil;
import com.devonfw.cobigen.api.util.SystemUtil;
import com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil;
import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;
import com.thoughtworks.qdox.parser.ParseException;

/**
 * The {@link JavaMerger} merges a patch and the base file of the same class. This merge is a structural merge
 * considering code blocks of fields, methods and inner classes. There will be no merging on statement level
 *
 * @author mbrunnli (19.03.2013)
 */
public class JavaMerger implements Merger {

  /**
   * Merger Type to be registered
   */
  private String type;

  /**
   * The conflict resolving mode
   */
  private boolean patchOverrides;

  /**
   * Creates a new {@link JavaMerger}
   *
   * @param type merger type
   * @param patchOverrides if <code>true</code>, conflicts will be resolved by using the patch contents<br>
   *        if <code>false</code>, conflicts will be resolved by using the base contents
   * @author mbrunnli (19.03.2013)
   */
  public JavaMerger(String type, boolean patchOverrides) {

    this.type = type;
    this.patchOverrides = patchOverrides;
  }

  @Override
  public String getType() {

    return this.type;
  }

  @Override
  public String merge(File base, String patch, String targetCharset) throws MergeException {

    ModifyableJavaClass baseClass;
    String lineDelimiter;
    Path path = Paths.get(base.getAbsolutePath());

    try (FileInputStream stream = new FileInputStream(path.toString());
        BufferedInputStream bis = new BufferedInputStream(stream);
        InputStreamReader reader = new InputStreamReader(bis, targetCharset)) {

      baseClass = (ModifyableJavaClass) JavaParserUtil.getFirstJavaClass(reader);
      lineDelimiter = SystemUtil.determineLineDelimiter(path, targetCharset);

    } catch (IOException e) {
      throw new MergeException(base, "Cannot read base file.", e);
    } catch (ParseException e) {
      throw new MergeException(base, "The syntax of the base file is invalid. Error in line: " + e.getLine()
          + " / column: " + e.getColumn() + ": " + e.getMessage(), e);
    }

    ModifyableJavaClass patchClass;
    try (StringReader reader = new StringReader(patch)) {

      patchClass = (ModifyableJavaClass) JavaParserUtil.getFirstJavaClass(reader);
    } catch (ParseException e) {
      throw new MergeException(base, "The syntax of the generated patch is invalid. Error in line: " + e.getLine()
          + " / column: " + e.getColumn() + ": " + e.getMessage(), e);
    }

    if (baseClass == null) {
      throw new MergeException(base, "The base file does not declare a valid JavaClass.");
    } else if (patchClass == null) {
      throw new MergeException(base, "The patch does not declare a valid JavaClass.");
    }

    ModifyableJavaClass mergedClass = merge(baseClass, patchClass);
    return StringUtil.consolidateLineEndings(mergedClass.getSource().getCodeBlock(), lineDelimiter);
  }

  /**
   * Merges the two classes
   *
   * @return the merged {@link JavaClass}
   * @param baseClass {@link JavaClass}
   * @param patchClass {@link JavaClass}
   * @author mbrunnli (19.03.2013)
   */
  private ModifyableJavaClass merge(ModifyableJavaClass baseClass, ModifyableJavaClass patchClass) {

    baseClass.setAnnotations(mergeAnnotation(baseClass.getAnnotations(), patchClass.getAnnotations()));
    mergeImports(baseClass, patchClass);
    mergeFields(baseClass, patchClass);
    mergeInnerClasses(baseClass, patchClass);
    mergeMethods(baseClass, patchClass);
    mergeSupertypes(baseClass, patchClass);
    return baseClass;
  }

  /**
   * @param baseAnnotations {@link JavaClass} a List of annotations originate from the base class
   * @param patchAnnotations {@link JavaClass} a List of annotations originate from the patch class
   */
  private List<JavaAnnotation> mergeAnnotation(List<JavaAnnotation> baseAnnotations,
      List<JavaAnnotation> patchAnnotations) {

    List<JavaAnnotation> bAnnotations = this.patchOverrides ? patchAnnotations : baseAnnotations;
    List<JavaAnnotation> pAnnotations = this.patchOverrides ? baseAnnotations : patchAnnotations;

    Set<String> annotationNames = bAnnotations.stream().map(a -> a.getType().getName()).collect(Collectors.toSet());

    List<JavaAnnotation> annotationsToMerge = pAnnotations.stream()
        .filter(a -> !annotationNames.contains(a.getType().getName())).collect(Collectors.toList());

    return Stream.of(bAnnotations, annotationsToMerge).flatMap(a -> a.stream()).collect(Collectors.toList());
  }

  /**
   * Merges all super types of the two class sources
   *
   * @param baseClass {@link JavaClass}
   * @param patchClass {@link JavaClass}
   * @author mbrunnli (03.06.2013)
   */
  private void mergeSupertypes(ModifyableJavaClass baseClass, ModifyableJavaClass patchClass) {

    if (this.patchOverrides) {
      baseClass.setImplementz(patchClass.getInterfaces());
      if (patchClass.getSuperClass() != null
          && !patchClass.getSuperClass().getCanonicalName().equals("java.lang.Enum")) {
        baseClass.setSuperClass(patchClass.getSuperClass());
      }
    } else {
      List<JavaClass> baseClassInterfaces = baseClass.getInterfaces();
      for (JavaClass pClass : patchClass.getInterfaces()) {
        // TODO funktioniert noch nicht, da super klassen nicht im QDox Modell sind
        if (!baseClassInterfaces.contains(pClass) && !baseClass.isA(pClass)) {
          baseClassInterfaces.add(pClass);
        }
      }
      baseClass.setImplementz(baseClassInterfaces);
      if (baseClass.getSuperClass() == null
          || baseClass.getSuperClass().getCanonicalName().equals("java.lang.Object")) {
        baseClass.setSuperClass(patchClass.getSuperClass());
      }
    }
  }

  /**
   * Merges all imports of the two class sources
   *
   * @param baseClass {@link JavaClass}
   * @param patchClass {@link JavaClass}
   * @author mbrunnli (05.04.2013)
   */
  private void mergeImports(ModifyableJavaClass baseClass, ModifyableJavaClass patchClass) {

    for (String patchImport : patchClass.getSource().getImports()) {
      List<String> baseImports = baseClass.getSource().getImports();
      String conflictingBaseImport = null;
      for (String baseImport : baseImports) {
        if (getShortTypeName(patchImport).equals(getShortTypeName(baseImport))) {
          conflictingBaseImport = baseImport;
          break;
        }
      }
      if (conflictingBaseImport != null) {
        if (this.patchOverrides) {
          int i = baseImports.indexOf(conflictingBaseImport);
          baseImports.set(i, patchImport);
        } // else do not override
      } else {
        baseClass.getSource().getImports().add(patchImport);
      }
    }
  }

  /**
   * Shortens a canonical type name to the type name itself
   *
   * @param canonicalName to be shortend
   * @return the Type name
   * @author mbrunnli
   */
  private String getShortTypeName(String canonicalName) {

    if (canonicalName.lastIndexOf(".") != -1) {
      return canonicalName.substring(canonicalName.lastIndexOf(".") + 1);
    } else {
      return canonicalName;
    }
  }

  /**
   * Merges all inner {@link JavaClass}es of the given {@link JavaClass}es
   *
   * @param baseClass {@link JavaClass}
   * @param patchClass {@link JavaClass}
   * @author mbrunnli (19.03.2013)
   */
  private void mergeInnerClasses(ModifyableJavaClass baseClass, ModifyableJavaClass patchClass) {

    for (JavaClass rawInnerPatchClass : patchClass.getNestedClasses()) {
      ModifyableJavaClass innerPatchClass = (ModifyableJavaClass) rawInnerPatchClass;
      ModifyableJavaClass nestedBaseClass = (ModifyableJavaClass) baseClass
          .getNestedClassByName(innerPatchClass.getName());
      if (nestedBaseClass == null) {
        baseClass.addClass(innerPatchClass);
      } else {
        merge(nestedBaseClass, innerPatchClass);
      }
    }
  }

  /**
   * Merges all fields of the given {@link JavaClass}es
   *
   * @param baseClass {@link JavaClass}
   * @param patchClass {@link JavaClass}
   * @author mbrunnli (19.03.2013)
   */
  private void mergeFields(ModifyableJavaClass baseClass, ModifyableJavaClass patchClass) {

    for (JavaField patchField : patchClass.getFields()) {
      JavaField baseField = baseClass.getFieldByName(patchField.getName());
      if (baseField == null) {
        baseClass.addField(patchField);
      } else {
        ((DefaultJavaField) baseField)
            .setAnnotations(new ArrayList<>(mergeAnnotation(baseField.getAnnotations(), patchField.getAnnotations())));
        if (this.patchOverrides) {
          baseClass.replace(baseField, patchField);
        }
      }
    }
  }

  /**
   * Merges all methods of the given {@link JavaClass}es
   *
   * @param baseClass {@link JavaClass}
   * @param patchClass {@link JavaClass}
   * @author mbrunnli (19.03.2013)
   */
  private void mergeMethods(ModifyableJavaClass baseClass, ModifyableJavaClass patchClass) {

    // merge all non-conflicting imports from (final) base class to patch, to check for conflicting
    // method signatures
    mergeImports(patchClass, baseClass);

    for (JavaConstructor patchConstructor : patchClass.getConstructors()) {
      JavaConstructor baseConstructor = baseClass.getConstructor(patchConstructor.getParameterTypes());
      if (baseConstructor == null) {
        baseClass.addConstructor(patchConstructor);
      } else {
        if (this.patchOverrides) {
          baseClass.replace(baseConstructor, patchConstructor);
        } // else do not override
      }
    }

    List<JavaInitializer> InitializersToAdd = new ArrayList<>();

    for (JavaInitializer patchInitializerBlock : patchClass.getInitializers()) {

      for (JavaInitializer baseInitializerBlock : baseClass.getInitializers()) {
        if (getBlock(baseInitializerBlock).equals(getBlock(patchInitializerBlock))) {
          if (this.patchOverrides) {
            baseClass.replace(baseInitializerBlock, patchInitializerBlock);
          }

        } else {
          InitializersToAdd.add(patchInitializerBlock);
        }

      }

    }

    baseClass.addAllInitializer(InitializersToAdd);

    for (JavaMethod patchMethod : patchClass.getMethods()) {
      JavaMethod baseMethod = baseClass.getMethodBySignature(patchMethod.getName(),
          patchMethod.getParameterTypes(true));
      if (baseMethod == null) {
        baseClass.addMethod(patchMethod);
      } else {
        ((DefaultJavaMethod) baseMethod)
            .setAnnotations(mergeAnnotation(baseMethod.getAnnotations(), patchMethod.getAnnotations()));
        if (this.patchOverrides) {
          baseClass.replace(baseMethod, patchMethod);
        }
      }
    }

  }

  /**
   * @param initializer JavaInitializer
   * @return Contents of the Initializer block removing white space and new line
   */
  private String getBlock(JavaInitializer initializer) {

    return initializer.getBlockContent().replaceAll("\\s", "");

  }

}
