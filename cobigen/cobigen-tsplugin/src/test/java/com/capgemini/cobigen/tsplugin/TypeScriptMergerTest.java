package com.capgemini.cobigen.tsplugin;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.tsplugin.merger.TypeScriptMerger;

/**
 * Test methods for different TS mergers of the plugin
 */
public class TypeScriptMergerTest {

    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Checks if node is installed and version
     */
    @Test
    public void testNode() {
        String version = new String();
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "node --version");

        Process p;
        try {
            p = builder.start();
            try (InputStreamReader rdr = new InputStreamReader(p.getInputStream());
                BufferedReader r = new BufferedReader(rdr)) {
                String line;
                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    } else {
                        version = version.concat(line);
                    }
                }
            }

        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(version.startsWith("v6"));
    }

    /**
     * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = false
     */
    @Test
    public void testMergingNoOverrides() {
        // arrange
        File baseFile = new File(testFileRootPath + "base.ts");

        // act
        String mergedContents = new TypeScriptMerger("tsmerge", false).merge(baseFile, readTSFile("patch.ts"), "UTF-8");

        assertTrue(mergedContents.contains("bProperty"));
        assertTrue(mergedContents.contains("aProperty: number = 2"));
        assertTrue(mergedContents.contains("bMethod"));
        assertTrue(mergedContents.contains("aMethod"));
        assertTrue(mergedContents.contains("bProperty"));
        assertTrue(mergedContents.contains("import { c, f } from 'd'"));
        assertTrue(mergedContents.contains("import { a, e } from 'b'"));
    }

    /**
     * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = true
     */
    @Test
    public void testMergingOverrides() {
        // arrange
        File baseFile = new File(testFileRootPath + "base.ts");

        // act
        String mergedContents = new TypeScriptMerger("tsmerge", true).merge(baseFile, readTSFile("patch.ts"), "UTF-8");

        assertTrue(mergedContents.contains("bProperty"));
        assertTrue(mergedContents.contains("aProperty: number = 3"));
        assertTrue(mergedContents.contains("bMethod"));
        assertTrue(mergedContents.contains("aMethod"));
        assertTrue(mergedContents.contains("bProperty"));
        assertTrue(mergedContents.contains("import { c, f } from 'd'"));
        assertTrue(mergedContents.contains("import { a, e } from 'b'"));
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
