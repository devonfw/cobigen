package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.primarykeys;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * TODO leholm This type ...
 *
 */
public class Primaryfour {
  private Long testId;

  @Id
  @Column(name = "TEST_ID", length = 50, nullable = false)
  public Long getTestId() {

    return this.testId;
  }
}
