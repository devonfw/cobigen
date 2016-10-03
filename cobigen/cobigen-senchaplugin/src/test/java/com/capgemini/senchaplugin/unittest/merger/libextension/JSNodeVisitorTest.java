package com.capgemini.senchaplugin.unittest.merger.libextension;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import com.capgemini.cobigen.senchaplugin.merger.libextension.JSNodeVisitor;

/**
 *
 * @author rudiazma (Sep 13, 2016)
 */
public class JSNodeVisitorTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Compiler environment for Rhino
     */
    private CompilerEnvirons env;

    /**
     *
     * @author rudiazma (Sep 13, 2016)
     */
    @Before
    public void SetEnv() {
        env = new CompilerEnvirons();
        env.setRecordingLocalJsDocComments(true);
        env.setAllowSharpComments(true);
        env.setRecordingComments(true);

    }

    /**
     *
     * @author rudiazma (Sep 13, 2016)
     * @throws IOException
     *             if file not found
     */
    @Test
    public void nodesTest() throws IOException {
        File jsBaseFile = new File(testFileRootPath + "Base_property.js");

        JSNodeVisitor nodesVisit = new JSNodeVisitor();

        AstRoot nodes = new AstRoot();

        nodes = new Parser(env).parse(FileUtils.readFileToString(jsBaseFile), null, 1);

        nodes.visitAll(nodesVisit);

        assertTrue(nodesVisit.getFunctionCall().size() == 3);
        assertTrue(nodesVisit.getPropertyNodes().size() == 3);
        assertTrue(nodesVisit.getFirstArgument().toSource().equals("'restaurant.controller.table.Table'"));
    }
}
