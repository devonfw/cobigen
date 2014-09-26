package com.capgemini.cobigen.javaplugin.util;

import java.io.Reader;

import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * The {@link JavaParserUtil} class provides helper functions for generating parsed inputs
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class JavaParserUtil {
    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all upcoming parsed java files
     * will be added to the class library
     *
     * @param reader
     *            {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     * @author mbrunnli (19.03.2013)
     */
    public static JavaClass getFirstJavaClass(Reader... reader) {

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        JavaSource source = null;
        ModifyableJavaClass targetClass = null;
        for (Reader r : reader) {
            source = classLibraryBuilder.addSource(r);
            if (targetClass == null)
                targetClass = (ModifyableJavaClass) source.getClasses().get(0);
        }
        return targetClass;
    }

    /**
     * Returns the {@link JavaClass} parsed by the given {@link Reader}
     * @param reader
     *            {@link Reader} which contents should be parsed
     * @return the parsed {@link JavaClass}
     * @author mbrunnli (19.03.2013)
     */
    public static ModifyableJavaClass getJavaClass(Reader reader) {
        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        classLibraryBuilder.addSource(reader);
        JavaSource source = null;
        for (JavaSource s : classLibraryBuilder.getClassLibrary().getJavaSources()) {
            source = s;
            // only consider one class per file
            break;
        }
        // save cast as given by the customized builder
        return (ModifyableJavaClass) source.getClasses().get(0);
    }
}
