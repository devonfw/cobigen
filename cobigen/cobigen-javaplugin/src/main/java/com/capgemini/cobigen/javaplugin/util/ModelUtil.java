package com.capgemini.cobigen.javaplugin.util;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;

/**
 * * The {@link ModelUtil} class provides helper functions to access the given model.
 * @author fkreis (25.09.2014)
 */
public class ModelUtil {

    /**
     * Returns the model root-element ({@link ModelConstant#ROOT})
     *
     * @param model
     *            raw model
     * @return the model root-element ({@link ModelConstant#ROOT})
     */
    public static Map<String, Object> getRoot(Map<String, Object> model) {

        Map<String, Object> pojoMap = (Map<String, Object>) model.get(ModelConstant.ROOT);
        return pojoMap;
    }

    /**
     * Returns the model annotations-element ({@link ModelConstant#ANNOTATIONS})
     *
     * @param model
     *            raw model
     * @return the model annotations-element ({@link ModelConstant#ANNOTATIONS})
     */
    public static Map<String, Object> getAnnotations(Map<String, Object> model) {

        Map<String, Object> annotations = (Map<String, Object>) model.get(ModelConstant.ANNOTATIONS);
        return annotations;
    }

    /**
     * Returns the list of all field models ({@link ModelConstant#FIELDS})
     *
     * @param model
     *            raw model
     * @return the list of all field models
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getFields(Map<String, Object> model) {

        List<Map<String, Object>> attributes =
            (List<Map<String, Object>>) getRoot(model).get(ModelConstant.FIELDS);
        return attributes;
    }

    /**
     * Returns the field model with the given field name from model
     *
     * @param model
     *            raw model
     * @param fieldName
     *            field name to be retrieved
     * @return the field model for the given field name.
     */
    public static Map<String, Object> getField(Map<String, Object> model, String fieldName) {

        Map<String, Object> field = null;
        for (Map<String, Object> attr : getFields(model)) {
            if (fieldName.equals(attr.get(ModelConstant.NAME))) {
                field = attr;
                break;
            }
        }
        return field;
    }

    /**
     * Returns the model's supertype element
     *
     * @param model
     *            raw model
     * @return the model's supertype element
     */
    public static Map<String, Object> getExtendedType(Map<String, Object> model) {
        Map<String, Object> supertype = (Map<String, Object>) getRoot(model).get(ModelConstant.EXTENDED_TYPE);
        return supertype;
    }

    /**
     * Returns the model's implemented types element, which is a list consisting of the interface models
     *
     * @param model
     *            raw model
     * @return the model's interfaces element
     */
    public static List<Map<String, Object>> getImplementedTypes(Map<String, Object> model) {
        List<Map<String, Object>> interfaces =
            (List<Map<String, Object>>) getRoot(model).get(ModelConstant.IMPLEMENTED_TYPES);
        return interfaces;
    }

    /**
     * Returns the model's methods element, which is a list consisting of the method models
     *
     * @param model
     *            raw model
     * @return the model's methods element
     */
    public static List<Map<String, Object>> getMethods(Map<String, Object> model) {
        List<Map<String, Object>> methods =
            (List<Map<String, Object>>) getRoot(model).get(ModelConstant.METHODS);
        return methods;
    }

    /**
     * Returns the model's name element, which is the simple name of the input class
     *
     * @param model
     *            raw model
     * @return the model's name element
     */
    public static String getName(Map<String, Object> model) {
        String name = (String) getRoot(model).get(ModelConstant.NAME);
        return name;
    }

    /**
     * Returns the model's canonicalName element, which is the full qualified name of the input class
     *
     * @param model
     *            raw model
     * @return the model's canonicalName element
     */
    public static String getCanonicalName(Map<String, Object> model) {
        String cName = (String) getRoot(model).get(ModelConstant.CANONICAL_NAME);
        return cName;
    }

}
