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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.tsplugin.TypeScriptPluginActivator;

/**
 *
 */
public class TypeScriptInputReaderTest {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TypeScriptInputReader.class);

  /** Test resources root path */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/files/";

  /** Activator initializing the external server */
  private TypeScriptPluginActivator activator = new TypeScriptPluginActivator();

  /**
   * Sends the path of a file to be parsed by the external process. Should return a valid JSON model.
   *
   * @param filePath the path that is going to get sent to the external process.
   * @return The output from the server (in this case will be a JSON model of the input file).
   *
   * @test fails
   */
  public String readingInput(Path filePath) {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();

    // act
    String inputModel = (String) tsInputReader.read(filePath, Charset.defaultCharset());
    assertThat(inputModel).contains("\"identifier\":\"aProperty\"");
    assertThat(inputModel).contains("\"identifier\":\"aMethod\"");
    assertThat(inputModel).contains("\"module\":\"b\"");
    return inputModel;
  }

  /**
   * Sends the path of a file to be parsed by the external process. Should return a valid JSON model. The validity of
   * the generated model is then checked.
   *
   * @test fails
   */
  @Test
  public void testCreatingModel() {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    Map<String, Object> mapModel = (Map<String, Object>) tsInputReader.read(baseFile.getAbsoluteFile().toPath(),
        Charset.defaultCharset());
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

  }

  /**
   * Sends a fileEto containing only the path of the file that needs to be parsed. Checks whether it is a valid input.
   *
   * @test fails
   */
  @Test
  public void testValidInput() {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    boolean isValidInput = tsInputReader.isValidInput(baseFile);

    assertTrue(isValidInput);

  }

  /**
   * Sends an Object containing only the path of the file that needs to be parsed. Checks whether it is a valid input.
   *
   * @test fails
   */
  @Test
  public void testValidInputObjectString() {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    Map<String, Object> inputModel = (Map<String, Object>) tsInputReader.read(baseFile.getAbsoluteFile().toPath(),
        Charset.defaultCharset());

    boolean isValidInput = tsInputReader.isValidInput(inputModel);

    assertTrue(isValidInput);

  }

  /**
   * Sends a fileEto containing only the path of the file that needs to be parsed. Checks whether the file is most
   * likely readable.
   *
   * @test fails
   */
  @Test
  public void testIsMostProbablyReadable() {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    boolean isReadable = tsInputReader.isMostLikelyReadable(baseFile.toPath());

    assertTrue(isReadable);

  }

  /**
   * Testing whether a file is valid, after it has been read.
   *
   * @test fails
   */
  @Test
  public void testIsValidInputAfterReading() {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();
    File baseFile = new File(testFileRootPath + "baseFile.ts");
    // parsing
    tsInputReader.read(baseFile.toPath(), Charset.defaultCharset());
    // Now checking whether the input is valid
    boolean isValid = tsInputReader.isValidInput(baseFile.toPath());

    assertTrue(isValid);

  }

  /**
   * Testing the extraction of the first class or interface.
   *
   * @test fails
   */
  @Test
  public void testGetInputObjects() {

    // arrange
    InputReader tsInputReader = this.activator.bindTriggerInterpreter().stream().map(e -> e.getInputReader())
        .findFirst().get();
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    List<Object> tsInputObjects = tsInputReader.getInputObjects(baseFile, Charset.defaultCharset());
    LinkedHashMap<String, Object> inputObject = castToHashMap(tsInputObjects.get(0));

    assertNotNull(inputObject);
    assertEquals(inputObject.get("identifier"), "a");
    assertNotNull(inputObject.get("methods"));

    LOG.debug(inputObject.toString());

  }

  /**
   * Casts an object to a LinkedHashMap
   *
   * @param o object to cast
   * @return object casted to a LinkedHashMap
   */
  private LinkedHashMap<String, Object> castToHashMap(Object o) {

    return (LinkedHashMap<String, Object>) o;
  }

  /**
   * Cast an object on the map to a list
   *
   * @param mapModel current map where our object is located
   * @param key key to find the object
   * @return object casted to list
   */
  private ArrayList<Object> castToList(Map<String, Object> mapModel, String key) {

    return (ArrayList<Object>) mapModel.get(key);
  }

};