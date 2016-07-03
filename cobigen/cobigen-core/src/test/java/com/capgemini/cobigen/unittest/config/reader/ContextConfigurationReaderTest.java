package com.capgemini.cobigen.unittest.config.reader;

import java.io.File;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Test;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.reader.ContextConfigurationReader;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;

/**
 * This {@link TestCase} tests the {@link ContextConfigurationReader}
 *
 * @author mbrunnli (18.06.2013)
 */
public class ContextConfigurationReaderTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath =
        "src/test/resources/testdata/unittest/config/reader/ContextConfigurationReaderTest/";

    /**
     * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {
        new ContextConfigurationReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
    }

    /**
     * Tests
     * @throws Exception
     *             test fails
     * @author mbrunnli (16.02.2015)
     */
    @Test
    public void testReadConfigurationFromZip() throws Exception {
        new CobiGen(new File(testFileRootPath + "valid.zip").toURI());
    }

}
