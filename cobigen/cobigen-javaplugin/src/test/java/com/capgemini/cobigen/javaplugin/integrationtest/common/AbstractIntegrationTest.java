package com.capgemini.cobigen.javaplugin.integrationtest.common;

import java.io.File;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.capgemini.cobigen.impl.PluginRegistry;
import com.capgemini.cobigen.impl.TemplateEngineRegistry;
import com.capgemini.cobigen.javaplugin.JavaPluginActivator;
import com.capgemini.cobigen.tempeng.freemarker.FreeMarkerTemplateEngine;

/**
 * Common Integration test implementation.
 */
public abstract class AbstractIntegrationTest {

    static {
        PluginRegistry.loadPlugin(JavaPluginActivator.class);
        TemplateEngineRegistry.register(FreeMarkerTemplateEngine.class);
    }

    /** Test configuration to CobiGen */
    protected File cobigenConfigFolder = new File("src/test/resources/testdata/integrationtest/templates");

    /** Temporary folder interface */
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

}
