package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.util.StringUtil;

/**
 * Implementation of {@link CobiGenGenerator} to generate getter methods from the fields.
 *
 */
public abstract class CobiGenGeneratorJavaType implements CobiGenGenerator {

  @Override
  public void generate(CobiGenModel model, Appendable code) throws IOException {

    Class<?> type = CobiGenVariableDefinitions.JAVA_TYPE.getValue(model);
    for (Field field : type.getDeclaredFields()) {
      if (acceptField(field)) {
        generate(field, model, code);
      }
    }
  }

  protected abstract void generate(Field field, CobiGenModel model, Appendable code) throws IOException;

  protected String getTypeName(Field field, CobiGenModel model) {

    Class<?> type = field.getType();
    if ("java.lang".equals(type.getPackageName())) {
      return type.getSimpleName();
    } else {
      // TODO we can not generate import statements at this point
      // this is one of the hundreds reasons for using mmm-code
      // further this will not work if generic types are used what can also only be solved with something like mmm-code
      // also we should use the model (or also pass the template) to determine if we are in the context of an ETO
      String qualifiedName = type.getName();
      if (qualifiedName.endsWith("Entity") && qualifiedName.contains(".dataaccess.")) {
        qualifiedName = qualifiedName.replace(".dataaccess.", ".common.");
        qualifiedName = qualifiedName.substring(0, qualifiedName.length() - "Entity".length()) + "Eto";
      } else if (Collection.class.isAssignableFrom(type)) {
        return null;
      }
      return qualifiedName;
    }
  }

  /**
   * @return {@code true} if the methods shall be generated as implementations with body, {@code false} otherwise if
   *         methods shall be generated as initial declaration in interface including JavaDoc.
   */
  protected boolean isImplementation() {

    // TODO could be determined automatically from the Template
    return true;
  }

  protected void generateField(Field field, CobiGenModel model, Appendable code) throws IOException {

    String type = getTypeName(field, model);
    if (type == null) {
      return;
    }
    code.append('\n');
    code.append("  private ");
    code.append(type);
    code.append(" ");
    code.append(field.getName());
    code.append(";\n");
  }

  protected void generateGetter(Field field, CobiGenModel model, Appendable code) throws IOException {

    String type = getTypeName(field, model);
    if (type == null) {
      return;
    }
    String name = field.getName();
    boolean implementation = isImplementation();
    if (implementation) {
      code.append("\n  @Override\n  public ");
    } else {
      code.append("\n" //
          + "  /**\n" //
          + "   * @return the ");
      code.append(name);
      code.append(".\n" //
          + "   */\n" //
          + "  ");
    }
    code.append(type);
    code.append(" ");
    code.append(getGetterPrefix(field, model));
    code.append(StringUtil.capFirst(name));
    if (implementation) {
      code.append("() {\n");
      code.append("    return this.");
      code.append(name);
      code.append(";\n  }\n");
    } else {
      code.append("();\n");
    }
  }

  protected String getGetterPrefix(Field field, CobiGenModel model) {

    if (field.getType() == boolean.class) {
      return "is";
    } else {
      return "get";
    }
  }

  protected void generateSetter(Field field, CobiGenModel model, Appendable code) throws IOException {

    String type = getTypeName(field, model);
    if (type == null) {
      return;
    }
    String name = field.getName();
    String capitalizedName = StringUtil.capFirst(name);
    boolean implementation = isImplementation();
    if (implementation) {
      code.append("\n  @Override\n  public void set");
    } else {
      code.append("\n" //
          + "  /**\n" //
          + "   * @param ");
      code.append(name);
      code.append(" the new value of {@link #");
      code.append(getGetterPrefix(field, model));
      code.append(capitalizedName);
      code.append("()}.\n" //
          + "   */\n" //
          + "  void set");
    }
    code.append(capitalizedName);
    code.append("(");
    code.append(type);
    code.append(" ");
    code.append(name);
    if (implementation) {
      code.append(") {\n" //
          + "    this.");
      code.append(name);
      code.append(" = ");
      code.append(name);
      code.append(";\n  }\n");
    } else {
      code.append(");\n");
    }
  }

  protected boolean acceptField(Field field) {

    return !Modifier.isStatic(field.getModifiers());
  }

}
