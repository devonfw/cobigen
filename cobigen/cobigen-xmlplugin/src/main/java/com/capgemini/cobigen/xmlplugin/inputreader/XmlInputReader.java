package com.capgemini.cobigen.xmlplugin.inputreader;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.capgemini.cobigen.extension.IInputReader;

/**
 *
 * @author fkreis (10.11.2014)
 */
public class XmlInputReader implements IInputReader {

    /**
     * {@inheritDoc}
     * @author fkreis (10.11.2014)
     */
    @Override
    public boolean isValidInput(Object input) {
        // TODO should Element or other inputs also be provided?
        if (input instanceof Document) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * {@inheritDoc}
     * @author fkreis (10.11.2014)
     */
    @Override
    public Map<String, Object> createModel(Object input) {
        if (input instanceof Document) {
            Document doc = (Document) input;
            Element rootElement = doc.getDocumentElement();
            Map<String, Object> model = new HashMap<>();
            model.put(rootElement.getNodeName(), deriveSubModel(rootElement));
            return new HashMap<>(model);
        } else {
            return null; // TODO?
        }

    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns
     * <code>false</code>.
     * @author fkreis (10.11.2014)
     */
    @Override
    public boolean combinesMultipleInputObjects(Object input) {
        return false;
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * Since the {@link XmlInputReader} does not support multiple input objects it always returns an empty
     * {@link List}.
     * @author fkreis (10.11.2014)
     */
    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Object> emptyList = new LinkedList<>();
        return emptyList;
    }

    /**
     * {@inheritDoc} <br>
     * <br>
     * Since the {@link XmlInputReader} does not provide any template methods it always returns an empty
     * {@link Map}.
     * @author fkreis (10.11.2014)
     */
    @Override
    public Map<String, Object> getTemplateMethods(Object input) {
        Map<String, Object> emptyMap = new HashMap<>();
        return emptyMap;
    }

    /**
     * @param input
     *            the element the model should derived from
     * @return derived sub model
     * @author fkreis (17.11.2014)
     */
    private Map<String, Object> deriveSubModel(Element input) {
        // prepare result object
        Map<String, Object> submodel = new HashMap<>();

        // put element's attributes into a list and as single attributes into the model
        NamedNodeMap attributeNodes = input.getAttributes();
        List<Map<String, Object>> attrList = new LinkedList<>();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Map<String, Object> att = new HashMap<>();
            Node currentAttrNode = attributeNodes.item(i);
            String attrName = currentAttrNode.getNodeName();
            String attrValue = currentAttrNode.getNodeValue();
            // as list
            att.put(attrName, attrValue);
            attrList.add(att);
            // as single attributes
            submodel.put(ModelConstant.SINGLE_ATTRIBUTE + attrName, attrValue);
        }
        submodel.put(ModelConstant.ATTRIBUTES, attrList);

        return new HashMap<>(submodel);
    }

}
