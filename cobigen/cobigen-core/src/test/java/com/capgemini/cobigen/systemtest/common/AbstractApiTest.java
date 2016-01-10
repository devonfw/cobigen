package com.capgemini.cobigen.systemtest.common;

import java.util.Collection;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.extension.to.TemplateTo;

/**
 *
 * @author mbrunnli (07.12.2014)
 */
public class AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    protected static String apiTestsRootPath = "src/test/resources/testdata/systemtest/";

    /**
     * JUnit Rule to temporarily create files and folders, which will be automatically removed after test
     * execution
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Search for template by id
     * @param templates
     *            list of templates
     * @param id
     *            to search for
     * @return the first template, with the given id or <code>null</code> if not found
     * @author mbrunnli (Dec 20, 2015)
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
