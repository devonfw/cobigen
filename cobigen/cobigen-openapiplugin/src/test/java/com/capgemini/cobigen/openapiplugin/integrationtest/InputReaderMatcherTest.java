package com.capgemini.cobigen.openapiplugin.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.impl.CobiGenFactory;
import com.capgemini.cobigen.openapiplugin.util.TestConstants;

/**
 * Testing the integration of input reader and matcher as the matchers algorithm depends on the model created
 * by the input reader.
 */
public class InputReaderMatcherTest {

    /** Testdata root path */
    private static final String testdataRoot = "src/test/resources/testdata/integrationtest/InputReaderMatcherTest";

    @Test
    public void testBasicElementMatcher() throws Exception {
        CobiGen cobigen = CobiGenFactory.create(Paths.get(testdataRoot, "templates").toUri());

        Object openApiFile =
            cobigen.read("openapi", Paths.get(testdataRoot, "one-component.yaml"), TestConstants.UTF_8);
        assertThat(openApiFile).isNotNull();

        List<Object> inputObjects = cobigen.getInputObjects(openApiFile, TestConstants.UTF_8);
        assertThat(inputObjects).isNotNull().hasSize(1);
    }
}
