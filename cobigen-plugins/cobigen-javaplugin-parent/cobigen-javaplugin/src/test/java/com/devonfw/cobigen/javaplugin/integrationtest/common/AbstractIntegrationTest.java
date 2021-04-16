package com.devonfw.cobigen.javaplugin.integrationtest.common;

import java.io.File;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Common Integration test implementation.
 */
public abstract class AbstractIntegrationTest {

    /** Test configuration to CobiGen */
    protected File cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/templates");

    /** Temporary folder interface */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

}
