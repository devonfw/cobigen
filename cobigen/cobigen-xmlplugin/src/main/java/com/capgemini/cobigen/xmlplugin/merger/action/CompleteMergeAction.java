package com.capgemini.cobigen.xmlplugin.merger.action;

import java.util.List;

import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.action.CompleteAction;
import ch.elca.el4j.services.xmlmerge.action.OrderedMergeAction;

/**
 * This class combines the functionality of the {@link OrderedMergeAction} class and the
 * {@link CompleteAction} class
 *
 * @author trippl (05.04.2013)
 */
public class CompleteMergeAction extends BasicMergeAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void mergeContent(Element outElement, Element originalElement, Element patchElement)
        throws AbstractXmlMergeException {

        ContentFilter filter =
            new ContentFilter(ContentFilter.ELEMENT | ContentFilter.COMMENT | ContentFilter.TEXT
                | ContentFilter.CDATA);
        List<Content> originalContent = originalElement.getContent(filter);
        List<Content> patchContent = patchElement.getContent(filter);

        for (Content content : originalContent) {
            Content match = findContent(content, patchContent);

            if (match != null) {
                // content in both elements
                if (content instanceof Comment || content instanceof Text) {
                    outElement.addContent((Content) content.clone());
                } else if (content instanceof Element) {
                    Element element = (Element) content;
                    Element matchElement = (Element) match;
                    applyAction(outElement, element, matchElement);
                }
                patchContent.remove(match);
            } else {
                // content in originalContent only
                if (content instanceof Comment || content instanceof Text) {
                    outElement.addContent((Content) content.clone());
                } else if (content instanceof Element) {
                    Element element = (Element) content;
                    applyAction(outElement, element, null);
                }
            }
        }

        for (Content content : patchContent) {
            // content in patchContent only
            if (content instanceof Comment || content instanceof Text) {
                outElement.addContent((Content) content.clone());
            } else if (content instanceof Element) {
                Element element = (Element) content;
                applyAction(outElement, null, element);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void mergeNamespaces(Element outElement, Element originalElement, Element patchElement) {

        for (Namespace namespace : (List<Namespace>) originalElement.getAdditionalNamespaces()) {
            outElement.addNamespaceDeclaration(namespace);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void mergeAttributes(Element outElement, Element originalElement, Element patchElement) {

        addAttributes(outElement, originalElement);
        addAttributes(outElement, patchElement);
    }

}
