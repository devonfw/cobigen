package com.devonfw.cobigen.api.template.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;

/**
 * Implementation of {@link CobiGenCollectionProvider} to {@link #get(CobiGenModel) get} a {@link List} of the
 * non-static {@link Field}s.
 */
public class CobiGenCollectionProviderFields implements CobiGenCollectionProvider {

  @Override
  public Collection<CobiGenModel> get(CobiGenModel model) {

    Class<?> type = CobiGenVariableDefinitions.JAVA_TYPE.getValue(model);
    Field[] fields = type.getDeclaredFields();
    List<CobiGenModel> result = new ArrayList<>(fields.length);
    for (Field field : fields) {
      if (acceptField(field)) {
        result.add(new JavaFieldModel(field, model));
      }
    }
    return result;
  }

  private boolean acceptField(Field field) {

    return !Modifier.isStatic(field.getModifiers());
  }

}
