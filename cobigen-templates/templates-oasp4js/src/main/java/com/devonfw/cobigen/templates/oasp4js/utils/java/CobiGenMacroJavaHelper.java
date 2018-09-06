package com.devonfw.cobigen.templates.oasp4js.utils.java;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.mmm.util.pojo.descriptor.api.PojoDescriptor;
import net.sf.mmm.util.pojo.descriptor.api.PojoDescriptorBuilder;
import net.sf.mmm.util.pojo.descriptor.impl.PojoDescriptorBuilderFactoryImpl;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.base.ReflectionUtilImpl;

/**
 * CobiGenMarco with static helper methods for Java specific generation logic.
 */
public class CobiGenMacroJavaHelper {

  /**
   * @param type the {@link Class} to introspect.
   * @return the given {@link Class} wrapped as {@link JavaBean}.
   */
  public static JavaBean createBean(Class<?> type) {

    PojoDescriptor<?> descriptor = DESCRIPTOR_BUILDER.getDescriptor(type);
    return new JavaBean(descriptor);
  }

  /**
   * @param types the {@link Collection} with the required {@link Type}s.
   * @return a {@link List} with the import statements required to use the given {@code types}.
   */
  public static List<String> getImportStatements(Collection<? extends Type> types) {

    Set<Class<?>> types2Import = new HashSet<>(types.size());
    for (Type type : types) {
      collectTypes2Import(type, types2Import);
    }
    List<String> importStatements = new ArrayList<>(types2Import.size());
    for (Class<?> type : types2Import) {
      importStatements.add("import " + type.getCanonicalName() + ";");
    }
    return importStatements;
  }

  /**
   * @param type the {@link Class} to check.
   * @return {@code true} if the class needs to be imported, {@code false} otherwise (if {@link Class#isPrimitive()
   *         primitive}, {@link Class#getPackage() package} is "java.lang", etc.).
   */
  public static boolean requiresImport(Class<?> type) {

    if ((type == null) || type.isArray() || type.isPrimitive()) {
      return false;
    }
    Package pkg = type.getPackage();
    if (pkg == null) {
      return false;
    }
    String pkgName = pkg.getName();
    if ((pkgName == null) || (pkgName.isEmpty()) || (pkgName.equals("java.lang"))) {
      return false;
    }
    if (type.isAnonymousClass()) {
      return false;
    }
    if (type.getCanonicalName() == null) {
      return false;
    }
    return true;
  }

  private static void collectTypes2Import(Type type, Set<Class<?>> types2Import) {

    if (type instanceof GenericType<?>) {
      collectTypes2Import((GenericType<?>) type, types2Import);
    } else if (type instanceof Class) {
      collectTypes2Import((Class<?>) type, types2Import);
    } else {
      collectTypes2Import(ReflectionUtilImpl.getInstance().createGenericType(type), types2Import);
    }
  }

  private static void collectTypes2Import(Class<?> type, Set<Class<?>> types2Import) {

    if (type.isArray()) {
      collectTypes2Import(type.getComponentType(), types2Import);
    } else if (requiresImport(type)) {
      types2Import.add(type);
    }
  }

  private static void collectTypes2Import(GenericType<?> type, Set<Class<?>> types2Import) {

    collectTypes2Import(type.getAssignmentClass(), types2Import);
    for (int i = 0; i < type.getTypeArgumentCount(); i++) {
      collectTypes2Import(type.getTypeArgument(i), types2Import);
    }
  }

  private static final PojoDescriptorBuilder DESCRIPTOR_BUILDER =
      PojoDescriptorBuilderFactoryImpl.getInstance().createPublicMethodDescriptorBuilder();

}
