package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import net.sf.mmm.util.exception.api.DuplicateObjectException;
import net.sf.mmm.util.lang.api.AbstractSimpleDatatypeBase;
import net.sf.mmm.util.lang.api.StringUtil;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.api.ReflectionUtil;
import net.sf.mmm.util.reflect.base.ReflectionUtilImpl;

import io.oasp.module.basic.common.api.reflect.OaspPackage;
import io.oasp.module.basic.common.api.to.AbstractCto;
import io.oasp.module.basic.common.api.to.AbstractEto;
import io.oasp.module.basic.common.api.to.AbstractTo;

/**
 * CobiGenMarco with static helper methods for generation of JavaScript/TypeScript (and mappings from Java).
 */
public class CobiGenMacroJavaScriptHelper {

  private static final Map<Class<?>, JavaScriptType> JAVA_TO_JS_TYPE_MAP = new HashMap<>();

  static {
    registerTypeMapping(boolean.class, JavaScriptBasicType.BOOLEAN);
    registerTypeMapping(Boolean.class, JavaScriptBasicType.BOOLEAN);
    registerTypeMapping(String.class, JavaScriptBasicType.STRING);
    registerTypeMapping(char.class, JavaScriptBasicType.STRING);
    registerTypeMapping(String.class, JavaScriptBasicType.STRING);
    registerTypeMapping(Date.class, JavaScriptComplexType.DATE);
    registerTypeMapping(AbstractTo.class, JavaScriptComplexType.ABSTRACT_TO);
    registerTypeMapping(AbstractEto.class, JavaScriptComplexType.ABSTRACT_ETO);
    registerTypeMapping(AbstractCto.class, JavaScriptComplexType.ABSTRACT_CTO);
    try {
      Java8Support.init();
    } catch (Throwable e) {
      // ignore...
    }
  }

  /**
   * Allows to register project-specific custom mappings. You calling code needs to be executed on bootstrapping at the
   * beginning of template processing.
   *
   * @param javaType the {@link Class} reflecting the Java type to map.
   * @param jsType the corresponding {@link JavaScriptType} to map to.
   */
  public static void registerTypeMapping(Class<?> javaType, JavaScriptType jsType) {

    JavaScriptType duplicate = JAVA_TO_JS_TYPE_MAP.put(javaType, jsType);
    if ((duplicate != null) && (duplicate != jsType)) {
      throw new DuplicateObjectException(duplicate, javaType, jsType);
    }
  }

  /**
   * @param jsTypeSupplier a {@link Supplier} for the {@link JavaScriptType}s.
   * @return the {@link List} of import statements required for the supplied {@link JavaScriptType}s.
   */
  public static List<String> getImports(Supplier<Set<JavaScriptType>> jsTypeSupplier) {

    return getImports(jsTypeSupplier.get());
  }

  /**
   * @param jsTypes the {@link Set} of {@link JavaScriptType}s to be used.
   * @return the {@link List} of import statements required for the given {@link JavaScriptType}s.
   */
  public static List<String> getImports(Set<JavaScriptType> jsTypes) {

    Set<JavaScriptType> types2Import = new HashSet<>(jsTypes.size());
    for (JavaScriptType type : jsTypes) {
      if (!type.isPrimitive()) {
        JavaScriptType jsType = type.getNonArrayType();
        if (jsType.getQualifiedName() != null) {
          types2Import.add(jsType);
        }
      }
    }
    List<String> importStatements = new ArrayList<>(types2Import.size());
    for (JavaScriptType type : types2Import) {
      importStatements.add(getImportStatement(type));
    }
    return importStatements;
  }

  /**
   * @param jsType the {@link JavaScriptType} to import.
   * @return the according JS/TS import statement (without newline).
   */
  public static String getImportStatement(JavaScriptType jsType) {

    return "import {" + jsType.getSimpleName() + "} from '" + jsType.getQualifiedName() + "';";
  }

  /**
   * @param javaTypes the Java {@link Type}s to be used as corresponding JS/TS types.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the {@link List} of import statements required for the given {@link Type}s.
   */
  public static List<String> getImportStatements(Collection<? extends Type> javaTypes, Class<?> pojoClass) {

    Set<JavaScriptType> types2Import = toJavaScriptTypes(javaTypes, pojoClass);
    return getImports(types2Import);
  }

