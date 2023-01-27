package x_rootpackage_x.general.common;

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

}
