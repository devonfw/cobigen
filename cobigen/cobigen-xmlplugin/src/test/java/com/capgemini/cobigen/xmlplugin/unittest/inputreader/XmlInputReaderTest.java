package com.capgemini.cobigen.xmlplugin.unittest.inputreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.xmlplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.xmlplugin.inputreader.XmlInputReader;

/**
 *
 * @author fkreis (10.11.2014)
 */
public class XmlInputReaderTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/inputreader/";

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
    assertTrue(xmlInputReader.isValidInput(validInput));
  }

  /**
   * Test method for {@link XmlInputReader#isValidInput(java.lang.Object)} in case of an invalid input.
   */
  @Test
  public void testIsValidInput_isNotValid() {

    XmlInputReader xmlInputReader = new XmlInputReader();
    Object invalidInput = new Object();
    assertFalse(xmlInputReader.isValidInput(invalidInput));
  }

  /**
   * Test method for {@link XmlInputReader#createModel(java.lang.Object)}.
   *
   * The blacklist filtering should occur (no 'city' key in the model but two 'city' models in 'Children')
   *
   * @throws ParserConfigurationException test fails
   * @throws IOException test fails
   * @throws SAXException test fails
   * @author fkreis (10.11.2014)
   * @author (modified) sholzer, 2017-05-12: reduced complexity of the test case and adapted new modul structure.
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

    assertNotNull(model);
    assertEquals(expectedModel, model);
  }

  /**
   * Test method for {@link XmlInputReader#createModel(java.lang.Object)}.
   *
   * @throws ParserConfigurationException test fails
   * @throws IOException test fails
   * @throws SAXException test fails
   *
   * @author sholzer, 2017-05-12: for branch completeness
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
    expectedSubModel.put("city", city);

    expectedSubModel.put(ModelConstant.CHILDREN, children);

    // expected model

    Map<String, Object> expectedModel = new HashMap<>();
    expectedModel.put("library", expectedSubModel);

    assertNotNull(model);
    assertEquals(expectedModel, model);
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
   * @author sholzer, 2017-05-12
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

    expectedSubModel.put(ModelConstant.CHILDREN, children);

    // expected model

    Map<String, Object> expectedModel = new HashMap<>();
    expectedModel.put("library", expectedSubModel);

    assertNotNull(model);
    assertEquals(expectedModel, model);
  }

}
