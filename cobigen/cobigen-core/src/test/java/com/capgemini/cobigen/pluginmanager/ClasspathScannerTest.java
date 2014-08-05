package com.capgemini.cobigen.pluginmanager;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ClasspathScannerTest {

    @Test
    @Ignore("Logic not yet implemented")
    public void testePluginRegistering() throws ClassNotFoundException {

        // getClass().getClassLoader().loadClass("ClasspathScannerTest.TestMerger");
        // getClass().getClassLoader().loadClass("ClasspathScannerTest.TestTriggerInterpreter");
        // getClass().getClassLoader().loadClass("ClasspathScannerTest.TestPlugin");

        ClasspathScanner.scanClasspathAndRegisterPlugins();

        Assert.assertNotNull("TestMerger not registered", PluginRegistry.getMerger("TestMerger"));
        Assert.assertNotNull("TestTriggerInterpreter not registered",
                PluginRegistry.getTriggerInterpreter("TestTriggerInterpreter"));
    }

}
