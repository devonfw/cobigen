package com.capgemini.cobigen.javaplugin.unittest.merger;

import static com.capgemini.cobigen.javaplugin.util.JavaParserUtil.getFirstJavaClass;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.javaplugin.merger.JavaMerger;
import com.thoughtworks.qdox.model.JavaSource;

/**
 *
 * @author sholzer (Jun 16, 2015)
 */
public class BugOneOEight {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    @Test
    public void test() throws IOException, MergeException {

        File base = new File(testFileRootPath + "BaseFile_QualType.java");
        File patch = new File(testFileRootPath + "PatchFile_QualType.java");

        JavaSource mergedSource = getMergedSource(base, patch, true);

        System.out.print(mergedSource.toString());

    }

    /**
     * Calls the {@link JavaMerger} to merge the base and patch file wit the given overriding behavior
     * @param baseFile
     *            base file
     * @param patchFile
     *            patch file
     * @param override
     *            overriding behavior
     * @return the merged {@link JavaSource}
     * @throws IOException
     *             if one of the files could not be read
     * @throws MergeException
     *             test fails
     * @author mbrunnli (17.04.2013)
     */
    private JavaSource getMergedSource(File baseFile, File patchFile, boolean override) throws IOException,
        MergeException {
        String mergedContents =
            new JavaMerger("", override).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");
        return getFirstJavaClass(new StringReader(mergedContents)).getSource();
    }

}
