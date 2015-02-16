package com.capgemini.cobigen.unittest.config.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Test;

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
     * @throws IOException
     *             test fails
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException, IOException {

        new ContextConfigurationReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
    }

}
