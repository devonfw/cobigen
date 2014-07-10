/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.generator.java;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * The {@link JavaModelAdaptor} uses the directly passed reference of the given model and enriches it with
 * additional data
 * @author mbrunnli (13.02.2013)
 */
public class JavaModelAdaptor {

    /**
     * Model reference
     */
    private Map<String, Object> model;

    /**
     * Creates a new {@link JavaModelAdaptor} instance for the given model reference
     * @param model
     *            reference to the model to be enriched
     * @author mbrunnli (13.02.2013)
     */
    public JavaModelAdaptor(Map<String, Object> model) {
        this.model = model;
    }

    /**
     * Adds the javaDoc section to each attribute, where it is defined for
     * @param type
     *            source {@link IType} from which the javaDoc should be retrieved
     * @throws JavaModelException
     * @author mbrunnli (13.02.2013)
     */
    public void addAttributesDescription(IType type) throws JavaModelException {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> attributes =
            (List<Map<String, String>>) ((Map<String, Object>) model.get("pojo")).get("attributes");
        Iterator<Map<String, String>> it = attributes.iterator();
        Map<String, String> currentAttr;
        while (it.hasNext()) {
            currentAttr = it.next();
            IField field = type.getField(currentAttr.get("name"));
            if (field.exists()) {
                String doc = getJavaDoc(field, false);
                String docText = getJavaDoc(field, false);
                if (doc != null) {
                    currentAttr.put("javaDocWithSyntax", doc);
                    currentAttr.put("javaDoc", docText);
                }
            }
        }
    }

    /**
     * Adds the methods of the given type to the model
     * @param type
     *            source {@link IType} from which the javaDoc should be retrieved
     * @throws JavaModelException
     *             if the given type does not exist or if an exception occurs while accessing its
     *             corresponding resource
     * @author mbrunnli (13.02.2013)
     */
    public void addMethods(IType type) throws JavaModelException {
        List<Map<String, String>> methods = new LinkedList<Map<String, String>>();

        for (IMethod method : type.getMethods()) {
            Map<String, String> methodData = new HashMap<String, String>();
            methodData.put("name", method.getElementName());
            String doc = getJavaDoc(method, false);
            String docText = getJavaDoc(method, true);
            if (doc != null) {
                methodData.put("javaDocWithSyntax", doc);
                methodData.put("javaDoc", docText);
            }
            methods.add(methodData);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> pojo = (Map<String, Object>) model.get("pojo");
        pojo.put("methods", methods);
    }

    // public void checkIdAnnotationsviaJavaModel(IType type) {
    // @SuppressWarnings("unchecked")
    // List<Map<String, String>> attributes =
    // (List<Map<String, String>>) ((Map<String, Object>) model.get("pojo")).get("attributes");
    // Iterator<Map<String, String>> it = attributes.iterator();
    // Map<String, String> currentAttr;
    // while (it.hasNext()) {
    // currentAttr = it.next();
    // method
    // IField field = type.getField(currentAttr.get("name"));
    // if (field.exists()) {
    //
    // }
    // }
    // }

    /**
     * Retrieves the javaDoc from a given {@link IMember} of the JavaModel
     * @param member
     *            {@link IMember} the javaDoc should be retrieved from
     * @param onlyText
     *            states whether only the text should be extracted or not
     * @return the javaDoc from a given {@link IMember} of the JavaModel
     * @throws JavaModelException
     *             if the given member does not exist or if an exception occurs while accessing its
     *             corresponding resource.
     * @author mbrunnli (13.02.2013)
     */
    private String getJavaDoc(IMember member, boolean onlyText) throws JavaModelException {
        ISourceRange range = member.getJavadocRange();
        if (range != null) {
            String javaDoc =
                member.getCompilationUnit().getBuffer().getText(range.getOffset(), range.getLength());
            // Replace JavaDoc Syntax: [^\\S\\r\\n] means 'all white spaces besides new lines'
            javaDoc = javaDoc.replaceAll("/\\*|\\s*\\*/|[^\\S\\r\\n]*\\*[^\\S\\r\\n]*", "").trim();
            return javaDoc;
        }
        return null;
    }
}
