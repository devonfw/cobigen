package com.capgemini.cobigen.eclipse.generator.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.javaplugin.model.JavaModelUtil;
import com.capgemini.cobigen.javaplugin.model.ModelConstant;

/**
 * The generator interface for the external generator library
 *
 */
public class JavaInputGeneratorWrapper extends CobiGenWrapper {

    /** A set of removed fields for the generation. */
    private Set<String> ignoreFields = new HashSet<>();

    /**
     * Creates a new generator instance
     * @param cobiGen
     *            initialized {@link CobiGen} instance
     * @param inputs
     *            list of inputs for generation
     * @param inputSourceProject
     *            project from which the inputs have been selected
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public JavaInputGeneratorWrapper(CobiGen cobiGen, IProject inputSourceProject, List<Object> inputs)
        throws GeneratorProjectNotExistentException, InvalidConfigurationException {
        super(cobiGen, inputSourceProject, inputs);
    }

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
     */
    public Map<String, String> getAttributesToTypeMapOfFirstInput() throws InvalidConfigurationException {

        Map<String, String> result = new HashMap<>();

        Object firstInput = getCurrentRepresentingInput();
        List<String> matchingTriggerIds = cobiGen.getMatchingTriggerIds(firstInput);
        Map<String, Object> model = cobiGen.getModelBuilder(firstInput, matchingTriggerIds.get(0)).createModel();

        List<Map<String, Object>> attributes = JavaModelUtil.getFields(model);
        for (Map<String, Object> attr : attributes) {
            result.put((String) attr.get(ModelConstant.NAME), (String) attr.get(ModelConstant.TYPE));
        }
        return result;
    }

    /**
     * Removes a given attributes from the model
     * @param name
     *            name of the attribute to be removed
     */
    public void removeFieldFromModel(String name) {

        ignoreFields.add(name);
    }

    /**
     * Removes all fields from the model which have been flagged to be ignored
     * @param model
     *            in which the ignored fields should be removed
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
