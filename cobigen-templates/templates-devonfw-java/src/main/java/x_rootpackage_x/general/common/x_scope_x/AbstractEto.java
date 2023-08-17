package x_rootpackage_x.general.common.x_scope_x;

import java.util.Objects;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

/**
 * Abstract base class for an Entity Transfer Object (ETO).
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.ENTITY, constant = true)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_API) })
public abstract class AbstractEto implements ApplicationEntity {

  private Long id;

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

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = (this.id == null) ? 0 : this.id.hashCode();
    result = prime * result;
    if (this.version != null) {
      result = result + this.version.intValue();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    } else if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    AbstractEto other = (AbstractEto) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    } else if (!Objects.equals(this.version, other.version)) {
      return false;
    }
    return true;
  }
}
