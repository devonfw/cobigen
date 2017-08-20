package com.capgemini.cobigen.eclipse.generator.java;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.eclipse.common.tools.EclipseJavaModelUtil;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;

/**
 * The generator interface for the external generator library
 *
 */
public class JavaGeneratorWrapper extends CobiGenWrapper {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(JavaGeneratorWrapper.class);

    /**
     * A set of removed fields for the generation.
     */
    private Set<String> ignoreFields = new HashSet<>();

    /**
     * Creates a new generator instance
     * @param inputs
     *            list of inputs for generation
     * @param inputSourceProject
     *            project from which the inputs have been selected
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws IOException
     *             if the generator project could not be found or read
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     */
    public JavaGeneratorWrapper(IProject inputSourceProject, List<Object> inputs)
        throws GeneratorProjectNotExistentException, CoreException, InvalidConfigurationException, IOException {
        super(inputSourceProject, inputs);
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
     *
     * @param name
     *            name of the attribute to be removed
     */
    public void removeFieldFromModel(String name) {

        ignoreFields.add(name);
    }

    /**
     * Removes all fields from the model which have been flagged to be ignored
     *
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

    @Override
    public boolean isValidInput(IStructuredSelection selection) throws InvalidInputException {
        LOG.debug("Start checking selection validity for the use as Java input.");

        Iterator<?> it = selection.iterator();
        List<String> firstTriggers = null;

        boolean uniqueSourceSelected = false;

        try {
            while (it.hasNext()) {
                Object tmp = it.next();
                if (tmp instanceof ICompilationUnit) {
                    if (firstTriggers == null) {
                        firstTriggers = findMatchingTriggers((ICompilationUnit) tmp);
                    } else {
                        if (!firstTriggers.equals(findMatchingTriggers((ICompilationUnit) tmp))) {
                            throw new InvalidInputException(
                                "You selected at least two inputs, which are not matching the same triggers. "
                                    + "For batch processing all inputs have to match the same triggers.");
                        }
                    }
                } else if (tmp instanceof IPackageFragment) {
                    uniqueSourceSelected = true;
                    firstTriggers = cobiGen.getMatchingTriggerIds(
                        new PackageFolder(((IPackageFragment) tmp).getResource().getLocationURI(),
                            ((IPackageFragment) tmp).getElementName()));
                } else {
                    throw new InvalidInputException(
                        "You selected at least one input, which type is currently not supported as input for generation. "
                            + "Please choose a different one or read the CobiGen documentation for more details.");
                }

                if (uniqueSourceSelected && selection.size() > 1) {
                    throw new InvalidInputException(
                        "You selected at least one input, which type is currently not supported for batch processing.\n "
                            + "Please just select multiple inputs only if batch processing is supported for all inputs.");
                }
            }
        } finally {
            LOG.debug("Ended checking selection validity for the use as Java input.");
        }

        return firstTriggers != null && !firstTriggers.isEmpty();
    }

    /**
     * Returns a {@link Set} of {@link Trigger}s that support the give {@link ICompilationUnit}
     *
     * @param cu
     *            {@link ICompilationUnit} to be checked
     * @return the {@link Set} of {@link Trigger}s
     * @throws InvalidInputException
     *             if the input could not be read as expected
     */
    private List<String> findMatchingTriggers(ICompilationUnit cu) throws InvalidInputException {

        ClassLoader classLoader;
        IType type = null;
        try {
            classLoader = ClassLoaderUtil.getProjectClassLoader(cu.getJavaProject());
            type = EclipseJavaModelUtil.getJavaClassType(cu);
            return cobiGen.getMatchingTriggerIds(classLoader.loadClass(type.getFullyQualifiedName()));
        } catch (MalformedURLException e) {
            throw new InvalidInputException(
                "Error while retrieving the project's ('" + cu.getJavaProject().getElementName() + "') classloader.",
                e);
        } catch (CoreException e) {
            throw new InvalidInputException("An eclipse internal exception occured!", e);
        } catch (ClassNotFoundException e) {
            throw new InvalidInputException("The class '" + type.getFullyQualifiedName() + "' could not be found. "
                + "This may be cause of a non-compiling host project of the selected input.", e);
        } catch (UnsupportedClassVersionError e) {
            throw new InvalidInputException("Incompatible java version: "
                + "You have selected a java class, which Java version is higher than the Java runtime your eclipse is running with. "
                + "Please update your PATH variable to reference the latest Java runtime you are developing for and restart eclipse.\n"
                + "Current runtime: " + System.getProperty("java.version"), e);
        }
    }
}
