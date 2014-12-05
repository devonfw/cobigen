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
 *
 * @author mbrunnli (03.12.2014)
 */
public class GeneratorWrapperFactory {

    /**
     *
     * @param selection
     * @return
     * @throws GeneratorProjectNotExistentException
     * @throws CoreException
     * @throws ClassNotFoundException
     * @throws MalformedURLException
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
     *
     * @param selection
     * @return
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
