package com.capgemini.cobigen_htmlplugin.unittest.merger;

import java.io.File;

import org.junit.Test;

import com.capgemini.cobigen.htmlplugin.merger.HTMLMerger;

@SuppressWarnings("javadoc")
public class HTMLMergerTest {

    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    @Test
    public void htmlMergeTest_AddSideBarButton() {
        File htmlBaseFile = new File(testFileRootPath + "app.component.html");
        new HTMLMerger("htmlng2", false).merge(htmlBaseFile, "", "UTF-8");
    }

    @Test
    public void htmlMergeTest_AddSideBarButton_Override() {
        File htmlBaseFile = new File(testFileRootPath + "app.component.html");
        new HTMLMerger("htmlng2_override", true).merge(htmlBaseFile, "", "UTF-8");
    }
}
