/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.xmlplugin.matcher;

import org.jdom.Attribute;
import org.jdom.Element;

import ch.elca.el4j.services.xmlmerge.Matcher;

/**
 * Used by an {@link ch.elca.el4j.services.xmlmerge.XmlMerge} instance to evaluate whether two elements match,
 * by calling the Matcher's {@link XmlMatcher#matches} method.
 * @author trippl (12.03.2013)
 * 
 */
public class XmlMatcher implements Matcher {

    /**
     * Evaluates whether the two given elements match, by doing the following:
     * <p>
     * - Compares value of "id" attribute if the elements have one. <br>
     * - Compares value of "resource" attribute if the elements qualified name is "import". <br>
     * - Compares value of "name" attribute if the elements qualified name is "query", "property" or "define".
     * <br>
     * - Compares value of "on" attribute if the elements qualified name is "transition". <br>
     * - Compares value of the elements Text if the elements share the same parent element (
     * {@link #parentElementsMatch(Element, Element)}) <br>
     * - All other elements are compared by their qualified name (not very accurate).
     * @author trippl (12.03.2013)
     */
    @Override
    public boolean matches(Element originalElement, Element patchElement) {
        if (originalElement.getQualifiedName().equals(patchElement.getQualifiedName())) {

            if (originalElement.getAttribute("id") != null && patchElement.getAttribute("id") != null) {
                return originalElement.getAttributeValue("id").equals(patchElement.getAttributeValue("id"));
            }

            if (originalElement.getQualifiedName().equals("import")) {
                if (originalElement.getAttribute("resource") != null
                    && patchElement.getAttribute("resource") != null) { // should not be null
                    return originalElement.getAttributeValue("resource").equals(
                        patchElement.getAttributeValue("resource"));
                }
            }

            if (originalElement.getName().equals("query") || originalElement.getName().equals("property")
                || originalElement.getQualifiedName().equals("define")) {
                if (originalElement.getAttribute("name") != null && patchElement.getAttribute("name") != null) { // should
                                                                                                                 // not
                                                                                                                 // be
                                                                                                                 // null
                    return originalElement.getAttributeValue("name").equals(
                        patchElement.getAttributeValue("name"));
                }
            }

            if (originalElement.getName().equals("transition")) {
                if (originalElement.getAttribute("on") != null && patchElement.getAttribute("on") != null) { // should
                                                                                                             // not
                                                                                                             // be
                                                                                                             // null
                    return originalElement.getAttributeValue("on").equals(
                        patchElement.getAttributeValue("on"));
                }
            }

            if (originalElement.getName().equals("outputLabel")
                || originalElement.getName().equals("message")) {
                if (originalElement.getAttribute("for") != null && patchElement.getAttribute("for") != null) { // should
                                                                                                               // not
                                                                                                               // be
                                                                                                               // null
                    if (originalElement.getAttributeValue("for")
                        .equals(patchElement.getAttributeValue("for"))) {
                        if (originalElement.getAttribute("value") != null
                            && patchElement.getAttribute("value") != null) {
                            return originalElement.getAttributeValue("value").equals(
                                patchElement.getAttributeValue("value"));
                        } else {
                            return originalElement.getAttributeValue("for").equals(
                                patchElement.getAttributeValue("for"));
                        }
                    }
                    return false;
                }
            }

            if (parentElementsMatch(originalElement, patchElement)) {
                return originalElement.getText().equals(patchElement.getText());
            }

            else {
                return true;
            }
        }
        return false;
    }

    /**
     * Compares the values of the {@link Element}s "id" {@link Attribute}, if they have one.
     * @param element1
     * @param element2
     * @return true, if the ids match, false, if they don't match or don't exist
     * @author trippl (27.03.2013)
     */
    private boolean idsMatch(Element element1, Element element2) {
        if (element1.getAttribute("id") != null && element2.getAttribute("id") != null) {
            return element1.getAttributeValue("id").equals(element2.getAttributeValue("id"));
        }
        return false;
    }

    /**
     * Validates, if the parent {@link Element}s of the given {@link Element}s match, by calling
     * {@link #idsMatch(Element, Element)}. If they don't match, this function is recursively called until
     * {@link Element#getParentElement()} returns null.
     * @param originalParent
     *            original parent element
     * @param patchParent
     *            generated patch element
     * @return <code>true</code> if any parents match<br>
     *         <code>false</code> if none of them match or {@link Element#getParentElement()} return
     *         <code>null</code>
     * @author trippl (27.03.2013)
     */
    private boolean parentElementsMatch(Element originalParent, Element patchParent) {
        Element element1Parent = originalParent.getParentElement();
        Element element2Parent = patchParent.getParentElement();
        if (element1Parent == null || element2Parent == null) {
            return false;
        } else {
            if (idsMatch(element1Parent, element2Parent))
                return true;
            return parentElementsMatch(element1Parent, element2Parent);
        }
    }

}
