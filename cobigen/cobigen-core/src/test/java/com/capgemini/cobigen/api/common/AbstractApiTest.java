package com.capgemini.cobigen.api.common;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author mbrunnli (07.12.2014)
 */
public class AbstractApiTest {

    /**
     * Root path to all resources used in this test case
     */
    protected static String apiTestsRootPath = "src/test/resources/testdata/api/";

    /**
     * JUnit Rule to temporarily create files and folders, which will be automatically removed after test
     * execution
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
}
