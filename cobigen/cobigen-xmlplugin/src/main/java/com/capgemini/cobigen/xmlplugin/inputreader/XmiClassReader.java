package com.capgemini.cobigen.xmlplugin.inputreader;

import java.util.List;

/**
 * Extracts class names out of xmi files to generate multiple classes out of one source file.
 */
public class XmiClassReader {
    // TODO get the right input file
    // something like:
    // Path cobigenConfigFolder = new
    // File("src/test/resources/testdata/integrationtest/uml-basic-test").toPath();
    // Path input = cobigenConfigFolder.resolve("uml.xml");

    // parsing the xmi:
    // 1: cobigen uses JAXB: ask Ruben if we can use this parser to get all nodenames (class names)
    // 2: use XPath to get every occurrence of packagedElement like
    // "XMI/Model/packagedElement/packagedElement/@name"
    // some kind of iteration is needed.

    // return all extracted Names as List<Object>

    /**
     *
     * @return a list of objects which represent the name of every class in the given xmi.
     */
    List<Object> getClassNames() {
        return null;
    }

}
