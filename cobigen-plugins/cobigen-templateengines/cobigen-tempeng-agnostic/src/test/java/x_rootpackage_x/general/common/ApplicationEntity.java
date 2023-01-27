package x_rootpackage_x.general.common;

/**
 * Interface for an entity of this application.
 */
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
