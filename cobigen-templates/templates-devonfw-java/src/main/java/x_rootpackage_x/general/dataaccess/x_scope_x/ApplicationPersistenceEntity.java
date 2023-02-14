package x_rootpackage_x.general.dataaccess.x_scope_x;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

import x_rootpackage_x.general.common.x_scope_x.ApplicationEntity;

/**
 * Abstract base class for a persistent (JPA) entity of this application.
 */
@MappedSuperclass
@CobiGenTemplate(value = CobiGenJavaIncrements.ENTITY, constant = true)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class ApplicationPersistenceEntity implements ApplicationEntity {

  @Id
  @GeneratedValue
  private Long id;

  @Version
  private Integer version;

  @Override
  public Long getId() {

    return this.id;
  }

  @Override
  public void setId(Long id) {

    this.id = id;
  }

  @Override
  public Integer getVersion() {

    return this.version;
  }

  @Override
  public void setVersion(Integer version) {

    this.version = version;
  }
}
