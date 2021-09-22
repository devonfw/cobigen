package com.devonfw.cobigen.xmlplugin.inputreader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.extension.InputReader;

/** {@link InputReader} for XML files. */
public class XmlInputReader implements InputReader {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(XmlInputReader.class);

  @Override
  public boolean isValidInput(Object input) {

    if (input instanceof Document || input instanceof Path && Files.isRegularFile((Path) input)) {
      return true;
    } else if (input instanceof Node[] && ((Node[]) input).length == 2) {
      Node[] inputArr = (Node[]) input;
      return inputArr[0] instanceof Document;
    } else {
      return false;
    }
  }

  @Override
  public Map<String, Object> createModel(Object input) {

    if (isValidInput(input)) {
      if (input instanceof Document) {
        Map<String, Object> model = new HashMap<>();
        fillModel((Document) input, model, ModelConstant.ROOT_DOC);
        return model;
      } else if (input instanceof Node[]) {
        Map<String, Object> model = new HashMap<>();
        // Document newXmlDocument = createSubDoc(nextNode);

        fillModel((Document) ((Node[]) input)[0], model, ModelConstant.ROOT_DOC);
        fillModel(((Node[]) input)[1], model, ModelConstant.ROOT_ELEMDOC);
        return model;
      } else {
        throw new IllegalArgumentException(
            "XmlInputReader::createModel(Object) called with invalid parameter value. This is a bug.");
      }
    } else {
      return null;
    }
  }

  /**
   * Adds the given document to the given model. First, the string based model is derived and added by the root node's
   * key. Second, the document itself is added under the given XPath root key.
   *
   * @param doc the document to be processed
   * @param model the model to be enriched
   * @param xpathRootKey key of the document in the model to be accessible for xpath expressions
   */
  private void fillModel(Document doc, Map<String, Object> model, String xpathRootKey) {

    Element rootElement = doc.getDocumentElement();
    // custom extracted model for more convenient navigation in the template languages
    model.put(rootElement.getNodeName(), deriveSubModel(rootElement));
    // complete access to allow xpath
    model.put(xpathRootKey, doc);
  }

  /** @see #fillModel(Document, Map, String) */
  @SuppressWarnings("javadoc")
  private void fillModel(Node rootNode, Map<String, Object> model, String xpathRootKey) {

    // custom extracted model for more convenient navigation in the template languages
    model.put(rootNode.getNodeName(), deriveSubModel((Element) rootNode));
    // complete access to allow xpath
    model.put(xpathRootKey, rootNode);
  }

  /**
   * {@inheritDoc}<br>
   * Splits an XMI Document into multiple sub-documents, one per each found class. Returns a {@link List} of
   * DocumentImpl.
   */
  @Override
  public List<Object> getInputObjects(Object input, Charset inputCharset) {

    LOG.debug("Retrieve xml input objects...");
    long start = System.currentTimeMillis();

    if (input instanceof Document) {
      Document doc = (Document) input;
      DocumentTraversal traversal = (DocumentTraversal) doc;
      TreeWalker treeWalker = traversal.createTreeWalker(doc.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null,
          false);
      Node nextNode = treeWalker.nextNode();
      List<Object> docsList = new LinkedList<>();
      while (nextNode != null) {
        docsList.add(new Node[] { doc, nextNode });
        nextNode = treeWalker.nextNode();
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug("{} sub trees extracted in {}s", docsList.size(), (System.currentTimeMillis() - start) / 1000d);
      }
      return docsList;
    }
    throw new IllegalArgumentException(
        "XmlInputReader::getInputObjects(Object,Charset) called with a wrong input parameter. This is a bug!");

  }

  /**
   * {@inheritDoc}<br>
   * Since the {@link XmlInputReader} does not support multiple input objects it always returns an empty {@link List}.
   */
  @Override
  public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {

    return getInputObjects(input, inputCharset);
  }

  /**
   * @param input the element the model should derived from
   * @return derived sub model
   */
  private Map<String, Object> deriveSubModel(Element input) {

    // prepare result object
    Map<String, Object> submodel = new HashMap<>();

    // get all child nodes
    NodeList childList = input.getChildNodes();

    // put element's name into the model
    submodel.put(ModelConstant.NODE_NAME, input.getNodeName());

    // put element's attributes into a list and as single attributes into the model
    NamedNodeMap attributeNodes = input.getAttributes();
    List<Map<String, Object>> attrList = new LinkedList<>();
    for (int i = 0; i < attributeNodes.getLength(); i++) {
      Map<String, Object> att = new HashMap<>();
      Node currentAttrNode = attributeNodes.item(i);
      String attrName = currentAttrNode.getNodeName();
      String attrValue = currentAttrNode.getNodeValue();

      // as list
      att.put(ModelConstant.ATTRIBUTE_NAME, attrName);
      att.put(ModelConstant.ATTRIBUTE_VALUE, attrValue);
      attrList.add(att);

      // as single attributes
      submodel.put(ModelConstant.SINGLE_ATTRIBUTE + attrName, attrValue);
    }
    submodel.put(ModelConstant.ATTRIBUTES, attrList);

    // put text nodes (pcdata) into the model
    List<String> textNodeList = new LinkedList<>();
    String textcontent = "";
    for (int i = 0; i < childList.getLength(); i++) {
      Node currentChild = childList.item(i);
      if (currentChild.getNodeType() == Node.TEXT_NODE) {
        String currentTextContent = currentChild.getTextContent().trim();
        // as String List
        if (!currentTextContent.equals("")) {
          textNodeList.add(currentTextContent);
        }
        // as concatenated String
        textcontent += currentTextContent;
      }
    }
    submodel.put(ModelConstant.TEXT_NODES, textNodeList);
    submodel.put(ModelConstant.TEXT_CONTENT, textcontent);

    // put element child nodes into the model as list (call method recursively)
    List<Map<String, Object>> modelChildList = new LinkedList<>();
    List<String> blackList = new LinkedList<>();
    for (int i = 0; i < childList.getLength(); i++) {
      Node currentChild = childList.item(i);
      if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
        Map<String, Object> currentChildModel = deriveSubModel((Element) currentChild);

        // as list
        modelChildList.add(currentChildModel);

        // as single child, only add if its name is unique, otherwise it just can be provided via
        // CHILDREN list
        String childname = currentChild.getNodeName();
        if (blackList.contains(childname)) {

        } else if (submodel.containsKey(childname)) {
          submodel.remove(childname);
          blackList.add(childname);
        } else {
          submodel.put(childname, currentChildModel);
        }

      }
    }
    submodel.put(ModelConstant.CHILDREN, modelChildList);

    return new HashMap<>(submodel);
  }

  @Override
  public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {

    if (!Files.isDirectory(path)) {
      try (InputStream fileIn = Files.newInputStream(path)) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        // disable validations by default to increase overall performance
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return factory.newDocumentBuilder().parse(fileIn);
      } catch (SAXException | IOException | ParserConfigurationException e) {
        throw new InputReaderException("Could not read file " + path.toString(), e);
      }
    }
    throw new IllegalArgumentException("Currently folders are not supported as Input by XmlInputReader#read");
  }

  @Override
  public boolean isMostLikelyReadable(Path path) {

    List<String> validExtensions = Arrays.asList("xml", "xmi");
    String fileExtension = FilenameUtils.getExtension(path.toString()).toLowerCase();
    return validExtensions.contains(fileExtension);
  }
}
