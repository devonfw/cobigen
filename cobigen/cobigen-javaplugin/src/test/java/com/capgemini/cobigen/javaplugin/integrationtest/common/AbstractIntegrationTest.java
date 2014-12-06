package com.capgemini.cobigen.javaplugin.integrationtest.common;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;

/**
 *
 * @author mbrunnli (06.12.2014)
 */
public abstract class AbstractIntegrationTest {

    /**
     * Test configuration to CobiGen
     */
    protected File cobigenConfigFolder = new File(
        "src/test/resources/com/capgemini/cobigen/javaplugin/integrationtest/templates");

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
