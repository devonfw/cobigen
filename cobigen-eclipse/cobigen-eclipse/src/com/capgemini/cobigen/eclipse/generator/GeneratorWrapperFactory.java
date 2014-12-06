package com.capgemini.cobigen.eclipse.generator;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.NotYetSupportedException;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.generator.java.JavaInputConverter;
import com.google.common.collect.Lists;

/**
 * Generator creation factory, which creates a specific generator instance dependent on the current selection
 * within the eclipse IDE
 * @author mbrunnli (03.12.2014)
 */
public class GeneratorWrapperFactory {

    /**
     * Creates a generator dependent on the input of the selection
     * @param selection
     *            current {@link IStructuredSelection} treated as input for generation
     * @return a specific {@link CobiGenWrapper} instance
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @throws MalformedURLException
     *             if an eclipse internal exception occurs while retrieving the class loader of the
     *             corresponding java project
     * @author mbrunnli (04.12.2014)
     */
    public static CobiGenWrapper createGenerator(IStructuredSelection selection)
        throws GeneratorProjectNotExistentException, CoreException, MalformedURLException,
        ClassNotFoundException {
        List<Object> extractedInputs = extractValidEclipseInputs(selection);

        if (extractedInputs.size() > 0) {
            Object firstElement = extractedInputs.get(0);
            if (firstElement instanceof IJavaElement) {
                JavaGeneratorWrapper generator = new JavaGeneratorWrapper();
                if (extractedInputs.size() == 1) {
                    generator.setInput(firstElement);
                } else {
                    generator.setInput(extractedInputs);
                }
                generator.setGenerationTargetProject(((IJavaElement) firstElement).getJavaProject()
                    .getProject());
                generator.setInputs(JavaInputConverter.convertInput(extractedInputs));
                return generator;
            } else if (firstElement instanceof IResource) {
                // TODO XML?
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
