package com.devonfw.cobigen.xmlplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.devonfw.cobigen.xmlplugin.inputreader.ModelConstant;
import com.devonfw.cobigen.xmlplugin.inputreader.XmlInputReader;

/**
 * Unit tests for {@link XmlInputReader}
 */
public class XmlInputReaderTest {

  /** UTF-8 Charset */
  private static final Charset UTF_8 = Charset.forName("UTF-8");

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/inputreader/";

  /**
   * Tests the model building for container elements.
   *
   * @throws Exception test fails
   */
  @Test
  @SuppressWarnings({ "unchecked", "null" })
  public void testModelCreationForContainerElements() throws Exception {

    XmlInputReader xmlInputReader = new XmlInputReader();
    File xmlFile = new File(testFileRootPath + "simpleXml.xml");
    Object doc = xmlInputReader.read(xmlFile.toPath(), UTF_8);

    List<Object> inputObjects = xmlInputReader.getInputObjects(doc, UTF_8);

    Map<String, Object> aStringModel = null, bStringModel = null, brStringModel = null;
    Node aDoc = null, bDoc = null, brDoc = null;
    for (Object inputObj : inputObjects) {
      Map<String, Object> model = xmlInputReader.createModel(inputObj);
      if (model.containsKey("a")) {
        aStringModel = (Map<String, Object>) model.get("a");
        aDoc = (Node) model.get("elemDoc");
      } else if (model.containsKey("b")) {
        bStringModel = (Map<String, Object>) model.get("b");
        bDoc = (Node) model.get("elemDoc");
      } else if (model.containsKey("br")) {
        brStringModel = (Map<String, Object>) model.get("br");
        brDoc = (Node) model.get("elemDoc");
      } else {
        throw new AssertionError("The result contains an unexpected model.");
      }
    }

    // check string model
    assertThat(aStringModel).isNotNull();
    assertThat(aStringModel.get("_at_attr")).isNotNull().isEqualTo("blubb");
    assertThat(aStringModel.get("br")).isNotNull();

    assertThat(bStringModel).isNotNull();
    assertThat(bStringModel.get("_text_")).isEqualTo("");

    assertThat(brStringModel).isNotNull();
    assertThat(brStringModel.get("_text_")).isEqualTo("abc");

    // check for existence of correct dom doc
    assertThat(aDoc).isNotNull().extracting(e -> e.getNodeName()).containsExactly("a");
    assertThat(bDoc).isNotNull().extracting(e -> e.getNodeName()).containsExactly("b");
    assertThat(brDoc).isNotNull().extracting(e -> e.getNodeName()).containsExactly("br");
  }

  /**
   * Tests the correct retrieval of input objects. Here: generically return all elements of the input document as a new
   * document.
   *
   * @throws Exception test fails
   */
  @Test
  public void testGetInputObjects() throws Exception {

    XmlInputReader xmlInputReader = new XmlInputReader();
    File xmlFile = new File(testFileRootPath + "simpleXml.xml");
    Object doc = xmlInputReader.read(xmlFile.toPath(), UTF_8);

    List<Object> inputObjects = xmlInputReader.getInputObjects(doc, UTF_8);

    // root is not part of the list by intention
    assertThat(inputObjects).extracting(e -> ((Node[]) e)[1].getNodeName()).containsExactlyInAnyOrder("a", "b", "br");
  }

  /**
   * Test method for {@link XmlInputReader#isValidInput(java.lang.Object)} in case of a valid input.
   *
   * @throws ParserConfigurationException test fails
   * @throws IOException test fails
   * @throws SAXException test fails
   */
  @Test
  public void testIsValidInput_isValid() throws ParserConfigurationException, SAXException, IOException {

    XmlInputReader xmlInputReader = new XmlInputReader();
    File xmlFile = new File(testFileRootPath + "/testLibrary.xml");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document validInput = dBuilder.parse(xmlFile);
    assertThat(xmlInputReader.isValidInput(validInput)).isTrue();
  }

