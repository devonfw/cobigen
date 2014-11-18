package com.capgemini.cobigen.xmlplugin.test.inputreader;

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

import org.junit.Ignore;
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
    private static String testFileRootPath =
        "src/test/resources/com/capgemini/cobigen/xmlplugin/test/inputreader/";

    /**
     * Test method for {@link XmlInputReader#isValidInput(java.lang.Object)} in case of a valid input.
     * @throws ParserConfigurationException
     *             test fails
     * @throws IOException
     *             test fails
     * @throws SAXException
     *             test fails
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
     * @throws ParserConfigurationException
     *             test fails
     * @throws IOException
     *             test fails
     * @throws SAXException
     *             test fails
     */
    @Test
    @Ignore("test model is to complex now, rebuild test with new test structure")
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

        List<Map<String, Object>> attributes = new LinkedList<>();
        Map<String, Object> libAttr1Map = new HashMap<>();
        libAttr1Map.put("libAttr1", "1");
        attributes.add(libAttr1Map);
        Map<String, Object> libAttr2Map = new HashMap<>();
        libAttr2Map.put("libAttr2", "2");
        attributes.add(libAttr2Map);
        Map<String, Object> libAttr3Map = new HashMap<>();
        libAttr3Map.put("libAttr3", "3");
        attributes.add(libAttr3Map);
        expectedSubModel.put(ModelConstant.ATTRIBUTES, attributes); // attr list
        expectedSubModel.put("@libAttr1", "1"); // single attr
        expectedSubModel.put("@libAttr2", "2");
        expectedSubModel.put("@libAttr3", "3");

        Map<String, Object> expectedModel = new HashMap<>();
        expectedModel.put("library", expectedSubModel);

        // validate result
        // System.out.println(model);
        // System.out.println(expectedModel);
        assertNotNull(model);
        assertEquals(expectedModel, model);
    }

}
