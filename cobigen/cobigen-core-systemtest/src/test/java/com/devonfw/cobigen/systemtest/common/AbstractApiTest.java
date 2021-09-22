package com.devonfw.cobigen.systemtest.common;

import java.util.Collection;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.to.TemplateTo;

/**
 * Abstract test implementation providing cross-cutting functionality and properties.
 */
public abstract class AbstractApiTest {

  /**
   * Root path to all resources used in this test case
   */
  protected static String apiTestsRootPath = "src/test/resources/testdata/systemtest/";

  /**
   * JUnit Rule to temporarily create files and folders, which will be automatically removed after test execution
   */
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * Search for template by id
   *
   * @param templates list of templates
   * @param id to search for
   * @return the first template, with the given id or <code>null</code> if not found
   */
  public TemplateTo getTemplateById(Collection<TemplateTo> templates, String id) {

    for (TemplateTo template : templates) {
      if (template.getId().equals(id)) {
        return template;
      }
    }
    return null;
  }
}
