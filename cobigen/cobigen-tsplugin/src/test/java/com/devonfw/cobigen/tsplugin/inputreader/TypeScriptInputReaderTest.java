package com.devonfw.cobigen.tsplugin.inputreader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMergerTest;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 *
 */
public class TypeScriptInputReaderTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/files/";

    /** Initializing connection with server */
    private static ExternalProcessHandler request = ExternalProcessHandler
        .getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

    /**
     * Starts the server and initializes the connection to it
     */
    @BeforeClass
    public static void initializeServer() {
        assertEquals(true, request.executingExe(Constants.EXE_NAME, TypeScriptMergerTest.class));
        assertEquals(true, request.initializeConnection());
    }

    /**
     * Sends the path of a file to be parsed by the external process. Should return a valid JSON model.
     * @param filePath
     *            the path that is going to get sent to the external process.
     * @return The output from the server (in this case will be a JSON model of the input file).
     *
     * @test fails
     */
    public String readingInput(Path filePath) {

        try {

            // act
            String inputModel = (String) new TypeScriptInputReader().read(filePath, Charset.defaultCharset());

            assertThat(inputModel).contains("\"name\":\"aProperty\"");
            assertThat(inputModel).contains("\"name\":\"aMethod\"");
            assertThat(inputModel).contains("\"name\":\"b\"");

            return inputModel;

        } finally {

            request.terminateProcessConnection();
        }
    }

    /**
     * Sends the path of a file to be parsed by the external process. Should return a valid JSON model.
     *
     * @test fails
     */
    @Test
    public void testCreatingModel() {

        try {

            // arrange
            File baseFile = new File(testFileRootPath + "baseFile.ts");

            String inputModel = readingInput(baseFile.getAbsoluteFile().toPath());

            Map<String, Object> mapModel = new TypeScriptInputReader().createModel(inputModel);

            ArrayList<Object> declarations = (ArrayList<Object>) mapModel.get("declarations");

            assertThat(mapModel).isNotNull();
            for (Object declaration : declarations) {
                assertThat(declaration).isEqualTo("a");
            }

        } finally {

            request.terminateProcessConnection();
        }
    }

}