  /**
   * @param javaTypes the Java {@link Type}s to be used as corresponding JS/TS types.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the {@link Set} of {@link JavaScriptType} corresponding to the given Java {@link Type}s.
   */
  public static Set<JavaScriptType> toJavaScriptTypes(Collection<? extends Type> javaTypes, Class<?> pojoClass) {

    Set<JavaScriptType> jsTypes = new HashSet<>(javaTypes.size());
    for (Type type : javaTypes) {
      jsTypes.add(toJavaScriptType(type, pojoClass));
    }
    return jsTypes;
  }

  /**
   * @param javaType the {@link Type} reflecting the Java type to map.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the {@link JavaScriptType} corresponding to the given Java {@link Type}.
   */
  public static JavaScriptType toJavaScriptType(Type javaType, Class<?> pojoClass) {

    if (javaType instanceof GenericType<?>) {
      return toJavaScriptType((GenericType<?>) javaType, pojoClass);
    } else if (javaType instanceof Class) {
      return toJavaScriptType((Class<?>) javaType, pojoClass);
    } else {
      return toJavaScriptType(ReflectionUtilImpl.getInstance().createGenericType(javaType), pojoClass);
    }
  }

  /**
   * @param javaType the {@link GenericType} reflecting the Java type to map.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the {@link JavaScriptType} corresponding to the given Java {@link Type}.
   */
  public static JavaScriptType toJavaScriptType(GenericType<?> javaType, Class<?> pojoClass) {

    GenericType<?> componentType = javaType.getComponentType();
    if (componentType != null) {
      return new JavaScriptArrayType(toJavaScriptType(componentType, pojoClass));
    }
    Class<?> javaClass = javaType.getAssignmentClass();
    int typeArgumentCount = javaType.getTypeArgumentCount();
    if (typeArgumentCount > 0) {
      JavaScriptType[] jsTypes = new JavaScriptType[typeArgumentCount];
      for (int i = 0; i < typeArgumentCount; i++) {
        jsTypes[i] = toJavaScriptType(javaType.getTypeArgument(i), pojoClass);
      }
      String path = buildTypePath(javaClass, pojoClass);
      return new JavaScriptGenericType(path, javaClass.getSimpleName(), jsTypes);
    }
    return toJavaScriptType(javaClass, pojoClass);
  }

  /**
   * @param javaClass the {@link Class} reflecting the Java type to map.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the {@link JavaScriptType} corresponding to the given Java {@link Type}.
   */
  public static JavaScriptType toJavaScriptType(Class<?> javaClass, Class<?> pojoClass) {

    JavaScriptType jsType = JAVA_TO_JS_TYPE_MAP.get(javaClass);
    if (jsType != null) {
      return jsType;
    }
    if (javaClass.isPrimitive()) {
      return toJavaScriptType(ReflectionUtilImpl.getInstance().getNonPrimitiveType(javaClass), pojoClass);
    }
    Class<?> javaType = unwrapType(javaClass);
    if (Number.class.isAssignableFrom(javaType)) {
      return JavaScriptBasicType.NUMBER;
    } else if (Boolean.class.isAssignableFrom(javaType)) {
      return JavaScriptBasicType.BOOLEAN;
    } else if (String.class.isAssignableFrom(javaType)) {
      return JavaScriptBasicType.STRING;
    } else if ((javaType == Object.class) || Map.class.isAssignableFrom(javaType)) {
      return JavaScriptBasicType.OBJECT;
    } else if (Collection.class.isAssignableFrom(javaType)) {
      return JavaScriptBasicType.ARRAY;
    } else if (javaType.isArray()) {
      Class<?> javaComponentType = javaType.getComponentType();
      if (javaComponentType == Object.class) {
        return JavaScriptBasicType.ARRAY;
      }
      JavaScriptType jsComponentType = toJavaScriptType(javaComponentType, pojoClass);
      return new JavaScriptArrayType(jsComponentType);
    } else if (javaType.getPackage().getName().equals("java.time")) {
      return JavaScriptBasicType.STRING;
    }
    return toJavaScriptComplexType(javaType, pojoClass);
  }

  /**
   * @param javaClassname the {@link Class#getName() qualified name} of the {@link Class} to map.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the {@link JavaScriptType} corresponding to the given {@code javaClassname}.
   */
  public static String toJavaScriptTypeString(String javaClassname, Class<?> pojoClass) {

    Class<?> javaClass;
    try {
      javaClass = CobiGenMacroJavaScriptHelper.class.getClassLoader().loadClass(javaClassname);
      return toJavaScriptType(javaClass, pojoClass).getSimpleName();
    } catch (ClassNotFoundException e) {
      int lastDot = javaClassname.lastIndexOf('.');
      if (lastDot > 0) {
        return javaClassname.substring(lastDot + 1);
      }
      return javaClassname;
    }
  }

