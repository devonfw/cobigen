package com.capgemini.cobigen.xmlplugin.inputreader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;

/** {@link InputReader} for XML files. */
public class XmlInputReader implements InputReader {

    @Override
    public boolean isValidInput(Object input) {
        if (input instanceof Document || input instanceof Path && Files.isRegularFile((Path) input)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> createModel(Object input) {
        if (isValidInput(input)) {
            Document doc = (Document) input;
            Element rootElement = doc.getDocumentElement();
            Map<String, Object> model = new HashMap<>();
            model.put(rootElement.getNodeName(), deriveSubModel(rootElement));
            return new HashMap<>(model);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}.<br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns
     * <code>false</code>.
     */
    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        return false;
    }

    /**
     * {@inheritDoc}<br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns an empty
     * {@link List}.
     */
    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        return new LinkedList<>();
    }

    /**
     * {@inheritDoc}<br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns an empty
     * {@link List}.
     */
    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        return new LinkedList<>();
    }

    /**
     * {@inheritDoc} <br>
     * Since the {@link XmlInputReader} does not provide any template methods it always returns an empty
     * {@link Map}.
     */
    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        return new HashMap<>();
    }

    /**
     * @param input
     *            the element the model should derived from
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
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileIn);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                throw new InputReaderException("Could not read file " + path.toString(), e);
            }
        }
        throw new IllegalArgumentException("Currently folders are not supported as Input by XmlInputReader#read");
    }
}
