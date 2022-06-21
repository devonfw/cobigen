package com.devonfw;

/**
 * Data access object for Visitor entities
 */
public class VisitorEntity extends ApplicationPersistenceEntity implements Visitor {

  private String username;

  private String name;

  @Column(name = "USERNAME")
  public String getUsername() {
    return this.username;
  }

  @Column(name = "USER")
  public String getName() {
    return this.Name;
  }

}
