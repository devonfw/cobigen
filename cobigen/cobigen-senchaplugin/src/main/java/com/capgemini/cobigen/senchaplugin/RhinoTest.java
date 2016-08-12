package com.capgemini.cobigen.senchaplugin;

import java.io.File;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import com.capgemini.cobigen.senchaplugin.merger.JSMerger;

/**
 * test class for rhino
 * @author rudiazma (Jul 7, 2016)
 *
 */

public class RhinoTest {

    @SuppressWarnings("javadoc")
    public static void main(String[] args) throws Exception {

        File base = new File("src/main/resources/base.js");
        File patch = new File("src/main/resources/patch.js");
        File result = new File("src/main/resources/result.js");

        JSMerger merger = new JSMerger("js", true);

        String out = merger.merge(base, patch.getAbsolutePath(), null);

        FileUtils.writeStringToFile(result, out, Charsets.toCharset("UTF-8"));
    }

}
