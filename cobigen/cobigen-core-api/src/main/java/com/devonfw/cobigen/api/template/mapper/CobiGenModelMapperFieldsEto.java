package com.devonfw.cobigen.api.template.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenModelDefault;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.model.JavaStringType;

/**
 * Implementation of {@link CobiGenModelMapper} that {@link #map(CobiGenModel) maps} fields from entity to ETO.
 */
public class CobiGenModelMapperFieldsEto implements CobiGenModelMapper {

  @Override
  public CobiGenModel map(CobiGenModel model) {

    Type fieldType = CobiGenVariableDefinitions.FIELD_TYPE.getValue(model);
    if (fieldType instanceof Class<?>) {
      Class<?> fieldClass = (Class<?>) fieldType;
      if (Collection.class.isAssignableFrom(fieldClass)) {
        return null;
      } else if (Map.class.isAssignableFrom(fieldClass)) {
        return null;
      }
      String simpleName = fieldClass.getSimpleName();
      if (simpleName.endsWith("Entity")) {
        String qualifiedName = fieldClass.getName();
        if (qualifiedName.contains(".dataaccess.")) {
          qualifiedName = qualifiedName.replace(".dataaccess.", ".common.");
          qualifiedName = qualifiedName.substring(0, qualifiedName.length() - "Entity".length()) + "Eto";
          CobiGenModelDefault etoModel = new CobiGenModelDefault(model);
          CobiGenVariableDefinitions.FIELD_TYPE.setValue(etoModel, new JavaStringType(qualifiedName));
          String fieldName = CobiGenVariableDefinitions.FIELD_NAME.getValue(model);
          CobiGenVariableDefinitions.FIELD_NAME.setValue(etoModel, fieldName + "Id");
          return etoModel;
        }
      }
    }
    return model;
  }
}