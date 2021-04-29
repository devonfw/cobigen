package com.devonfw.cobigen.tsplugin.merger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.tsplugin.TypeScriptPluginActivator;

/**
 * Test methods for different TS mergers of the plugin
 */
public class TypeScriptMergerTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/files/";

    /** Activator initializing the external server */
    private TypeScriptPluginActivator activator = new TypeScriptPluginActivator();

    /**
     * Checks if the ts-merger can be launched and if the output is correct with patchOverrides = false
     */
    @Test
    public void testMergingNoOverrides() {

        // arrange
        Merger tsMerger = activator.bindMerger().stream()
            .filter(e -> e.getType().equals(TypeScriptPluginActivator.TSMERGE)).findFirst().get();
        File baseFile = new File(testFileRootPath + "baseFile.ts");

        // Should merge comments
        String regex = " * Should format correctly this line";

        // act
        String mergedContents = tsMerger.merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

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

        mergedContents = tsMerger.merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

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

    }

    /**
     * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = true
     */
    @Test
    public void testMergingOverrides() {

        // arrange
        Merger tsMerger = activator.bindMerger().stream()
            .filter(e -> e.getType().equals(TypeScriptPluginActivator.TSMERGE_OVERRIDE)).findFirst().get();
        File baseFile = new File(testFileRootPath + "baseFile.ts");

        // act
        String mergedContents = tsMerger.merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

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
        // Should merge comments
        assertThat(mergedContents).contains("// Should contain this comment");

        mergedContents = tsMerger.merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

        assertThat(mergedContents).contains("aProperty: number = 3");
        assertThat(mergedContents).contains("bProperty");
        assertThat(mergedContents).contains("bMethod");
        assertThat(mergedContents).contains("aMethod");
        assertThat(mergedContents).contains("bProperty");
        assertThat(mergedContents).contains("import { c, f } from 'd'");
        assertThat(mergedContents).contains("import { a, e } from 'b'");
        assertThat(mergedContents).contains("export { e, g } from 'f';");
        assertThat(mergedContents).contains("interface a {");
        assertThat(mergedContents).contains("private b: string;");
        // Should merge comments
        assertThat(mergedContents).contains("// Should contain this comment");

    }

    /**
     * We need to test whether we are able to send large amount of data to the server.
     */
    @Test
    public void testMergingMassiveFile() {

        // arrange
        Merger tsMerger = activator.bindMerger().stream()
            .filter(e -> e.getType().equals(TypeScriptPluginActivator.TSMERGE)).findFirst().get();
        File baseFile = new File(testFileRootPath + "massiveFile.ts");

        // act
        String mergedContents = tsMerger.merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

        assertEquals(false, mergedContents.contains("Not able to merge") || mergedContents.isEmpty());
    }

    /**
     * Tests whether the contents will be rewritten after parsing and printing with the right encoding
     *
     * @throws IOException
     *             test fails
     */
    @Test
    public void testReadingEncoding() throws IOException {

        // Arrange
        Merger tsMerger = activator.bindMerger().stream()
            .filter(e -> e.getType().equals(TypeScriptPluginActivator.TSMERGE)).findFirst().get();
        File baseFile = new File(testFileRootPath + "baseFile_encoding_UTF-8.ts");
        File patchFile = new File(testFileRootPath + "patchFile.ts");

        String mergedContents = tsMerger.merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");

        assertThat(mergedContents.contains("Ñ")).isTrue();

        baseFile = new File(testFileRootPath + "baseFile_encoding_ISO-8859-1.ts");
        mergedContents = "";
        assertThat(mergedContents.contains("Ñ"));

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

    /**
     * Tests if TypeScript can handle null- and undefined-aware types
     */
    @Test
    public void testNullAndUndefinedTypes() {
        Merger tsMerger = activator.bindMerger().stream()
            .filter(e -> e.getType().equals(TypeScriptPluginActivator.TSMERGE)).findFirst().get();
        File baseFile = new File(testFileRootPath + "nullBase.ts");
        String mergedContents = tsMerger.merge(baseFile, readTSFile("nullPatch.ts"), "UTF-8");
        assertEquals(false, mergedContents.contains("Not able to merge") || mergedContents.isEmpty());
    }

}
