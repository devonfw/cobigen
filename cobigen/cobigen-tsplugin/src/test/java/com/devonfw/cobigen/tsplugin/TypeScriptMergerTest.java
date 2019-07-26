package com.devonfw.cobigen.tsplugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * Test methods for different TS mergers of the plugin
 */
public class TypeScriptMergerTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /** Initializing connection with server */
    private static ExternalProcessHandler request =
        ExternalProcessHandler.getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, 81);

    /**
     * Starts the server and initializes the connection to it
     */
    @BeforeClass
    public static void initializeServer() {
        assertEquals(true, request.executingExe(Constants.EXE_NAME, TypeScriptMergerTest.class));
        assertEquals(true, request.initializeConnection());
    }

    /**
     * Checks if the ts-merger can be launched and if the output is correct with patchOverrides = false
     *
     * @test fails
     */
    @Test
    public void testMergingNoOverrides() {

        try {

            // arrange
            File baseFile = new File(testFileRootPath + "baseFile.ts");

            // Next version should merge comments
            String regex = " * Should format correctly this line";

            // act
            String mergedContents =
                new TypeScriptMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("aProperty: number = 2");
            assertThat(mergedContents).contains("bMethod");
            assertThat(mergedContents).contains("aMethod");
            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("import { c, f } from 'd'");
            assertThat(mergedContents).contains("import { a, e } from 'b'");
            assertThat(mergedContents).contains("export { e, g } from 'f';");
            assertThat(mergedContents).contains("export interface a {");
            assertThat(mergedContents).contains("private b: number;");
            assertThat(mergedContents).containsPattern(regex);

            mergedContents =
                new TypeScriptMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("aProperty: number = 2");
            assertThat(mergedContents).contains("bMethod");
            assertThat(mergedContents).contains("aMethod");
            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("import { c, f } from 'd'");
            assertThat(mergedContents).contains("import { a, e } from 'b'");
            assertThat(mergedContents).contains("export { e, g } from 'f';");
            assertThat(mergedContents).contains("export interface a {");
            assertThat(mergedContents).contains("private b: number;");
            // assertThat(mergedContents).containsPattern(regex);
        } finally {

            request.terminateProcessConnection();
        }
    }

    /**
     * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = true
     *
     * @test fails
     */
    @Test
    public void testMergingOverrides() {

        try {

            // arrange
            File baseFile = new File(testFileRootPath + "baseFile.ts");

            // act
            String mergedContents =
                new TypeScriptMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("aProperty: number = 3");
            assertThat(mergedContents).contains("bMethod");
            assertThat(mergedContents).contains("aMethod");
            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("import { c, f } from 'd'");
            assertThat(mergedContents).contains("import { a, e } from 'b'");
            assertThat(mergedContents).contains("export { e, g } from 'f';");
            assertThat(mergedContents).contains("interface a {");
            assertThat(mergedContents).contains("private b: string;");
            assertThat(mergedContents).contains("// Should contain this comment");

            mergedContents =
                new TypeScriptMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("aProperty: number = 3");
            assertThat(mergedContents).contains("bMethod");
            assertThat(mergedContents).contains("aMethod");
            assertThat(mergedContents).contains("bProperty");
            assertThat(mergedContents).contains("import { c, f } from 'd'");
            assertThat(mergedContents).contains("import { a, e } from 'b'");
            assertThat(mergedContents).contains("export { e, g } from 'f';");
            assertThat(mergedContents).contains("interface a {");
            assertThat(mergedContents).contains("private b: string;");
            assertThat(mergedContents).contains("// Should contain this comment");
        } finally {
            request.terminateProcessConnection();
        }
    }

    /**
     * We need to test whether we are able to send large amount of data to the server.
     *
     * @test fails
     */
    @Test
    public void testMergingMassiveFile() {

        try {

            // arrange
            File baseFile = new File(testFileRootPath + "massiveFile.ts");

            // act
            String mergedContents =
                new TypeScriptMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

            assertEquals(false, mergedContents.contains("Not able to merge") || mergedContents.isEmpty());
        } finally {
            request.terminateProcessConnection();
        }

    }

    /**
     * Tests whether the contents will be rewritten after parsing and printing with the right encoding
     *
     * @throws IOException
     *             test fails
     * @test fails
     */
    @Test
    public void testReadingEncoding() throws IOException {

        File baseFile = new File(testFileRootPath + "baseFile_encoding_UTF-8.ts");
        File patchFile = new File(testFileRootPath + "patchFile.ts");

        try {

            assertEquals(request.executingExe(Constants.EXE_NAME, this.getClass()), true);
            assertEquals(true, request.initializeConnection());

            String mergedContents =
                new TypeScriptMerger("tsmerge", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");

            assertThat(mergedContents.contains("Ñ")).isTrue();

            baseFile = new File(testFileRootPath + "baseFile_encoding_ISO-8859-1.ts");
            mergedContents = "";
            assertThat(mergedContents.contains("Ñ"));
        } finally {
            request.terminateProcessConnection();
        }
    }

    /**
     * Reads a TS file
     *
     * @param fileName
     *            the ts file
     * @return the content of the file
     */
    private String readTSFile(String fileName) {

        File patchFile = new File(testFileRootPath + fileName);
        String file = patchFile.getAbsolutePath();
        Reader reader = null;
        String returnString;

        try {
            reader = new FileReader(file);
            returnString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(patchFile, "Can not read file " + patchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(patchFile, "Can not read the base file " + patchFile.getAbsolutePath());
        }

        return returnString;
    }

}
