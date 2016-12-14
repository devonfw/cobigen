package com.capgemini.cobigen.senchaplugin.unittest.merger.libextension;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import com.capgemini.cobigen.senchaplugin.merger.libextension.SenchaNodeVisitor;

@SuppressWarnings("javadoc")
public class SenchaNodeVisitorTest {

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
    }

    @Test
    public void nodesTest() throws IOException {
        File jsBaseFile = new File(testFileRootPath + "Base_property.js");

        SenchaNodeVisitor nodesVisit = new SenchaNodeVisitor();

        AstRoot nodes = new AstRoot();

        nodes = new Parser(env).parse(FileUtils.readFileToString(jsBaseFile), null, 1);

        nodes.visitAll(nodesVisit);

        assertTrue(nodesVisit.getFunctionCall().size() == 3);
        assertTrue(nodesVisit.getPropertyNodes().size() == 3);
        assertTrue(nodesVisit.getFirstArgument().toSource().equals("'restaurant.controller.table.Table'"));
    }
}
