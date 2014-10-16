/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateModelException;

/**
 * A {@link NodeModel} for FreeMarker in order to support XPath in templates
 *
 * @author mbrunnli (19.02.2013)
 */
public class JaxenXPathSupportNodeModel extends NodeModel {

    /**
     * {@link Node} wrapped by this {@link NodeModel}
     */
    private Node node;

    /**
     * Assigning logger to JaxenXPathSupportNodeModel
     */
    private static final Logger LOG = LoggerFactory.getLogger(JaxenXPathSupportNodeModel.class);

    /**
     * Creates a new {@link JaxenXPathSupportNodeModel} for the given node
     *
     * @param node
     *            to be root for this {@link NodeModel}
     * @author mbrunnli (08.02.2013)
     */
    public JaxenXPathSupportNodeModel(Node node) {
        super(node);
        this.node = node;
        try {
            useJaxenXPathSupport();
        } catch (Exception e) {
            LOG.error("{}", "Exception if the Jaxen classes are not present", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (08.02.2013)
     */
    @Override
    public String getNodeName() throws TemplateModelException {
        if (node instanceof Element) {
            return node.getTextContent();
        } else if (node instanceof Attr) {
            return node.getNodeValue();
        } else if (node instanceof Text) {
            return node.getNodeName();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (08.02.2013)
     */
    @Override
    public boolean isEmpty() throws TemplateModelException {
        if (node instanceof Element) {
            return node.getTextContent().trim().isEmpty();
        } else if (node instanceof Attr) {
            return node.getNodeValue().trim().isEmpty();
        } else if (node instanceof Text) {
            return node.getNodeName().trim().isEmpty();
        }
        return false;
    }

}
