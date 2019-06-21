package com.devonfw.cobigen.cli.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import net.sf.mmm.code.api.CodeName;
import net.sf.mmm.code.base.BaseFile;
import net.sf.mmm.code.base.BasePackage;
import net.sf.mmm.code.base.source.BaseSource;
import net.sf.mmm.code.impl.java.JavaContext;
import net.sf.mmm.code.impl.java.parser.JavaSourceCodeParserImpl;
import net.sf.mmm.util.io.api.IoMode;
import net.sf.mmm.util.io.api.RuntimeIoException;

/**
 * This class contains logic to parse the input file of the user in order to find useful information
 */
public class ParsingUtils {

    /**
     * Creates a new {@link BasePackage}
     * @param source
     *            of the current context {@link BaseSource}
     * @param qName
     *            the qualified name of the parent {@link CodeName}
     * @return A new {@link BasePackage}
     */
    private static BasePackage createPackage(BaseSource source, CodeName qName) {

        BasePackage parentPkg = source.getRootPackage();
        if (qName == null) {
            return parentPkg;
        }
        String simpleName = qName.getSimpleName();
        CodeName parentName = qName.getParent();
        if (parentName == null) {
            if (simpleName.isEmpty()) {
                return parentPkg;
            }
        } else {
            parentPkg = createPackage(source, parentName);
        }
        return new BasePackage(parentPkg, simpleName, null, null, false);
    }

    /**
     * Creates a new {@link BaseFile}
     * @param className
     *            name of the class
     * @param context
     *            current java project context
     * @return A new {@link BaseFile}
     */
    private static BaseFile createFile(String className, JavaContext context) {

        CodeName qName = context.parseName(className);
        BasePackage pkg = createPackage(context.getSource(), qName.getParent());
        return new BaseFile(pkg, qName.getSimpleName());
    }

    /**
     * Gets the full qualified name of the input file
     * @param inputFile
     *            Java file we want to get its qualified name
     * @param context
     *            current java project context
     * @return The full qualified name of the input file
     */
    public static String getQualifiedName(File inputFile, JavaContext context) {

        String className = inputFile.getName().replace(".java", "");
        BaseFile file = createFile(className, context);
        JavaSourceCodeParserImpl parser = new JavaSourceCodeParserImpl();

        try (Reader reader = new FileReader(inputFile.getAbsolutePath())) {
            return parser.parseQualifiedName(reader, file);
        } catch (IOException e) {
            throw new RuntimeIoException(e, IoMode.READ);
        }
    }

}
