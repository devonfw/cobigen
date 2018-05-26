package com.devonfw.cobigen.xmlplugin.matcher;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Node;

/** Namespace Resolver backed by an existing DOM Document */
public class NamespaceResolver implements NamespaceContext {

    /** the delegating source document */
    private Node sourceDocument;

    /**
     * This constructor stores the source document to search the namespaces in it.
     * @param document
     *            source document
     */
    public NamespaceResolver(Node document) {
        sourceDocument = document;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return sourceDocument.lookupNamespaceURI(null);
        } else {
            return sourceDocument.lookupNamespaceURI(prefix);
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return sourceDocument.lookupPrefix(namespaceURI);
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }

}
