package com.capgemini.cobigen.eclipse.generator.xml;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;

/**
 * Generator wrapper covering xml documents as inputs
 * @author mbrunnli (06.12.2014)
 */
public class XmlGeneratorWrapper extends CobiGenWrapper {

    /**
     * Creates a new {@link XmlGeneratorWrapper} to handle xml documents as input
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project does not exist
     * @throws CoreException
     *             if an eclipse internal exception occurred
     * @author mbrunnli (06.12.2014)
     */
    public XmlGeneratorWrapper() throws GeneratorProjectNotExistentException, CoreException {
        super();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.12.2014)
     */
    @Override
    public void adaptModel(Map<String, Object> model) {
        // nothing to do
    }
}
