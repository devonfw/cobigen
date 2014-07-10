/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.xmlplugin.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

import com.capgemini.cobigen.xmlplugin.action.BasicMergeAction;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.Action;
import ch.elca.el4j.services.xmlmerge.Mapper;
import ch.elca.el4j.services.xmlmerge.Matcher;
import ch.elca.el4j.services.xmlmerge.MergeAction;
import ch.elca.el4j.services.xmlmerge.action.AbstractMergeAction;
import ch.elca.el4j.services.xmlmerge.action.OrderedMergeAction;

/**
 * This class provides the functionality of the {@link OrderedMergeAction}
 * class to it's subclasses.
 * <br> In Order to work the subclasses have to override the following functions:
 * <br> {@link #mergeNamespaces(Element, Element, Element)}
 * <br> {@link #mergeAttributes(Element, Element, Element)}
 * <br> {@link #mergeContent(Element, Element, Element)}
 * @author trippl (05.04.2013)
 */
public abstract class BasicMergeAction extends AbstractMergeAction {
	
	/**
	 * {@inheritDoc}
	 */
	public void perform(Element originalElement, Element patchElement,
		Element outputParentElement) throws AbstractXmlMergeException {

		Mapper mapper = (Mapper) m_mapperFactory.getOperation(originalElement,
			patchElement);

		if (originalElement == null) {
			outputParentElement.addContent(mapper.map(patchElement));
		} else if (patchElement == null) {
			outputParentElement.addContent((Content) originalElement.clone());
		} else {

			Element workingElement = new Element(originalElement.getName(),
				originalElement.getNamespacePrefix(), originalElement
					.getNamespaceURI());
			
			mergeNamespaces(workingElement, originalElement, patchElement);
			mergeAttributes(workingElement, originalElement, patchElement);
			mergeContent(workingElement, originalElement, patchElement);
			
			outputParentElement.addContent(workingElement);
		}

	}
	
	/**
	 * Merges the {@link Namespace}s of the originalElement and the patchElement
	 * and adds them to the outElement.
	 * @param outElement
	 * 				the {@link Element} the namespaces are added to
	 * @param originalElement
	 * 				the original {@link Element}
	 * @param patchElement
	 * 				the patch {@link Element}
	 * @author trippl (05.04.2013)
	 */
	protected void mergeNamespaces(Element outElement, Element originalElement, Element patchElement) {
		
	}
	
	/**
	 * Merges the {@link Attribute}s of the originalElement and the patchElement
	 * and adds them to the outElement.
	 * @param outElement
	 * 				the {@link Element} the namespaces are added to
	 * @param originalElement
	 * 				the original {@link Element}
	 * @param patchElement
	 * 				the patch {@link Element}
	 * @author trippl (05.04.2013)
	 */
	protected void mergeAttributes(Element outElement, Element originalElement, Element patchElement) {
		
	}

	/**
	 * Checks if the given {@link List} contains the specified {@link Content}.
	 * @param content
	 * 			{@link Content} to be searched
	 * @param contents
	 * 			{@link List} of {@link Content} to be compared
	 * @return  The {@link Content} in the {@link List}, if found, else null
	 * @throws AbstractXmlMergeException - If an error occurs during the match-operation creation
	 * @author trippl (03.04.2013)
	 */
	protected Content findContent(Content content, List<Content> contents) throws AbstractXmlMergeException {
		for (Content c : contents) {
			if (content.getClass().equals(c.getClass())) {
				if (content instanceof Comment) {
					if (content.getValue().equals(c.getValue()))
						return c;
				} else if (content instanceof Text) {
					return c;
				} else if (content instanceof Element) {
					Element element = (Element) content;
					Element e = (Element) c;
					if (((Matcher) m_matcherFactory.getOperation(element, e)).matches(element, e)) {
						return c;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Merges the {@link Content}s of the originalElement and the patchElement
	 * and adds them to the outElement.
	 * @param outElement
	 * 				the {@link Element} the contents are added to
	 * @param originalElement
	 * 				the original {@link Element}
	 * @param patchElement
	 * 				the patch {@link Element}
	 * @throws AbstractXmlMergeException
	 * 				if an error occurred during the merge
	 * @author trippl (28.03.2013)
	 */	
	protected void mergeContent(Element outElement, Element originalElement, Element patchElement) throws AbstractXmlMergeException {
	
	}

	/**
	 * Applies the action which performs the merge between two source elements.
	 *
	 * @param workingParent
	 *            Output parent element
	 * @param originalElement
	 *            Original element
	 * @param patchElement
	 *            Patch element
	 * @throws AbstractXmlMergeException
	 *             if an error occurred during the merge
	 */
	protected void applyAction(Element workingParent, Element originalElement,
		Element patchElement) throws AbstractXmlMergeException {
		Action action = (Action) m_actionFactory.getOperation(originalElement,
			patchElement);
		Mapper mapper = (Mapper) m_mapperFactory.getOperation(originalElement,
			patchElement);

		if (action instanceof BasicMergeAction) {
			MergeAction mergeAction = (MergeAction) action;
			mergeAction.setActionFactory(m_actionFactory);
			mergeAction.setMapperFactory(m_mapperFactory);
			mergeAction.setMatcherFactory(m_matcherFactory);
		}

		action
			.perform(originalElement, mapper.map(patchElement), workingParent);
	}

	/**
	 * Adds attributes from in element to out element.
	 * @param out out element
	 * @param in in element
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addAttributes(Element out, Element in) {

		LinkedHashMap allAttributes = new LinkedHashMap();

		List outAttributes = new ArrayList(out.getAttributes());
		List inAttributes = new ArrayList(in.getAttributes());

		for (int i = 0; i < inAttributes.size(); i++) {
			Attribute attr = (Attribute) inAttributes.get(i);
			allAttributes.put(attr.getQualifiedName(), attr.clone());
		}
		
		for (int i = 0; i < outAttributes.size(); i++) {
			Attribute attr = (Attribute) outAttributes.get(i);
			allAttributes.put(attr.getQualifiedName(), attr.clone());
		}

		out.setAttributes(new ArrayList(allAttributes.values()));
	}

}
