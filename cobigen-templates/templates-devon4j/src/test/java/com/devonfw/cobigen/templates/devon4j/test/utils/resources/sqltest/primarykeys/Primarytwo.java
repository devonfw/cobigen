package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * TODO leholm This type ...
 *
 */
public class Primarytwo {
  @Id
  @Column(name = "TEST_ID", length = 50, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

}
