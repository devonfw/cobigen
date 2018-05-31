package com.capgemini.cobigen.senchaplugin.unittest.merger;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.StringLiteral;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.senchaplugin.merger.SenchaMerger;
import com.capgemini.cobigen.senchaplugin.merger.libextension.SenchaNodeVisitor;

@SuppressWarnings("javadoc")
public class SenchaMergerTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Compiler environment for Rhino
     */
    private CompilerEnvirons env;

    @Before
    public void SetEnv() {
        env = new CompilerEnvirons();
        // env.setRecordingLocalJsDocComments(true);
        // env.setAllowSharpComments(true);
        // env.setRecordingComments(true);

    }

    @Test
    public void addObjectPropertyTest_NoOverride() {
        File senchaBaseFile = new File(testFileRootPath + "Base_property.js");
        File senchaPatchFile = new File(testFileRootPath + "Patch_property.js");

        String file = senchaPatchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        }

        String mergedContents = new SenchaMerger("senchamerge", false).merge(senchaBaseFile, patchString, "UTF-8");

        SenchaNodeVisitor nodesResultVisit = new SenchaNodeVisitor();

        AstRoot nodesResult = new AstRoot();

        nodesResult = new Parser(env).parse(mergedContents, null, 1);

        nodesResult.visitAll(nodesResultVisit);

        nodesResultVisit.getPropertyNodes();
        List<String> properties = new LinkedList<>();
        for (ObjectProperty prop : nodesResultVisit.getPropertyNodes()) {
            properties.add(prop.toSource());
        }

        assertTrue(properties.contains("newProperty: 'added'"));
    }

    @Test
    public void addObjectPropertyTest_Override() {
        File senchaBaseFile = new File(testFileRootPath + "Base_property.js");
        File senchaPatchFile = new File(testFileRootPath + "Patch_property.js");

        String file = senchaPatchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        }

        String mergedContents =
            new SenchaMerger("senchamerge_override", true).merge(senchaBaseFile, patchString, "UTF-8");

        SenchaNodeVisitor nodesResultVisit = new SenchaNodeVisitor();

        AstRoot nodesResult = new AstRoot();

        nodesResult = new Parser(env).parse(mergedContents, null, 1);

        nodesResult.visitAll(nodesResultVisit);

        nodesResultVisit.getPropertyNodes();
        List<String> properties = new LinkedList<>();
        for (ObjectProperty prop : nodesResultVisit.getPropertyNodes()) {
            properties.add(prop.toSource());
        }

        assertTrue(properties.contains("newProperty: 'added'"));
    }

    @Test
    public void addArrayElementTest_NoOverride() {
        File senchaBaseFile = new File(testFileRootPath + "Base_ArrayElement.js");
        File senchaPatchFile = new File(testFileRootPath + "Patch_ArrayElement.js");

        String file = senchaPatchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        }

        String mergedContents = new SenchaMerger("senchamerge", false).merge(senchaBaseFile, patchString, "UTF-8");

        SenchaNodeVisitor nodesResultVisit = new SenchaNodeVisitor();

        AstRoot nodesResult = new AstRoot();

        nodesResult = new Parser(env).parse(mergedContents, null, 1);

        nodesResult.visitAll(nodesResultVisit);

        nodesResultVisit.getPropertyNodes();

        boolean added = false;
        for (ObjectProperty prop : nodesResultVisit.getPropertyNodes()) {
            if (prop.getRight() instanceof ArrayLiteral) {
                ArrayLiteral array = (ArrayLiteral) prop.getRight();
                for (AstNode element : array.getElements()) {
                    StringLiteral str = (StringLiteral) element;
                    if (str.toSource().equals("'element4'")) {
                        added = true;
                        break;
                    }
                }
            }
        }

        assertTrue(added);
    }

    @Test
    public void addArrayElementTest_Override() {
        File senchaBaseFile = new File(testFileRootPath + "Base_ArrayElement.js");
        File senchaPatchFile = new File(testFileRootPath + "Patch_ArrayElementOverride.js");

        String file = senchaPatchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(senchaPatchFile,
                "Can not read the base file " + senchaPatchFile.getAbsolutePath());
        }

        String mergedContents =
            new SenchaMerger("senchamerge_override", true).merge(senchaBaseFile, patchString, "UTF-8");

        SenchaNodeVisitor nodesResultVisit = new SenchaNodeVisitor();

        AstRoot nodesResult = new AstRoot();

        nodesResult = new Parser(env).parse(mergedContents, null, 1);

        nodesResult.visitAll(nodesResultVisit);

        nodesResultVisit.getPropertyNodes();

        ArrayLiteral array = new ArrayLiteral();
        for (ObjectProperty prop : nodesResultVisit.getPropertyNodes()) {
            if (prop.getRight() instanceof ArrayLiteral) {
                array = (ArrayLiteral) prop.getRight();
                break;
            }
        }

        assertTrue(array.getElements().size() == 1);
        assertTrue(array.getElement(0).toSource().equals("'element4'"));
    }
}
