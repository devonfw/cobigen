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
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRoot(Map<String, Object> model) {

        Map<String, Object> pojoMap = (Map<String, Object>) model.get(ModelConstant.ROOT);
        // Assert.assertNotNull(ModelConstant.ROOT + " is not accessible in model", pojoMap);
        return pojoMap;
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
        // Assert.assertNotNull(ModelConstant.FIELDS + " is not accessible in model", attributes);
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

        // Assert.assertNotNull("There is no field with name '" + fieldName + "' in the model", field);
        return field;
    }

}
