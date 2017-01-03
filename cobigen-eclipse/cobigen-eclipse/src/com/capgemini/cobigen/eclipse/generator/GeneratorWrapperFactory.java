package com.capgemini.cobigen.eclipse.generator;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.generator.java.JavaInputConverter;
import com.capgemini.cobigen.eclipse.generator.xml.XmlGeneratorWrapper;
import com.capgemini.cobigen.eclipse.generator.xml.XmlInputConverter;
import com.google.common.collect.Lists;

/**
 * Generator creation factory, which creates a specific generator instance dependent on the current selection
 * within the eclipse IDE
 * @author mbrunnli (03.12.2014)
 */
public class GeneratorWrapperFactory {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorWrapperFactory.class);

    /**
     * Creates a generator dependent on the input of the selection
     * @param selection
     *            current {@link IStructuredSelection} treated as input for generation
     * @return a specific {@link CobiGenWrapper} instance
     * @throws GeneratorCreationException
     *             if any exception occurred during converting the inputs or creating the generator
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project does not exist
     * @throws InvalidInputException
     *             if the selection includes non supported input types or is composed in a non supported
     *             combination of inputs.
     * @author mbrunnli (04.12.2014)
     */
    public static CobiGenWrapper createGenerator(IStructuredSelection selection)
        throws GeneratorCreationException, GeneratorProjectNotExistentException, InvalidInputException {

        List<Object> extractedInputs = extractValidEclipseInputs(selection);

        if (extractedInputs.size() > 0) {
            try {
                Object firstElement = extractedInputs.get(0);
                if (firstElement instanceof IJavaElement) {
                    LOG.info("Create new CobiGen instance for java inputs...");
                    JavaGeneratorWrapper generator = new JavaGeneratorWrapper();
                    generator.setGenerationTargetProject(((IJavaElement) firstElement).getJavaProject().getProject());
                    generator.setInputs(JavaInputConverter.convertInput(extractedInputs));
                    return generator;
                } else if (firstElement instanceof IFile) {
                    LOG.info("Create new CobiGen instance for xml inputs...");
                    XmlGeneratorWrapper generator = new XmlGeneratorWrapper();
                    generator.setGenerationTargetProject(((IFile) firstElement).getProject());
                    generator.setInputs(XmlInputConverter.convertInput(extractedInputs));
                    return generator;
                }
            } catch (CoreException e) {
                LOG.error("An eclipse internal exception occurred", e);
                throw new GeneratorCreationException("An eclipse internal exception occurred", e);
            } catch (IOException e) {
                LOG.error("Configuration source could not be read", e);
                throw new GeneratorCreationException("Configuration source could not be read", e);
            }
        }
        return null;
    }

    /**
     * Extracts a list of valid eclipse inputs. Therefore this method will throw an
     * {@link InvalidInputException},whenever<br>
     * <ul>
     * <li>the selection contains different content types</li>
     * <li>the selection contains a content type, which is currently not supported</li>
     * </ul>
     * @param selection
     *            current {@link IStructuredSelection selection} of within the IDE
     * @return the {@link List} of selected objects, whereas all elements of the list are of the same content
     *         type
     * @throws InvalidInputException
     *             if the selection includes non supported input types or is composed in a non supported
     *             combination of inputs.
     * @author mbrunnli (04.12.2014)
     */
    private static List<Object> extractValidEclipseInputs(IStructuredSelection selection) throws InvalidInputException {
        LOG.info("Start extraction of valid inputs from selection...");
        int type = 0;
        boolean initialized = false;
        List<Object> inputObjects = Lists.newLinkedList();

        /*
         * Collect selected objects and check whether all selected objects are of the same type
         */
        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            switch (type) {
            case 0:
                if (o instanceof ICompilationUnit) {
                    inputObjects.add(o);
                    initialized = true;
                } else if (initialized) {
                    throw new InvalidInputException(
                        "Multiple different inputs have been selected of the following types: " + ICompilationUnit.class
                            + ", " + o.getClass());
                }
                if (initialized) {
                    type = 0;
                    break;
                }
                //$FALL-THROUGH$
            case 1:
                if (o instanceof IPackageFragment) {
                    inputObjects.add(o);
                    initialized = true;
                } else if (initialized) {
                    throw new InvalidInputException(
                        "Multiple different inputs have been selected of the following types: " + IPackageFragment.class
                            + ", " + o.getClass());
                }
                if (initialized) {
                    type = 1;
                    break;
                }
                //$FALL-THROUGH$
            case 2:
                if (o instanceof IFile) {
                    inputObjects.add(o);
                    initialized = true;
                } else if (initialized) {
                    throw new InvalidInputException(
                        "Multiple different inputs have been selected of the following types: " + IFile.class + ", "
                            + o.getClass());
                }
                if (initialized) {
                    type = 2;
                    break;
                }
                //$FALL-THROUGH$
            default:
                throw new InvalidInputException("Your selection contains an object of the type "
                    + o.getClass().toString()
                    + ", which is not yet supported to be treated as an input for generation.\n"
                    + "Please adjust your selection to only contain supported objects like Java classes/packages or XML files.");
            }
        }

        LOG.info("Finished extraction of inputs from selection successfully.");
        return inputObjects;
    }
}
