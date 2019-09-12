package com.devonfw.cobigen.tsplugin.inputreader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;

/**
 *
 */
public class TypeScriptInputReaderTest {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TypeScriptInputReader.class);

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
        assertEquals(true, request.startServer(TypeScriptInputReaderTest.class));
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
            assertThat(inputModel).contains("\"identifier\":\"aProperty\"");
            assertThat(inputModel).contains("\"identifier\":\"aMethod\"");
            assertThat(inputModel).contains("\"module\":\"b\"");
            return inputModel;

        } finally {
            request.terminateProcessConnection();
        }
    }

    /**
     * Sends the path of a file to be parsed by the external process. Should return a valid JSON model. The
     * validity of the generated model is then checked.
     *
     * @test fails
     */
    @Test
    public void testCreatingModel() {

        try {

            // arrange
            File baseFile = new File(testFileRootPath + "baseFile.ts");

            String inputModel = (String) new TypeScriptInputReader().read(baseFile.getAbsoluteFile().toPath(),
                Charset.defaultCharset());

            Map<String, Object> mapModel = new TypeScriptInputReader().createModel(inputModel);
            assertThat(mapModel).isNotNull();

            LOG.debug(mapModel.toString());
            mapModel = (Map<String, Object>) mapModel.get("model");
            // Checking imports
            ArrayList<Object> importDeclarations = castToList(mapModel, "importDeclarations");
            assertEquals(importDeclarations.size(), 2);

            String[] modules = { "b", "d" };
            String[] importedEntities = { "a", "c" };

            for (int i = 0; i < modules.length; i++) {
                LinkedHashMap<String, Object> currentImport = (LinkedHashMap<String, Object>) importDeclarations.get(i);
                assertEquals(currentImport.get("module"), modules[i]);
                ArrayList<String> currentEntities = (ArrayList<String>) currentImport.get("named");
                assertEquals(currentEntities.get(0), importedEntities[i]);
            }

            // Checking exports
            ArrayList<Object> exportDeclarations = castToList(mapModel, "exportDeclarations");
            assertEquals(exportDeclarations.size(), 1);

            LinkedHashMap<String, Object> currentExport = (LinkedHashMap<String, Object>) exportDeclarations.get(0);
            assertEquals(currentExport.get("module"), "f");
            ArrayList<String> currentEntities = (ArrayList<String>) currentExport.get("named");
            assertEquals(currentEntities.get(0), "e");

            // Checking classes
            ArrayList<Object> classes = castToList(mapModel, "classes");
            assertEquals(classes.size(), 1);

            // Checking interfaces
            ArrayList<Object> interfaces = castToList(mapModel, "interfaces");
            assertEquals(interfaces.size(), 1);

        } finally {

            request.terminateProcessConnection();
        }
    }

    private ArrayList<Object> castToList(Map<String, Object> mapModel, String key) {
        return (ArrayList<Object>) mapModel.get(key);
    }

    /**
     * Sends a fileEto containing only the path of the file that needs to be parsed. Checks whether it is a
     * valid input.
     *
     * @test fails
     */
    @Test
    public void testValidInput() {

        try {
            File baseFile = new File(testFileRootPath + "baseFile.ts");
            boolean isValidInput = new TypeScriptInputReader().isValidInput(baseFile);

            LOG.debug("Valid input ? " + isValidInput);
            assertTrue(isValidInput);

        } finally {
            request.terminateProcessConnection();
        }
    }

    /**
     * Sends a fileEto containing only the path of the file that needs to be parsed. Checks whether the file
     * is most likely readable.
     *
     * @test fails
     */
    @Test
    public void testIsMostProbablyReadable() {

        try {
            File baseFile = new File(testFileRootPath + "baseFile.ts");
            boolean isReadable = new TypeScriptInputReader().isMostLikelyReadable(baseFile.toPath());

            LOG.debug("is most probably readable ? " + isReadable);
            assertTrue(isReadable);

        } finally {
            request.terminateProcessConnection();
        }
    }

    /**
     * Testing whether a file is valid, after it has been read.
     *
     * @test fails
     */
    @Test
    public void testIsValidInputAfterReading() {

        try {
            File baseFile = new File(testFileRootPath + "baseFile.ts");
            // parsing
            Object input = new TypeScriptInputReader().read(baseFile.toPath(), Charset.defaultCharset());
            // Now checking whether the input is valid
            boolean isValid = new TypeScriptInputReader().isValidInput(baseFile.toPath());

            LOG.debug("is valid ? " + isValid);
            assertTrue(isValid);

        } finally {
            request.terminateProcessConnection();
        }
    }

    /**
     * Testing the extraction of the first class or interface.
     * @test fails
     */
    @Test
    public void testGetInputObjects() {

        try {
            File baseFile = new File(testFileRootPath + "baseFile.ts");
            List<Object> tsInputObjects =
                new TypeScriptInputReader().getInputObjects(baseFile, Charset.defaultCharset());
            LinkedHashMap<String, Object> inputObject = castToHashMap(tsInputObjects.get(0));

            assertNotNull(inputObject);
            assertEquals(inputObject.get("identifier"), "a");
            assertNotNull(inputObject.get("methods"));

            LOG.debug(inputObject.toString());

        } finally {
            request.terminateProcessConnection();
        }
    }

    private LinkedHashMap<String, Object> castToHashMap(Object o) {
        return (LinkedHashMap<String, Object>) o;
    }

};