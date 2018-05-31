package com.capgemini.cobigen.unittest.config.reader;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.impl.config.reader.ContextConfigurationReader;
import com.capgemini.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link ContextConfigurationReader}
 */
public class ContextConfigurationReaderTest extends AbstractUnitTest {

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
     * Tests whether a valid configuration can be read from a zip file.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testReadConfigurationFromZip() throws Exception {
        CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI());
    }

}
