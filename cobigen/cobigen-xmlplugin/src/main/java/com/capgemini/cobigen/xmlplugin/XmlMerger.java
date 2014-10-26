/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.xmlplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.XmlMerge;
import ch.elca.el4j.services.xmlmerge.mapper.IdentityMapper;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.xmlplugin.action.BasicMergeAction;
import com.capgemini.cobigen.xmlplugin.matcher.XmlMatcher;
import com.capgemini.cobigen.xmlplugin.merge.BasicXmlMerge;

/**
 * The {@link XmlMerger} combines all functionality for merging XML structures
 *
 * @author mbrunnli (12.03.2013)
 */
public class XmlMerger implements IMerger {

    /**
     * Merger type to be registered
     */
    private String type;

    /**
     * {@link XmlMerge} instance
     */
    private XmlMerge xmlMerge;

    /**
     * Assigning logger to XmlMerger
     */
    private Logger LOG = LoggerFactory.getLogger(XmlMerger.class);

    /**
     * Creates a new {@link XmlMerger} with the given {@link BasicMergeAction} to be performed when merging
     * xml elements
     * @param type
     *            to be registered
     * @param action
     *            to be performed when merging two xml elements
     * @author mbrunnli (08.04.2014)
     */
    public XmlMerger(String type, BasicMergeAction action) {
        xmlMerge = new BasicXmlMerge(action, new IdentityMapper(), new XmlMatcher());
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @author trippl (05.03.2013)
     */
    @Override
    public String merge(File base, String patch, String targetCharset) throws IOException, MergeException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder docBuilder;
        String source = "base file"; // just for better error handling
        try {
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setValidating(false);
            docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                false);
            docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
            docBuilder = docBuilderFactory.newDocumentBuilder();

            Document baseDoc =
                docBuilder.parse(new InputSource(new InputStreamReader(new FileInputStream(base),
                    targetCharset)));
            source = "patch"; // base doc parsed correctly, next should be patch

            Document patchDoc = docBuilder.parse(new InputSource(new StringReader(patch)));

            // removeRedundantComments(baseDoc, patchDoc); <-- BasicXmlMerge combined with
            // CompletMergeAction takes care of that

            Document[] toMerge = { baseDoc, patchDoc };
            Document resultXml = xmlMerge.merge(toMerge);

            return prettyPrintDocument(resultXml);
        } catch (ParserConfigurationException e) {
            // ignore - developer fault
            LOG.error("This might be a bug.", e);
        } catch (AbstractXmlMergeException e) {
            LOG.error("An exception occured while merging the file '{}'", base.getAbsolutePath(), e);
            throw new MergeException("An exception occured while merging the file " + base.getAbsolutePath()
                + ":\n" + e.getMessage());
        } catch (TransformerException e) {
            LOG.error("An exception occured while merging the file '{}'", base.getAbsolutePath(), e);
            throw new MergeException("An exception occured while printing the merged file "
                + base.getAbsolutePath() + ":\n" + e.getMessage());
        } catch (SAXException e) {
            LOG.error("An exception occured while parsing the patch.", e);
            if (e.getMessage().contains(
                "The processing instruction target matching \"[xX][mM][lL]\" is not allowed")) {
                throw new MergeException("An exception occured while parsing the " + source + ".\n"
                    + "Please check whether the first line of the " + source + "(" + base.getName() + ") "
                    + " starts with the xml declaration like:\n"
                    + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\nOtherwise you will get this error.");
            }
            throw new MergeException("An exception occured while parsing the patch:\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Prints the given document to the specified destination.
     * @return The merged document contents
     * @param doc
     *            Document to be print
     * @throws TransformerException
     *             if an unrecoverable error occurs during the course of the transformation.
     * @author trippl (05.03.2013)
     */
    private String printDocument(Document doc) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        if (doc.getDoctype() != null) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doc.getDoctype().getPublicId());
        }
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }

    /**
     * Calls {@link #removeEmptyLines(Document)}, {@link #addEmptyLinesBetweenRootChildNodes(Document)} and
     * {@link #printDocument(Document)}. In that order.
     * @param doc
     *            Document to be print
     * @return The merged document contents
     * @throws TransformerException
     *             If an unrecoverable error occurs during the course of the transformation.
     * @author trippl (06.03.2013)
     */
    private String prettyPrintDocument(Document doc) throws TransformerException {
        removeEmptyLines(doc);
        addEmptyLinesBetweenRootChildNodes(doc);
        return printDocument(doc);
    }

    /**
     * Removes comments within the patch document if they also appear in the base document. Note: only removes
     * the first appearance within the patch document.
     * @param base
     *            The base document
     * @param patch
     *            The patch document
     * @author trippl (06.03.2013)
     */
    @SuppressWarnings("unused")
    private void removeRedundantComments(Document base, Document patch) {

        XPathFactory xpathFactory = XPathFactory.newInstance();
        // XPath to find all comment nodes.
        try {
            XPathExpression xpathExp = xpathFactory.newXPath().compile("//comment()");

            NodeList origianlCommentNodes = (NodeList) xpathExp.evaluate(base, XPathConstants.NODESET);
            NodeList patchCommentNodes = (NodeList) xpathExp.evaluate(patch, XPathConstants.NODESET);

            for (int i = 0; i < patchCommentNodes.getLength(); i++) {
                Node commentNode = patchCommentNodes.item(i);
                if (containsComment(origianlCommentNodes, commentNode)) {
                    commentNode.getParentNode().removeChild(commentNode);
                }
            }
        } catch (XPathExpressionException e) {
            // ignore - developer fault
            LOG.error("This might be a bug.", e);
        }
    }

    /**
     * Checks if the given {@link NodeList} contains the specified {@link Node}. This is done by comparing
     * their node values.
     * @param comments
     *            {@link NodeList} of comments to be checked
     * @param comment
     *            {@link Node} comment to be searched
     * @return true if the comment appears in the NodeList, else false.
     * @author trippl (06.03.2013)
     */
    private boolean containsComment(NodeList comments, Node comment) {
        for (int i = 0; i < comments.getLength(); i++) {
            if (comments.item(i).getNodeValue().equals(comment.getNodeValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes empty lines within the given {@link Document}.
     * @param doc
     *            {@link Document} to be checked for empty lines
     * @author trippl (06.03.2013)
     */
    private void removeEmptyLines(Document doc) {

        doc.normalize();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        try {
            // XPath to find empty text nodes.
            XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");

            NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);

            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
        } catch (XPathExpressionException e) {
            // ignore - developer fault
            LOG.error(e.toString());
        }
    }

    /**
     * Adds a empty line before every child node of the {@link Document}s root element node and a empty line
     * after the root node's last child. If the Document's root node is null, nothing is done.
     * @param doc
     *            {@link Document} to be formatted
     * @author trippl (07.03.2013)
     */
    private void addEmptyLinesBetweenRootChildNodes(Document doc) {

        Node root = doc.getDocumentElement();
        if (root == null) {
            return;
        }

        List<Node> nodes = copyNodeList(root.getChildNodes());

        for (Node node : nodes) {
            root.insertBefore(doc.createTextNode("\n\n\t"), node);
        }

        root.appendChild(doc.createTextNode("\n\n"));
    }

    /**
     * Adds the {@link NodeList}'s content into a {@link List}
     * @param nodes
     *            {@link Node}s to be copied
     * @return A {@link List} containing the {@link NodeList}'s {@link Node}s
     * @author trippl (03.04.2013)
     */
    private List<Node> copyNodeList(NodeList nodes) {
        List<Node> copy = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            copy.add(nodes.item(i));
        }
        return copy;
    }
}
