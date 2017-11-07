package com.capgemini.cobigen.xmlplugin.inputreader;

import java.util.LinkedList;
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

        // List of nodes for storing the parent packages of the class
        List<Node> packagesList = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            // not sure if this statement will cause some errors in the future; which items have attributes?
            if (nl.item(i).hasAttributes()) {
                if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Package")) {
                    recursiveExtractor(docList, nl.item(i).getChildNodes(),
                        // path + "." + this is for getting all the parent packages (right now is not needed).
                        nl.item(i).getAttributes().getNamedItem("name").getTextContent());
                    packagesList.add(nl.item(i));
                } else if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Class")) {
                    docList.add(generateNewClass(nl.item(i), path, packagesList));
                    packagesList = new LinkedList<>();
                }
            }
        }
        // System.out.println("-------recursive-anchor--------");
        return docList;
    }

    /**
     * Generates a new xmi file for any given class node.
     *
     * @param n
     *            This node needs to represent a class. It will be the source for the new xml file
     * @param pack
     *            The package of the class. (Currently not needed)
     * @param packagesList
     *            The list of nodes which n is child of
     * @return A document which represents one class.
     */
    private static Object generateNewClass(Node n, String pack, List<Node> packagesList) {
        try {
            newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Node rootNode = document.getFirstChild();
        Element rootTag = (Element) newXmlDocument.importNode(rootNode, false);

        Node copyNode = newXmlDocument.importNode(n, true);
        Node parentNode = n.getParentNode();

        newXmlDocument.appendChild(rootTag);

        List<Node> parentList = new LinkedList<>();
        if (parentNode == null) {
            rootTag.appendChild(copyNode);
        } else {
            parentList = extractParentNodes(parentNode);
        }

        if (parentList.size() > 0) {
            appendingToParent(copyNode, parentList);
            // We append the last one because it contains all the child elements
            Node childNode = newXmlDocument.importNode(parentList.get(parentList.size() - 1), true);
            rootTag.appendChild(newXmlDocument.importNode(childNode, true));
        }

        return newXmlDocument;
    }

    /**
     * Appends the children of each parent in the correct order
     * @param copyNode
     *            class node to append at the end
     * @param parentList
     *            list of parents
     */
    private static void appendingToParent(Node copyNode, List<Node> parentList) {
        // Parent tags are stored in reverse order, we get the last one
        for (int i = parentList.size() - 2; i >= 0; i--) {
            parentList.get(i + 1).appendChild(parentList.get(i));
            if (i == 0) {
                // For the last parent tag, we append the class
                parentList.get(0).appendChild(copyNode);
            }
        }
    }

    /**
     * Extracts all the parent nodes from the given node
     * @param parentNode
     *            The node from which we are going to take the parents from
     * @return List<Node>
     */
    private static List<Node> extractParentNodes(Node parentNode) {
        List<Node> parentList = new LinkedList<>();
        // While we don't reach the root tag, we get the parent nodes of the class
        while (!parentNode.getNodeName().equals("xmi:XMI")) {
            parentList.add(newXmlDocument.importNode(parentNode, false));
            parentNode = parentNode.getParentNode();
        }
        return parentList;
    }

    /**
     * Set the document that is going to be processed
     * @param doc
     */
    public void setDocument(Document doc) {
        XmiSplitter.document = doc;
    }

}
