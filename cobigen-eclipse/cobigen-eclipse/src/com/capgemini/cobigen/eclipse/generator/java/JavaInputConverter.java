package com.capgemini.cobigen.eclipse.generator.java;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.eclipse.common.tools.ClassLoaderUtil;
import com.capgemini.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.google.common.collect.Lists;

/**
 * Converter to convert the IDE representation of IDE elements to valid input types for the {@link CobiGen
 * generator}
 * @author mbrunnli (04.12.2014)
 */
public class JavaInputConverter {

    /**
     * Converts a list of IDE objects to the supported CobiGen input types
     * @param javaElements
     *            java IDE objects (mainly of type {@link IJavaElement}), which should be converted
     * @return the corresponding {@link List} of inputs for the {@link CobiGen generator}
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws ClassNotFoundException
     *             if the given type could not be found by the project {@link ClassLoader}
     * @throws MalformedURLException
     *             if an eclipse internal exception occurs while retrieving the class loader of the
     *             corresponding java project
     * @author mbrunnli (04.12.2014)
     */
    public static List<Object> convertInput(List<Object> javaElements) throws MalformedURLException,
        CoreException, ClassNotFoundException {
        List<Object> convertedInputs = Lists.newLinkedList();

        /*
         * Precondition / Assumption: all elements of the list are of the same type
         */
        for (Object elem : javaElements) {
            if (elem instanceof IPackageFragment) {
                IPackageFragment frag = (IPackageFragment) elem;
                PackageFolder packageFolder =
                    new PackageFolder(frag.getResource().getLocationURI(), frag.getElementName());
                packageFolder.setClassLoader(ClassLoaderUtil.getProjectClassLoader(frag.getJavaProject()));
                convertedInputs.add(packageFolder);
            } else if (elem instanceof ICompilationUnit) {
                // Take first input type as precondition for the input is that all input types are part of the
                // same project
                IType rootType = ((ICompilationUnit) elem).getTypes()[0];
                ClassLoader projectClassLoader =
                    ClassLoaderUtil.getProjectClassLoader(rootType.getJavaProject());
                Class<?> loadedClass = projectClassLoader.loadClass(rootType.getFullyQualifiedName());
                Object[] inputSourceAndClass =
                    new Object[] {
                        loadedClass,
                        JavaParserUtil.getFirstJavaClass(
                            ClassLoaderUtil.getProjectClassLoader(rootType.getJavaProject()),
                            new StringReader(((ICompilationUnit) elem).getSource())) };
                convertedInputs.add(inputSourceAndClass);
            }
        }

        return convertedInputs;
    }
}
