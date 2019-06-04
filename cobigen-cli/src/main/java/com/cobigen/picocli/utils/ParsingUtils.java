package com.cobigen.picocli.utils;

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

    private static BaseFile createFile(String qualifiedName, JavaContext context) {

        CodeName qName = context.parseName(qualifiedName);
        BasePackage pkg = createPackage(context.getSource(), qName.getParent());
        return new BaseFile(pkg, qName.getSimpleName());
    }

    public static String getQualifiedName(File inputFile, String qualifiedName, JavaContext context) {

        BaseFile file = createFile(qualifiedName, context);
        JavaSourceCodeParserImpl parser = JavaSourceCodeParserImpl.get();

        try (Reader reader = new FileReader(inputFile.getAbsolutePath())) {
            return parser.parseQualifiedName(reader, file);
        } catch (IOException e) {
            throw new RuntimeIoException(e, IoMode.READ);
        }
    }

}
