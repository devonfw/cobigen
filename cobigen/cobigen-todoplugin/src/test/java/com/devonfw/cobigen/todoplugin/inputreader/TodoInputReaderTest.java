package com.devonfw.cobigen.todoplugin.inputreader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input reader test for Todo files
 */
public class TodoInputReaderTest {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TodoInputReader.class);

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/files/";

    /**
     * Sends the path of a file to be parsed by the external process. Should return a valid JSON model.
     * @param filePath
     *            the path that is going to get sent to the external process.
     * @return The output from the server (in this case will be a JSON model of the input file).
     *
     * @test fails
     */
    public String readingInput(Path filePath) {

        // act
        String inputModel = (String) new TodoInputReader().read(filePath, Charset.defaultCharset());
        // Use the following assert as template for your testing
        // assertThat(inputModel).contains("\"identifier\":\"aProperty\"");
        return inputModel;
    }

    /**
     * Sends the path of a file to be parsed by the external process. Should return a valid JSON model. The
     * validity of the generated model is then checked.
     *
     * @test fails
     */
    @Test
    public void testCreatingModel() {

        // arrange
        File baseFile = new File(testFileRootPath + "baseFile.todo");

        String inputModel =
            (String) new TodoInputReader().read(baseFile.getAbsoluteFile().toPath(), Charset.defaultCharset());

        Map<String, Object> mapModel = new TodoInputReader().createModel(inputModel);
        assertThat(mapModel).isNotNull();

        LOG.debug(mapModel.toString());
        mapModel = (Map<String, Object>) mapModel.get("model");

        // Use the following assert as template for your testing
        // ArrayList<Object> classes = castToList(mapModel, "classes");
        // assertEquals(classes.size(), 1);

    }

    /**
     * Cast to list an object
     * @param mapModel
     *            map where our object to cast is stored
     * @param key
     *            cast object with this key
     * @return our object casted to an array list
     * @throws ClassCastException
     *             when casting was not successful
     */
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

        File baseFile = new File(testFileRootPath + "baseFile.todo");
        boolean isValidInput = new TodoInputReader().isValidInput(baseFile);

        LOG.debug("Valid input ? " + isValidInput);
        assertTrue(isValidInput);

    }

    /**
     * Sends a fileEto containing only the path of the file that needs to be parsed. Checks whether the file
     * is most likely readable.
     *
     * @test fails
     */
    @Test
    public void testIsMostProbablyReadable() {

        File baseFile = new File(testFileRootPath + "baseFile.todo");
        boolean isReadable = new TodoInputReader().isMostLikelyReadable(baseFile.toPath());

        LOG.debug("is most probably readable ? " + isReadable);
        assertTrue(isReadable);

    }

    /**
     * Testing whether a file is valid, after it has been read.
     *
     * @test fails
     */
    @Test
    public void testIsValidInputAfterReading() {

        File baseFile = new File(testFileRootPath + "baseFile.todo");
        // parsing
        Object input = new TodoInputReader().read(baseFile.toPath(), Charset.defaultCharset());
        // Now checking whether the input is valid
        boolean isValid = new TodoInputReader().isValidInput(baseFile.toPath());

        LOG.debug("is valid ? " + isValid);
        assertTrue(isValid);

    }

    /**
     * Testing the extraction of the first class or interface.
     * @test fails
     */
    @Test
    public void testGetInputObjects() {

        File baseFile = new File(testFileRootPath + "baseFile.todo");
        List<Object> todoInputObjects = new TodoInputReader().getInputObjects(baseFile, Charset.defaultCharset());
        // Uncomment the following
        // LinkedHashMap<String, Object> inputObject = castToHashMap(todoInputObjects.get(0));

        // Use the following assert as template for your testing
        // assertNotNull(inputObject);
        // assertNotNull(inputObject.get("methods"));

        // LOG.debug(inputObject.toString());

    }

    /**
     * Cast to linked hash map an object
     * @param o
     *            object to cast to linked hash map
     * @throws ClassCastException
     *             when casting was not successful
     * @return linked hash map
     */
    private LinkedHashMap<String, Object> castToHashMap(Object o) {
        return (LinkedHashMap<String, Object>) o;
    }

};