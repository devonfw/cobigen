package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * TODO leholm This type ...
 *
 */
public class Primarytwo {
  @Id
  @Column(name = "TEST_ID", length = 50, nullable = false)
  private Long id;

}
