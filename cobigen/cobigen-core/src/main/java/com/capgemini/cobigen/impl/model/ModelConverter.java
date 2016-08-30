package com.capgemini.cobigen.impl.model;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts the an FreeMarker object model into a DOM xml model
 *
 * @author mbrunnli (19.02.2013)
 */
public class ModelConverter {

    /**
     * Input object model
     */
    private Map<String, Object> model;

    /**
     * Output {@link Document}
     */
    private Document doc;

    /**
     * Assigning logger to ModelConverter
     */
    private static final Logger LOG = LoggerFactory.getLogger(ModelConverter.class);

    /**
     * Creates a new {@link ModelConverter} for the given object model
     *
     * @param model
     *            (object) model as defined by the FreeMarker documentation
     * @author mbrunnli (19.02.2013)
     */
    public ModelConverter(Map<String, Object> model) {
        this.model = model;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            // should not occur
            LOG.error("Building new Document Exception", e);
        }
    }

    /**
     * Converts the model passed in the constructor to a DOM representation
     *
     * @return the {@link Document} of the DOM representation
     * @author mbrunnli (19.02.2013)
     */
    public Document convertToDOM() {
        Element root = doc.createElement("doc");
        doc.appendChild(root);
        convertToDOMRecursively(root, model, null);
        return doc;
    }

    /**
     * Converts the given object into a DOM xml representation and adds it to the given parent. For reasons of
     * the design of the object model when using {@link List}s, there is the parameter nodeName. The nodeName
     * is a workaround to bridge one level of recursion without losing the meta information of the current
     * node name the list of items is attached to in the model.
     *
     * @param parent
     *            {@link Element} parent object (DOM xml representation)
     * @param object
     *            current model entity
     * @param nodeName
     *            current node name (only used for bridging one level of recursion when converting
     *            {@link List}s, see description)
     * @author mbrunnli (19.02.2013)
     */
    private void convertToDOMRecursively(Element parent, Object object, String nodeName) {
        if (object instanceof Map<?, ?>) {
            for (Object key : ((Map<?, ?>) object).keySet()) {
                if (key instanceof String) {
                    if (((Map<?, ?>) object).get(key) instanceof List<?>) {
                        convertToDOMRecursively(parent, ((Map<?, ?>) object).get(key), (String) key);
                    } else {
                        Element tmp = doc.createElement((String) key);
                        convertToDOMRecursively(tmp, ((Map<?, ?>) object).get(key), null);
                        parent.appendChild(tmp);
                    }
                } // XXX else should not occur --> exception?
            }
        } else if (object instanceof List<?>) {
            for (Object item : (List<?>) object) {
                Element tmp = doc.createElement(nodeName);
                convertToDOMRecursively(tmp, item, null); // TODO List in List Bug !?! -> No more name meta
                                                          // information
                parent.appendChild(tmp);
            }
        } else {
            if (object instanceof String) {
                parent.setTextContent((String) object);
            }
        }
    }
}
