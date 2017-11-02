package com.capgemini.cobigen.xmlplugin.inputreader;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Splits an XMI document into subdocuments, one per class found.
 *
 */

public class XmiSplitter {

    /**
    *
    */
    private static Document document;

    /**
    *
    */
    private static Document newXmlDocument;

    /**
     * This recursive function extracts classes and paths out of a xml file and generates for every class a
     * new xmi file.
     *
     * The first call should use an empty path, an empty docList and the whole document as the NodeList If
     * necessary the packages can be manipulated by providing a pre-package through the path.
     *
     * @param docList
     *            contains every new generated xmi file consisting of only one class and package annotation
     * @param nl
     *            the list of nodes to work with in this recursion.
     * @param path
     *            provides the package for every new recursive call
     * @return a list of objects (new xmi files)
     */
    public List<Object> recursiveExtractor(List<Object> docList, NodeList nl, String path) {

        for (int i = 0; i < nl.getLength(); i++) {
            // not sure if this statement will cause some errors in the future; which items have attributes?
            if (nl.item(i).hasAttributes()) {
                if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Package")) {
                    recursiveExtractor(docList, nl.item(i).getChildNodes(),
                        // path + "." + this is for getting all the parent packages (right now is not needed).
                        nl.item(i).getAttributes().getNamedItem("name").getTextContent());
                } else if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Class")) {
                    docList.add(generateNewClass(nl.item(i), path));
                }
            }
        }
        // System.out.println("-------recursive-anchor--------");
        return docList;
    }

    /**
     * Generates a new xmi file for any given class node and package.
     *
     * @param n
     *            This node needs to represent an class. It will be the source for the new xml file
     * @param pack
     *            The package of the class.
     * @return A document which represents one class.
     */
    private static Object generateNewClass(Node n, String pack) {
        try {
            newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Element pa = newXmlDocument.createElement("package");
        pa.setAttribute("name", pack);
        Element root = newXmlDocument.createElement("xmi:XMI");
        newXmlDocument.appendChild(root);
        Node copyNode = newXmlDocument.importNode(n, false);
        root.appendChild(pa);
        pa.appendChild(copyNode);
        return newXmlDocument;
    }

}
