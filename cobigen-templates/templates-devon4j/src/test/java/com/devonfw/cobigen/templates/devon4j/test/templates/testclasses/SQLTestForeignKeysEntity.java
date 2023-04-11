package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Test entity to test the correct generation of the enumerated type, the primary key, and name overriding
 *
 */
@Entity
@Table(name = "SQLTEST")
public class SQLTestForeignKeysEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @OneToOne()
  @JoinColumn(referencedColumnName = "MY_ID_FIELD", name = "test_id")
  private SQLTestEntity sqlTestEntity;

  /**
   * @return id
   */
  public Long getId() {

    return this.id;
  }

  /**
   * @param id new value of {@link #getid}.
   */
  public void setId(Long id) {

    this.id = id;
  }

  /**
   * @return sqlTestEntity
   */
  public SQLTestEntity getSqlTestEntity() {

    return this.sqlTestEntity;
  }

  /**
   * @param sqlTestEntity new value of {@link #getsqlTestEntity}.
   */
  public void setSqlTestEntity(SQLTestEntity sqlTestEntity) {

    this.sqlTestEntity = sqlTestEntity;
  }

}
