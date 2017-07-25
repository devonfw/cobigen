package com.capgemini.cobigen.openapiplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.openapiplugin.inputreader.OpenAPIInputReader;

/** Test suite for {@link OpenAPIInputReader}. */
public class OpenAPIInputReaderTest {

    /** Testdata root path */
    private static final String testdataRoot = "src/main/resources/unittest/OpenAPIInputReaderTest";

    /** UTF-8 Charset */
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Test {@link InputReader#getInputObjects(Object, Charset)} extracting two components
     * @throws Exception
     *             test fails
     */
    @Test
    public void testRetrieveAllInputs() throws Exception {

        OpenAPIInputReader inputReader = new OpenAPIInputReader();

        Object inputObject = inputReader.read(Paths.get(testdataRoot, "two-components.yaml"), UTF_8);
        List<Object> inputObjects = inputReader.getInputObjects(inputObject, UTF_8);

        assertThat(inputObjects).hasSize(2);
        assertThat(inputObjects).extracting("name").containsExactly("tablemanagement", "salesmanagement");
    }
}