  private static Class<?> unwrapType(Class<?> javaClass) {

    if (AbstractSimpleDatatypeBase.class.isAssignableFrom(javaClass)) {
      TypeVariable<?> typeVariable = AbstractSimpleDatatypeBase.class.getTypeParameters()[0];
      ReflectionUtil util = ReflectionUtilImpl.getInstance();
      GenericType<?> genericType = util.createGenericType(typeVariable, javaClass);
      return genericType.getRetrievalClass();
    }
    return javaClass;
  }

  private static JavaScriptType toJavaScriptComplexType(Class<?> javaClass, Class<?> pojoClass) {

    String path = buildTypePath(javaClass, pojoClass);
    if (javaClass.isEnum()) {
      return new JavaScriptEnumType(path, javaClass.getSimpleName());
    } else {
      return new JavaScriptComplexType(path, javaClass.getSimpleName());
    }
  }

  private static String buildTypePath(Class<?> javaClass, Class<?> pojoClass) {

    if (pojoClass == null) {
      return "./";
    } else {
      OaspPackage pkg = OaspPackage.of(javaClass);
      OaspPackage pojoPkg = OaspPackage.of(pojoClass);
      String application = pkg.getApplication();
      String path;
      if (Objects.equals(application, pojoPkg.getApplication())) {
        Path targetPath = getPgkPath(pkg);
        Path pojoPath = getPgkPath(pojoPkg);
        Path relativePath = pojoPath.relativize(targetPath);
        path = relativePath.toString().replace('\\', '/');
        if (!path.startsWith("..")) {
          if (path.startsWith("/")) {
            path = "." + path;
          } else {
            path = "./" + path;
          }
        }
      } else {
        path = application + ":" + pkg.getComponent() + "/" + pkg.getLayer() + "/";
        String detail = pkg.getDetail();
        if (detail != null) {
          path = path + detail.replace('.', '/') + "/";
        }
      }
      return path;
    }
  }

  private static Path getPgkPath(OaspPackage pkg) {

    if (!pkg.isValidLayer()) {
      return Paths.get("", pkg.toString().split("\\."));
    }
    String[] details = getDetails(pkg);
    int length = details.length;
    String[] paths = new String[length + 2];
    paths[0] = pkg.getComponent();
    paths[1] = pkg.getLayer();
    for (int i = 0; i < paths.length; i++) {
      if (paths[i] == null) {
        paths[i] = "";
      }
    }
    System.arraycopy(details, 0, paths, 2, length);
    String app = pkg.getApplication();
    if (app == null) {
      app = "";
    }
    return Paths.get(app, paths);
  }

  private static String[] getDetails(OaspPackage pkg) {

    String detail = pkg.getDetail();
    if (detail == null) {
      return StringUtil.EMPTY_STRING_ARRAY;
    } else {
      return detail.split("\\.");
    }
  }

  /**
   * @param javaType the {@link GenericType} of the property.
   * @param fieldName the name of the property.
   * @param pojoClass the {@link Class} reflecting the input of the CobiGen generation. Used to be able to generate
   *        relative imports.
   * @return the TypeScript assignment for JSON-deserialization of the property.
   */
  public static String toJsonAssignment(GenericType<?> javaType, String fieldName, Class<?> pojoClass) {

    StringBuilder buffer = new StringBuilder();
    toJsonAssignment(toJavaScriptType(javaType, pojoClass), fieldName, "json." + fieldName, buffer);
    return buffer.toString();
  }

  private static void toJsonAssignment(JavaScriptType jsType, String fieldName, String jsonFieldName,
      StringBuilder buffer) {

    if (jsType.isPrimitive()) {
      buffer.append(jsonFieldName);
      buffer.append(';');
      return;
    }
    buffer.append("JsonObjectMapper.getInstance().deserialize");
    JavaScriptType componentType = jsType.getComponentType();
    if (componentType == null) {
      buffer.append("Object(");
      buffer.append(jsType.getSimpleName());
    } else {
      buffer.append("Array(");
      buffer.append(componentType.getSimpleName());
    }
    buffer.append(", ");
    buffer.append(jsonFieldName);
    buffer.append(");");
  }

}