  /**
   * Test method for {@link XmlInputReader#isValidInput(java.lang.Object)} in case of an invalid input.
   */
  @Test
  public void testIsValidInput_isNotValid() {

    XmlInputReader xmlInputReader = new XmlInputReader();
    Object invalidInput = new Object();
    assertThat(xmlInputReader.isValidInput(invalidInput)).isFalse();
  }

  /**
   * Test method for {@link XmlInputReader#createModel(java.lang.Object)}.
   *
   * The blacklist filtering should occur (no 'city' key in the model but two 'city' models in 'Children')
   *
   * @throws ParserConfigurationException test fails
   * @throws IOException test fails
   * @throws SAXException test fails
   */
  @Test
  public void testCreateModel() throws ParserConfigurationException, SAXException, IOException {

    // prepare test objects and data
    XmlInputReader xmlInputReader = new XmlInputReader();
    File xmlFile = new File(testFileRootPath + "/testLibrary.xml");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);

    // use test objects and data
    Map<String, Object> model = xmlInputReader.createModel(doc);

    // prepare expected model
    Map<String, Object> expectedSubModel = new HashMap<>();

    expectedSubModel.put(ModelConstant.NODE_NAME, "library");

    // attribute nodes
    List<Map<String, Object>> attributes = new LinkedList<>();
    Map<String, Object> libAttr1Map = new HashMap<>();
    libAttr1Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr1");
    libAttr1Map.put(ModelConstant.ATTRIBUTE_VALUE, "1");
    attributes.add(libAttr1Map);
    Map<String, Object> libAttr2Map = new HashMap<>();
    libAttr2Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr2");
    libAttr2Map.put(ModelConstant.ATTRIBUTE_VALUE, "2");
    attributes.add(libAttr2Map);
    Map<String, Object> libAttr3Map = new HashMap<>();
    libAttr3Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr3");
    libAttr3Map.put(ModelConstant.ATTRIBUTE_VALUE, "3");
    attributes.add(libAttr3Map);
    expectedSubModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr1", "1"); // single attr
    expectedSubModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr2", "2");
    expectedSubModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr3", "3");
    expectedSubModel.put(ModelConstant.ATTRIBUTES, attributes); // attr list

    // text nodes
    String libTextContent = "libTextContent";
    List<String> libraryTextList = new LinkedList<>();
    libraryTextList.add(libTextContent);

    expectedSubModel.put(ModelConstant.TEXT_NODES, libraryTextList);
    expectedSubModel.put(ModelConstant.TEXT_CONTENT, libTextContent);

    // child nodes
    List<Map<String, Object>> children = new LinkedList<>();
    // // city
    Map<String, Object> city = new HashMap<>();
    city.put(ModelConstant.NODE_NAME, "city");
    city.put(ModelConstant.ATTRIBUTES, new LinkedList<>());
    String cityTextContent = "city";
    List<String> cityTextList = new LinkedList<>();
    cityTextList.add(cityTextContent);
    city.put(ModelConstant.TEXT_CONTENT, cityTextContent);
    city.put(ModelConstant.TEXT_NODES, cityTextList);
    city.put(ModelConstant.CHILDREN, new LinkedList<>());
    children.add(city);

    // // cityb
    Map<String, Object> cityb = new HashMap<>();
    cityb.put(ModelConstant.NODE_NAME, "city");
    cityb.put(ModelConstant.ATTRIBUTES, new LinkedList<>());
    String citybTextContent = "cityb";
    List<String> citybTextList = new LinkedList<>();
    citybTextList.add(citybTextContent);
    cityb.put(ModelConstant.TEXT_CONTENT, citybTextContent);
    cityb.put(ModelConstant.TEXT_NODES, citybTextList);
    cityb.put(ModelConstant.CHILDREN, new LinkedList<>());
    children.add(cityb);

