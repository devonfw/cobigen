package com.capgemini.cobigen.eclipse.generator.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;

/**
 * The generator interface for the external generator library
 *
 * @author mbrunnli (13.02.2013)
 */
public class JavaGeneratorWrapper extends CobiGenWrapper {

    /**
     * A set of removed fields for the generation.
     */
    private Set<String> ignoreFields = new HashSet<>();

    /**
     * Creates a new generator instance
     *
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @author mbrunnli (21.03.2014)
     */
    public JavaGeneratorWrapper() throws GeneratorProjectNotExistentException, CoreException {
        super();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.12.2014)
     */
    @Override
    public void adaptModel(Map<String, Object> model) {
        removeIgnoredFieldsFromModel(model);
    }

    /**
     * Returns the attributes and its types from the current model
     *
     * @return a {@link Map} mapping attribute name to attribute type name
     * @throws InvalidConfigurationException
     *             if the generator's configuration is faulty
     * @author mbrunnli (12.03.2013)
     */
    public Map<String, String> getAttributesToTypeMapOfFirstInput() throws InvalidConfigurationException {

        Map<String, String> result = new HashMap<>();

        Object firstInput = getCurrentRepresentingInput();
        List<String> matchingTriggerIds = cobiGen.getMatchingTriggerIds(firstInput);
        Map<String, Object> model =
            cobiGen.getModelBuilder(firstInput, matchingTriggerIds.get(0)).createModel();

        List<Map<String, Object>> attributes = JavaModelUtil.getFields(model);
        for (Map<String, Object> attr : attributes) {
            result.put((String) attr.get(ModelConstant.NAME), (String) attr.get(ModelConstant.TYPE));
        }
        return result;
    }

    /**
     * Removes a given attributes from the model
     *
     * @param name
     *            name of the attribute to be removed
     * @author mbrunnli (21.03.2013)
     */
    public void removeFieldFromModel(String name) {

        ignoreFields.add(name);
    }

    /**
     * Removes all fields from the model which have been flagged to be ignored
     *
     * @param model
     *            in which the ignored fields should be removed
     * @author mbrunnli (15.10.2013)
     */
    private void removeIgnoredFieldsFromModel(Map<String, Object> model) {

        List<Map<String, Object>> fields = JavaModelUtil.getFields(model);
        for (Iterator<Map<String, Object>> it = fields.iterator(); it.hasNext();) {
            Map<String, Object> next = it.next();
            for (String ignoredField : ignoreFields) {
                if (next.get(ModelConstant.NAME).equals(ignoredField)) {
                    it.remove();
                    break;
                }
            }
        }
    }
}
