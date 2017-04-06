package com.capgemini.cobigen.tsplugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.tsplugin.merger.TypeScriptMerger;

@SuppressWarnings("javadoc")
public class TypeScriptMergerTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    private static String index = "src/main/resources/tsm/";

    private static String indexTest = "src/main/resources/tsm/test/";

    @Test
    public void addObjectPropertyTest_NoOverride() throws IOException {
        File baseFile = new File(testFileRootPath + "test.ts");
        File patchFile = new File(testFileRootPath + "test_patch.ts");
        File indexFile = new File(index);
        File indexTestFile = new File(indexTest);

        // String file = indexFile.getAbsolutePath();
        // String test = indexTestFile.getAbsolutePath();
        // System.out.println(file);
        String tsBaseFile = baseFile.getAbsolutePath();
        String tsPatchFile = patchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(tsPatchFile);
            patchString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(patchFile, "Can not read the base file " + tsPatchFile);
        } catch (IOException e) {
            throw new MergeException(patchFile, "Can not read the base file " + tsPatchFile);
        }

        String mergedContents = new TypeScriptMerger("tsmerge", false).merge(baseFile, patchString, "UTF-8");
        System.out.println(mergedContents);
        // String line;
        // new ProcessBuilder("cmd.exe", "/c", "more " + baseFile.getAbsolutePath() + " > " + test +
        // "\\temp.ts").start();
        // new ProcessBuilder("cmd.exe", "/c", "more " + patchFile.getAbsolutePath() + " > " + test +
        // "\\temp_patch.ts")
        // .start();
        // ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd " + file + " && ts-node
        // src\\index.ts");
        // builder.redirectErrorStream(true);
        // Process p = builder.start();
        // BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        //
        // while (true) {
        // line = r.readLine();
        // if (line == null) {
        // break;
        // }
        // System.out.println(line);
        // }

    }

}
