package x_rootpackage_x.general.common.x_scope_x;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

/**
 * Interface for an entity of this application.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.ENTITY, constant = true)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_API) })
public interface ApplicationEntity {

  /**
   * @return the primary key of this entity.
   */
  Long getId();

  /**
   * @param id new value of {@link #getId()}.
   */
  void setId(Long id);

  /**
   * @return version
   */
  Integer getVersion();

  /**
   * @param version new value of {@link #getVersion()}.
   */
  void setVersion(Integer version);
}
