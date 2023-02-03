package x_rootpackage_x.general.common;

import com.google.common.base.Objects;

/**
 * Abstract base class for an Entity Transfer Object (ETO).
 */
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
    if (!Objects.equal(this.id, other.id)) {
      return false;
    } else if (!Objects.equal(this.version, other.version)) {
      return false;
    }
    return true;
  }
}
