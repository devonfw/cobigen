package x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_X;
import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_XEto;
import x_rootpackage_x.x_component_x.dataaccess.x_scope_x.x_detail_x.X_EntityName_XEntity;

/**
 * {@link Mapper} for {@link X_EntityName_X}.
 */
@Mapper(componentModel = "cdi")
@CobiGenTemplate(value = CobiGenJavaIncrements.LOGIC)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_IMPL),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public interface X_EntityName_XMapper {

  /**
   * @param items the {@link List} of {@link X_EntityName_XEntity}-objects to convert.
   * @return the {@link List} of converted {@link X_EntityName_XEto}s.
   */
  default List<X_EntityName_XEto> toEtos(List<X_EntityName_XEntity> items) {

    List<X_EntityName_XEto> etos = new ArrayList<>(items.size());
    for (X_EntityName_XEntity item : items) {
      etos.add(toEto(item));
    }
    return etos;
  }

  /**
   * @param items the {@link List} of {@link X_EntityName_XEto}s to convert.
   * @return the {@link List} of converted {@link X_EntityName_XEntity}-objects.
   */
  default List<X_EntityName_XEntity> toEntities(List<X_EntityName_XEto> items) {

    List<X_EntityName_XEntity> entities = new ArrayList<>(items.size());
    for (X_EntityName_XEto item : items) {
      entities.add(toEntity(item));
    }
    return entities;
  }

  /**
   * @param item the {@link X_EntityName_XEntity} to map.
   * @return the mapped {@link X_EntityName_XEto}.
   */
  X_EntityName_XEto toEto(X_EntityName_XEntity item);

  /**
   * @param item the {@link X_EntityName_XEto} to map.
   * @return the mapped {@link X_EntityName_XEntity}.
   */
  X_EntityName_XEntity toEntity(X_EntityName_XEto item);
}
