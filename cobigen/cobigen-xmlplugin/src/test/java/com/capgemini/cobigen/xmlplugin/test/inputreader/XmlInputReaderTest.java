package com.capgemini.cobigen.xmlplugin.test.inputreader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.dom4j.dom.DOMDocument;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.xmlplugin.inputreader.XmlInputReader;

/**
 *
 * @author fkreis (10.11.2014)
 */
public class XmlInputReaderTest {

    /**
     * Test method for {@link XmlInputReader#isValidInput(java.lang.Object)} in case of a valid input.
     */
    @Test
    public void testIsValidInput_isValid() {
        XmlInputReader xmlInputReader = new XmlInputReader();
        DOMDocument validInput = new DOMDocument();
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
     */
    @Ignore("not Not yet implemented")
    @Test
    public void testCreateModel() {
        fail("Not yet implemented");
    }

}
