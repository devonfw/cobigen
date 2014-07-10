/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.javaplugin.inputreader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.capgemini.cobigen.util.StringUtil;
import com.google.common.collect.Lists;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * The {@link ParsedJavaModelBuilder} builds a model using QDox as a Java parser
 * @author mbrunnli (03.06.2014)
 */
public class ParsedJavaModelBuilder {

    /**
     * Cached input pojo class in order to avoid unnecessary efforts
     */
    private JavaClass cachedPojo;

    /**
     * Cached model related to the cached input pojo
     */
    private Map<String, Object> cachedModel;

    /**
     * Creates the object model for the template instantiation.
     * 
     * @param javaClass
     *            {@link Class} object of the pojo all information should be retrieved from
     * @return A {@link Map} of a {@link String} key to {@link Object} mapping keys as described before to the
     *         corresponding information. Learn more about the FreeMarker data model at http
     *         ://freemarker.sourceforge.net/docs/dgui_quickstart.html
     * @author mbrunnli (06.02.2013)
     */
    Map<String, Object> createModel(final JavaClass javaClass) {
        if (cachedPojo != null && cachedPojo.equals(javaClass)) {
            return new HashMap<String, Object>(cachedModel);
        }
        cachedPojo = javaClass;

        cachedModel = new HashMap<String, Object>();
        Map<String, Object> pojoModel = new HashMap<String, Object>();
        pojoModel.put("name", javaClass.getName());
        if (javaClass.getPackage() != null) {
            pojoModel.put("package", javaClass.getPackage().getName());
        } else {
            pojoModel.put("package", "");
        }
        pojoModel.put("canonicalName", javaClass.getCanonicalName());

        Map<String, Object> annotations = new HashMap<>();
        extractAnnotationsRecursively(annotations, javaClass.getAnnotations());
        pojoModel.put("annotations", annotations);

        List<Map<String, Object>> attributes = extractAttributes(javaClass);
        pojoModel.put("attributes", attributes);
        determinePojoIds(javaClass, attributes);
        collectAnnotations(javaClass, attributes);

        pojoModel.put("methods", extractMethods(javaClass));
        cachedModel.put("pojo", pojoModel);

        // Utils to enable type checks //TODO not possible any more?
        // cachedModel.put("utils", new BeanModel(new FreeMarkerUtil(javaClass.getClassLoader()),
        // new DefaultObjectWrapper()));

        return new HashMap<String, Object>(cachedModel);
    }

    /**
     * Extracts all methods and the method properties for the model
     * @param javaClass
     *            input java class
     * @return a {@link List} of methods mapping each property to its value
     * @author mbrunnli (04.06.2014)
     */
    private List<Map<String, Object>> extractMethods(JavaClass javaClass) {
        List<Map<String, Object>> methods = new LinkedList<>();
        for (JavaMethod method : javaClass.getMethods()) {
            Map<String, Object> methodAttributes = new HashMap<>();
            methodAttributes.put("name", method.getName());
            if (method.getComment() != null)
                methodAttributes.put("javaDoc", method.getComment());
            Map<String, Object> annotations = new HashMap<>();
            extractAnnotationsRecursively(annotations, method.getAnnotations());
            methodAttributes.put("annotations", annotations);
            methods.add(methodAttributes);
        }
        return methods;
    }

    /**
     * Extracts the attributes from the given POJO
     * @param pojo
     *            {@link Class} object of the POJO the data should be retrieved from
     * @return a {@link Set} of attributes, where each attribute is represented by a {@link Map} of a
     *         {@link String} key to the corresponding {@link String} value of meta information
     * @author mbrunnli (06.02.2013)
     */
    private List<Map<String, Object>> extractAttributes(JavaClass pojo) {
        List<Map<String, Object>> attributes = new LinkedList<Map<String, Object>>();
        for (JavaField f : pojo.getFields()) {
            if (f.isStatic()) {
                continue;
            }
            Map<String, Object> attrValues = new HashMap<String, Object>();
            attrValues.put("name", f.getName());
            attrValues.put("type", f.getType().getName());
            attrValues.put("canonicalType", f.getType().getCanonicalName());
            attributes.add(attrValues);
        }
        return attributes;
    }

