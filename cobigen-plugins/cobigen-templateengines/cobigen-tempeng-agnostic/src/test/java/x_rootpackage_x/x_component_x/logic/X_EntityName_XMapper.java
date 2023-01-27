package x_rootpackage_x.x_component_x.logic;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import x_rootpackage_x.x_component_x.common.X_EntityName_X;
import x_rootpackage_x.x_component_x.common.X_EntityName_XEto;
import x_rootpackage_x.x_component_x.dataaccess.X_EntityName_XEntity;

/**
 * {@link Mapper} for {@link X_EntityName_X}.
 */
@Mapper(componentModel = "cdi")
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
