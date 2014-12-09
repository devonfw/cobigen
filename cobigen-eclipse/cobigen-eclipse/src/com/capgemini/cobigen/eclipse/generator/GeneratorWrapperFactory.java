package com.capgemini.cobigen.eclipse.generator;

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
import com.capgemini.cobigen.eclipse.common.exceptions.NotYetSupportedException;
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
     * @author mbrunnli (04.12.2014)
     */
    public static CobiGenWrapper createGenerator(IStructuredSelection selection)
        throws GeneratorCreationException, GeneratorProjectNotExistentException {
        List<Object> extractedInputs = extractValidEclipseInputs(selection);

        if (extractedInputs.size() > 0) {
            try {
                Object firstElement = extractedInputs.get(0);
                if (firstElement instanceof IJavaElement) {
                    JavaGeneratorWrapper generator = new JavaGeneratorWrapper();
                    generator.setGenerationTargetProject(((IJavaElement) firstElement).getJavaProject()
                        .getProject());
                    generator.setInputs(JavaInputConverter.convertInput(extractedInputs));
                    return generator;
                } else if (firstElement instanceof IFile) {
                    XmlGeneratorWrapper generator = new XmlGeneratorWrapper();
                    generator.setGenerationTargetProject(((IFile) firstElement).getProject());
                    generator.setInputs(XmlInputConverter.convertInput(extractedInputs));
                    return generator;
                }
            } catch (CoreException e) {
                LOG.error("An eclipse internal exception occurred", e);
                throw new GeneratorCreationException("An eclipse internal exception occurred", e);
            }
        }
        return null;
    }

    /**
     * Extracts a list of valid eclipse inputs. Therefore this method will throw an
     * {@link NotYetSupportedException},whenever<br>
     * <ul>
     * <li>the selection contains different content types</li>
     * <li>the selection contains a content type, which is currently not supported</li>
     * </ul>
     * @param selection
     *            current {@link IStructuredSelection selection} of within the IDE
     * @return the {@link List} of selected objects, whereas all elements of the list are of the same content
     *         type
     * @author mbrunnli (04.12.2014)
     */
    public static List<Object> extractValidEclipseInputs(IStructuredSelection selection) {
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
                    throw new NotYetSupportedException(
                        "Multiple different inputs have been selected of the following types: "
                            + ICompilationUnit.class + ", " + o.getClass());
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
                    throw new NotYetSupportedException(
                        "Multiple different inputs have been selected of the following types: "
                            + IPackageFragment.class + ", " + o.getClass());
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
                    throw new NotYetSupportedException(
                        "Multiple different inputs have been selected of the following types: " + IFile.class
                            + ", " + o.getClass());
                }
                if (initialized) {
                    type = 2;
                    break;
                }
                //$FALL-THROUGH$
            default:
                throw new NotYetSupportedException("An input of the type " + o.getClass().toString()
                    + " has been forwarded an input for generation but it is not yet supported!");
            }
        }

        return inputObjects;
    }
}