    /**
     * Collect all annotations for the given pojo from setter and getter methods by searching using the
     * attribute names. Annotation information retrieved from the setter and getter methods will be added the
     * the corresponding attribute meta data
     * @param javaClass
     *            class for which the setter and getter should be evaluated according to their annotations
     * @param attributes
     *            list of attribute meta data for the generation (object model)
     * @author mbrunnli (01.04.2014)
     */
    private void collectAnnotations(JavaClass javaClass, List<Map<String, Object>> attributes) {
        for (Map<String, Object> attr : attributes) {
            Map<String, Object> annotations = new HashMap<String, Object>();
            attr.put("annotations", annotations);

            JavaMethod getter =
                javaClass.getMethod("get" + StringUtils.capitalize((String) attr.get("name")), null, false);
            if (getter != null)
                extractAnnotationsRecursively(annotations, getter.getAnnotations());

            getter =
                javaClass.getMethod("is" + StringUtils.capitalize((String) attr.get("name")), null, false);
            if (getter != null)
                extractAnnotationsRecursively(annotations, getter.getAnnotations());

            // TODO bugfixing: setter has to have some parameters
            JavaMethod setter =
                javaClass.getMethod("set" + StringUtils.capitalize((String) attr.get("name")), null, false);
            if (setter != null)
                extractAnnotationsRecursively(annotations, setter.getAnnotations());
        }
    }

    /**
     * Extracts all information of the given annotations recursively and writes them into the object model
     * (annotationsMap)
     * @param annotationsMap
     *            object model for annotations
     * @param annotations
     *            to be analysed
     * @author mbrunnli (01.04.2014)
     */
    private void extractAnnotationsRecursively(Map<String, Object> annotationsMap,
        List<JavaAnnotation> annotations) {
        for (JavaAnnotation annotation : annotations) {
            Map<String, Object> annotationParameters = new HashMap<String, Object>();
            annotationsMap.put(annotation.getType().getCanonicalName().replaceAll("\\.", "_"),
                annotationParameters);

            for (String propertyName : annotation.getPropertyMap().keySet()) {
                Object value = annotation.getPropertyMap().get(propertyName).getParameterValue();
                if (value instanceof JavaAnnotation[]) {
                    Map<String, Object> annotationParameterParameters = new HashMap<String, Object>();
                    annotationParameters.put(propertyName, annotationParameterParameters);
                    extractAnnotationsRecursively(annotationParameterParameters,
                        Arrays.asList((JavaAnnotation[]) value));
                } else if (value instanceof Enum<?>[]) {
                    List<String> enumValues = Lists.newLinkedList();
                    for (Enum<?> e : ((Enum<?>[]) value)) {
                        enumValues.add(e.name());
                    }
                    annotationParameters.put(propertyName, enumValues);
                } else if (value instanceof Object[]) {
                    annotationParameters.put(propertyName, Arrays.asList(value));
                } else if (value instanceof Enum<?>) {
                    annotationParameters.put(propertyName, ((Enum<?>) value).name());
                } else {
                    annotationParameters.put(propertyName, value);
                }
            }
        }
    }

    /**
     * Determines whether the given attributes behaving as IDs on the persistence layer. The information will
     * be integrated into the default model as stated in {@link #createModel(JavaClass)}
     * @param javaClass
     *            {@link Class} object of the POJO the data should be retrieved from
     * @param attributes
     *            a {@link List} of all attributes and their properties
     * @author mbrunnli (12.02.2013)
     */
    private void determinePojoIds(JavaClass javaClass, List<Map<String, Object>> attributes) {
        for (Map<String, Object> attr : attributes) {
            JavaMethod getter = null;
            try {
                getter =
                    javaClass.getMethod("get" + StringUtil.capFirst((String) attr.get("name")), null, false);
            } catch (Exception e) {
                getter =
                    javaClass.getMethod("is" + StringUtil.capFirst((String) attr.get("name")), null, false);
            }
            if (getter == null)
                return;

            List<JavaAnnotation> annotations = getter.getAnnotations();
            for (JavaAnnotation a : annotations) {
                if ("javax.persistence.Id".equals(a.getType().getCanonicalName())) {
                    attr.put("isId", "true");
                    break;
                }
            }
            if (attr.get("isId") == null) {
                attr.put("isId", "false");
            }
        }
    }

}
