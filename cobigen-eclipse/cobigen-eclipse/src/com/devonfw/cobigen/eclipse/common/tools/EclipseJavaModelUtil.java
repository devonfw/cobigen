package com.devonfw.cobigen.eclipse.common.tools;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

/**
 * Util functionality around the eclipse JavaModel
 *
 * @author mbrunnli (08.04.2013)
 */
public class EclipseJavaModelUtil {

    /**
     * Returns the type equivalent to the java file name for the given {@link ICompilationUnit}
     * @param cu
     *            {@link ICompilationUnit} for which the top {@link IType} should be retrieved
     * @return {@link IType} equivalent to the {@link ICompilationUnit} file name
     * @author mbrunnli (08.04.2013)
     */
    public static IType getJavaClassType(ICompilationUnit cu) {
        return cu.getType(cu.getElementName().replace(".java", ""));
    }
}
