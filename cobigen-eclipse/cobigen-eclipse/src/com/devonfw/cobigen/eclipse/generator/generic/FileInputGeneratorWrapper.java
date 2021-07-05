package com.devonfw.cobigen.eclipse.generator.generic;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.devonfw.cobigen.eclipse.generator.CobiGenWrapper;

/**
 * Generator wrapper covering xml documents as inputs
 */
public class FileInputGeneratorWrapper extends CobiGenWrapper {

    /**
     * Creates a new {@link FileInputGeneratorWrapper} to handle xml documents as input
     * @param cobiGen
     *            initialized {@link CobiGen} instance
     * @param inputs
     *            list of inputs for generation
     * @param inputSourceProject
     *            project from which the inputs have been selected
     * @param monitor
     *            to track progress
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project does not exist
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public FileInputGeneratorWrapper(CobiGen cobiGen, IProject inputSourceProject, List<Object> inputs,
        IProgressMonitor monitor) throws GeneratorProjectNotExistentException, InvalidConfigurationException {
        super(cobiGen, inputSourceProject, inputs, monitor);
    }

    @Override
    public void adaptModel(Map<String, Object> model) {
        // nothing to do
    }

}