    expectedSubModel.put(ModelConstant.CHILDREN, children);

    // expected model

    Map<String, Object> expectedModel = new HashMap<>();
    expectedModel.put("library", expectedSubModel);

    assertThat(model).isNotNull();
    assertThat(model.get("library")).isNotNull().isEqualTo(expectedSubModel);
  }

  /**
   * Test method for {@link XmlInputReader#createModel(java.lang.Object)}.
   *
   * @throws ParserConfigurationException test fails
   * @throws IOException test fails
   * @throws SAXException test fails
   */
  @Test
  public void testCreateModel2() throws ParserConfigurationException, SAXException, IOException {

    // prepare test objects and data
    XmlInputReader xmlInputReader = new XmlInputReader();
    File xmlFile = new File(testFileRootPath + "/testLibrary2.xml");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);

    // use test objects and data
    Map<String, Object> model = xmlInputReader.createModel(doc);

    // prepare expected model
    Map<String, Object> expectedLibraryModel = new HashMap<>();

    expectedLibraryModel.put(ModelConstant.NODE_NAME, "library");

    // attribute nodes
    List<Map<String, Object>> attributes = new LinkedList<>();
    Map<String, Object> libAttr1Map = new HashMap<>();
    libAttr1Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr1");
    libAttr1Map.put(ModelConstant.ATTRIBUTE_VALUE, "1");
    attributes.add(libAttr1Map);
    Map<String, Object> libAttr2Map = new HashMap<>();
    libAttr2Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr2");
    libAttr2Map.put(ModelConstant.ATTRIBUTE_VALUE, "2");
    attributes.add(libAttr2Map);
    Map<String, Object> libAttr3Map = new HashMap<>();
    libAttr3Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr3");
    libAttr3Map.put(ModelConstant.ATTRIBUTE_VALUE, "3");
    attributes.add(libAttr3Map);
    expectedLibraryModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr1", "1"); // single attr
    expectedLibraryModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr2", "2");
    expectedLibraryModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr3", "3");
    expectedLibraryModel.put(ModelConstant.ATTRIBUTES, attributes); // attr list

    // text nodes
    String libTextContent = "libTextContent";
    List<String> libraryTextList = new LinkedList<>();
    libraryTextList.add(libTextContent);

    expectedLibraryModel.put(ModelConstant.TEXT_NODES, libraryTextList);
    expectedLibraryModel.put(ModelConstant.TEXT_CONTENT, libTextContent);

    // child nodes
    List<Map<String, Object>> children = new LinkedList<>();
    // // city
    Map<String, Object> city = new HashMap<>();
    city.put(ModelConstant.NODE_NAME, "city");
    city.put(ModelConstant.ATTRIBUTES, new LinkedList<>());
    String cityTextContent = "city";
    List<String> cityTextList = new LinkedList<>();
    cityTextList.add(cityTextContent);
    city.put(ModelConstant.TEXT_CONTENT, cityTextContent);
    city.put(ModelConstant.TEXT_NODES, cityTextList);
    city.put(ModelConstant.CHILDREN, new LinkedList<>());
    children.add(city);
    expectedLibraryModel.put("city", city);

    expectedLibraryModel.put(ModelConstant.CHILDREN, children);

    assertThat(model).isNotNull();
    assertThat(model.get("library")).isNotNull().isEqualTo(expectedLibraryModel);
  }

  /**
   * Test method for {@link XmlInputReader#createModel(java.lang.Object)}.
   *
   * The blacklist filtering should occur (no 'city' key in the model but three 'city' models in 'Children'). Test
   * checks the full black list branch
   *
   * @throws ParserConfigurationException test fails
   * @throws IOException test fails
   * @throws SAXException test fails
   */
  @Test
  public void testCreateModel3() throws ParserConfigurationException, SAXException, IOException {

    // prepare test objects and data
    XmlInputReader xmlInputReader = new XmlInputReader();
    File xmlFile = new File(testFileRootPath + "/testLibrary3.xml");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);

    // use test objects and data
    Map<String, Object> model = xmlInputReader.createModel(doc);

    // prepare expected model
    Map<String, Object> expectedModel = new HashMap<>();

    expectedModel.put(ModelConstant.NODE_NAME, "library");

    // attribute nodes
    List<Map<String, Object>> attributes = new LinkedList<>();
    Map<String, Object> libAttr1Map = new HashMap<>();
    libAttr1Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr1");
    libAttr1Map.put(ModelConstant.ATTRIBUTE_VALUE, "1");
    attributes.add(libAttr1Map);
    Map<String, Object> libAttr2Map = new HashMap<>();
    libAttr2Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr2");
    libAttr2Map.put(ModelConstant.ATTRIBUTE_VALUE, "2");
    attributes.add(libAttr2Map);
    Map<String, Object> libAttr3Map = new HashMap<>();
    libAttr3Map.put(ModelConstant.ATTRIBUTE_NAME, "libAttr3");
    libAttr3Map.put(ModelConstant.ATTRIBUTE_VALUE, "3");
    attributes.add(libAttr3Map);
    expectedModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr1", "1"); // single attr
    expectedModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr2", "2");
    expectedModel.put(ModelConstant.SINGLE_ATTRIBUTE + "libAttr3", "3");
    expectedModel.put(ModelConstant.ATTRIBUTES, attributes); // attr list

    // text nodes
    String libTextContent = "libTextContent";
    List<String> libraryTextList = new LinkedList<>();
    libraryTextList.add(libTextContent);

    expectedModel.put(ModelConstant.TEXT_NODES, libraryTextList);
    expectedModel.put(ModelConstant.TEXT_CONTENT, libTextContent);

    // child nodes
    List<Map<String, Object>> children = new LinkedList<>();
    // // city
    Map<String, Object> city = new HashMap<>();
    city.put(ModelConstant.NODE_NAME, "city");
    city.put(ModelConstant.ATTRIBUTES, new LinkedList<>());
    String cityTextContent = "city";
    List<String> cityTextList = new LinkedList<>();
    cityTextList.add(cityTextContent);
    city.put(ModelConstant.TEXT_CONTENT, cityTextContent);
    city.put(ModelConstant.TEXT_NODES, cityTextList);
    city.put(ModelConstant.CHILDREN, new LinkedList<>());
    children.add(city);

    // // citya
    Map<String, Object> citya = new HashMap<>();
    citya.put(ModelConstant.NODE_NAME, "city");
    citya.put(ModelConstant.ATTRIBUTES, new LinkedList<>());
    String cityaTextContent = "citya";
    List<String> cityaTextList = new LinkedList<>();
    cityaTextList.add(cityaTextContent);
    citya.put(ModelConstant.TEXT_CONTENT, cityaTextContent);
    citya.put(ModelConstant.TEXT_NODES, cityaTextList);
    citya.put(ModelConstant.CHILDREN, new LinkedList<>());
    children.add(citya);

    // // cityb
    Map<String, Object> cityb = new HashMap<>();
    cityb.put(ModelConstant.NODE_NAME, "city");
    cityb.put(ModelConstant.ATTRIBUTES, new LinkedList<>());
    String citybTextContent = "cityb";
    List<String> citybTextList = new LinkedList<>();
    citybTextList.add(citybTextContent);
    cityb.put(ModelConstant.TEXT_CONTENT, citybTextContent);
    cityb.put(ModelConstant.TEXT_NODES, citybTextList);
    cityb.put(ModelConstant.CHILDREN, new LinkedList<>());
    children.add(cityb);

    expectedModel.put(ModelConstant.CHILDREN, children);

    assertThat(model).isNotNull();
    assertThat(model.get("library")).isNotNull().isEqualTo(expectedModel);
  }

}
