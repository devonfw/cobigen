/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.xmlplugin.action;

import java.util.List;

import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;

import com.capgemini.cobigen.xmlplugin.action.BasicMergeAction;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.action.OrderedMergeAction;
import ch.elca.el4j.services.xmlmerge.action.OverrideAction;

/**
 * This class combines the functionality of the
 * {@link OrderedMergeAction} class and the {@link OverrideAction} class
 * @author trippl (05.04.2013)
 */
public class OverrideMergeAction extends BasicMergeAction {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void mergeContent(Element outElement, Element originalElement, Element patchElement) throws AbstractXmlMergeException {

		ContentFilter filter = new ContentFilter(ContentFilter.ELEMENT | ContentFilter.COMMENT | ContentFilter.TEXT | ContentFilter.CDATA);
		List<Content> originalContent = (List<Content>) originalElement.getContent(filter);
		List<Content> patchContent = (List<Content>) patchElement.getContent(filter);
		
		for (Content content : patchContent) {
			Content match = findContent(content, originalContent);
			
			if (match != null) {
				// content in both elements
				if (content instanceof Comment || content instanceof Text) {
					outElement.addContent((Content) content.clone());
				} else if (content instanceof Element) {
					Element element = (Element) content;
					Element matchElement = (Element) match;
					applyAction(outElement, matchElement, element);
				}
				originalContent.remove(match);
			} else {
				// content in patchContent only
				if (content instanceof Comment || content instanceof Text) {
					outElement.addContent((Content) content.clone());
				} else if (content instanceof Element) {
					Element element = (Element) content;
					applyAction(outElement, null, element);
				}
			}
		}
		
		for (Content content : originalContent) {
			// content in originalContent only
			if (content instanceof Comment || content instanceof Text) {
				outElement.addContent((Content) content.clone());
			} else if (content instanceof Element) {
				Element element = (Element) content;
				applyAction(outElement, element, null);
			}
		}	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void mergeNamespaces(Element outElement, Element originalElement, Element patchElement) {
		for (Namespace namespace : (List<Namespace>) patchElement.getAdditionalNamespaces()) {
			outElement.addNamespaceDeclaration(namespace);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void mergeAttributes(Element outElement, Element originalElement, Element patchElement) {
		addAttributes(outElement, patchElement);
		addAttributes(outElement, originalElement);	
	}

}
