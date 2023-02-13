package com.devonfw.cobigen.api.template.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import com.devonfw.cobigen.api.code.CobiGenCodeProperty;
import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.template.out.CobiGenOutput;

/**
 * {@link CobiGenOutputGenerator} to generate properties of an ETO.
 */
public class CobiGenGenertorJavaTypeEtoProperties extends CobiGenGenertorJavaTypeProperties {

  @Override
  protected CobiGenCodeProperty map(Field field, CobiGenOutput out, CobiGenModel model) {

    if (Modifier.isStatic(field.getModifiers())) {
      return null; // ignore static fields.
    }
    String name = field.getName();
    Class<?> type = field.getType();
    String typeName = type.getName();
    if (typeName.endsWith("Entity") && typeName.contains(".dataaccess.")) {
      typeName = typeName.replace(".dataaccess.", ".common.");
      typeName = typeName.substring(0, typeName.length() - "Entity".length()) + "Eto";
      name = name + "Id";
    } else if (Collection.class.isAssignableFrom(type)) {
      return null;
    }
    String description = null;
    if (out.isInterface()) {
      description = "the " + name; // kind of hack, only generate JavaDoc in case of interface
    }
    return new CobiGenCodeProperty(name, typeName, description);
  }

}
