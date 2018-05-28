package com.devonfw.cobigen.javaplugin.inputreader;

import java.io.Reader;

import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/** The {@link JavaParserUtil} class provides helper functions for generating parsed inputs */
public class JavaParserUtil {

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all upcoming parsed java files
     * will be added to the class library
     *
     * @param reader
     *            {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     */
    public static JavaClass getFirstJavaClass(Reader... reader) {
        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        return getFirstJavaClass(classLibraryBuilder, reader);
    }

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all upcoming parsed java files
     * will be added to the class library. By passing a {@link ClassLoader}, you can take impact on the class
     * name resolving
     *
     * @param classLoader
     *            which should be used for class name resolving
     * @param reader
     *            {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     */
    public static JavaClass getFirstJavaClass(ClassLoader classLoader, Reader... reader) {
        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendClassLoader(classLoader);
        return getFirstJavaClass(classLibraryBuilder, reader);
    }

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all upcoming parsed java files
     * will be added to the class library. Furthermore, a pre-built {@link ClassLibraryBuilder} should be
     * passed, which should be previously enriched by all necessary {@link ClassLoader}s.
     *
     * @param classLibraryBuilder
     *            {@link ClassLibraryBuilder} to build the sources with
     * @param reader
     *            {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     */
    private static JavaClass getFirstJavaClass(ClassLibraryBuilder classLibraryBuilder, Reader... reader) {
        JavaSource source = null;
        ModifyableJavaClass targetClass = null;
        for (Reader r : reader) {
            source = classLibraryBuilder.addSource(r);
            if (targetClass == null) {
                targetClass = (ModifyableJavaClass) source.getClasses().get(0);
            }
        }
        return targetClass;
    }

    /**
     * Converts the String representation of a canonical type into a String which represents the simple type.
     * E.g.:
     * <ul>
     * <li><code>java.lang.String</code> is converted into <code>String</code></li>
     * <li><code>java.util.List&lt;java.lang.String&gt;</code> is converted into
     * <code>List&lt;String&gt;</code></li>
     * </ul>
     *
     * @param canonicalType
     *            the String representation of the canonical type to be resolved
     * @return the resolved simple type as String representation.
     */
    public static String resolveToSimpleType(String canonicalType) {
        String simpleType = new String(canonicalType).replaceAll("(([\\w]+\\.))", "");
        return simpleType;
    }
}
