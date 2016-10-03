package com.capgemini.cobigen.javaplugin.integrationtest.common;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.impl.PluginRegistry;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;

/**
 * Common Integration test implementation.
 */
public abstract class AbstractIntegrationTest {

    /**
     * Test configuration to CobiGen
     */
    protected File cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/templates");

    /**
     * Temporary folder interface
     */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Common test setup
     * @author mbrunnli (25.10.2014)
     */
    @Before
    public void setup() {
        PluginRegistry.loadPlugin(JavaPluginActivator.class);
    }
}
