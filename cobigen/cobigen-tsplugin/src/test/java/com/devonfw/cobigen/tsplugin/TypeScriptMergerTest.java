package com.devonfw.cobigen.tsplugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;

/**
 * Test methods for different TS mergers of the plugin
 */
public class TypeScriptMergerTest {

    /** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = false
     * @throws MergeException
     *             test fails
     */
    @Test
    public void testMergingNoOverrides() throws MergeException {
        // arrange
        File baseFile = new File(testFileRootPath + "baseFile.ts");

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

        mergedContents =
            new TypeScriptMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

        assertThat(mergedContents).contains("bProperty");
        assertThat(mergedContents).contains("aProperty: number = 2");
        assertThat(mergedContents).contains("bMethod");
        assertThat(mergedContents).contains("aMethod");
        assertThat(mergedContents).contains("bProperty");
        assertThat(mergedContents).contains("import { c, f } from 'd'");
        assertThat(mergedContents).contains("import { a, e } from 'b'");
    }

    /**
     * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = true
     * @throws MergeException
     *             test fails
     */
    @Test
    public void testMergingOverrides() throws MergeException {
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

        mergedContents =
            new TypeScriptMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

        assertThat(mergedContents).contains("bProperty");
        assertThat(mergedContents).contains("aProperty: number = 3");
        assertThat(mergedContents).contains("bMethod");
        assertThat(mergedContents).contains("aMethod");
        assertThat(mergedContents).contains("bProperty");
        assertThat(mergedContents).contains("import { c, f } from 'd'");
        assertThat(mergedContents).contains("import { a, e } from 'b'");
    }

    /**
     * Tests whether the contents will be rewritten after parsing and printing with the right encoding
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     */
    @Test
    public void testReadingEncoding() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "baseFile_encoding_UTF-8.ts");
        File patchFile = new File(testFileRootPath + "patchFile.ts");
        String mergedContents =
            new TypeScriptMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");
        assertThat(mergedContents.contains("Ñ")).isTrue();

        baseFile = new File(testFileRootPath + "baseFile_encoding_ISO-8859-1.ts");
        mergedContents =
            new TypeScriptMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "ISO-8859-1");
        assertThat(mergedContents.contains("Ñ"));
    }

    /**
     * Reads a TS file
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
