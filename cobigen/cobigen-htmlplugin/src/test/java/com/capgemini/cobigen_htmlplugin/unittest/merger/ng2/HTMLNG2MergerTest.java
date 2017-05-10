package com.capgemini.cobigen_htmlplugin.unittest.merger.ng2;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.capgemini.cobigen.htmlplugin.merger.AngularMerger;
import com.capgemini.cobigen.htmlplugin.merger.constants.Constants;

@SuppressWarnings("javadoc")
public class HTMLNG2MergerTest {

    private static String testFileRootPathNG2 = "src/test/resources/testdata/unittest/merger/ng2/";

    @Test
    public void htmlMergeTest_AddSideBarButton() {
        Document mergedContents =
            htmlMerger(testFileRootPathNG2, "app.component.base.html", "app.component.patch.html", false, "html-ng*");
        Element sideBar = mergedContents.getElementsByTag(Constants.MD_NAV_LIST).first();
        Elements listEntry = sideBar.getElementsByTag(Constants.A_REF);
        assertTrue(listEntry.size() == 3);
    }

    @Test
    public void htmlMergeTest_AddSideBarButton_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "app.component.base.html", "app.component.patch.html",
            true, "html-ng*_override");
        Element sideBar = mergedContents.getElementsByTag(Constants.MD_NAV_LIST).first();
        Elements listEntry = sideBar.getElementsByTag(Constants.A_REF);
        assertTrue(listEntry.size() == 2);
    }

    @Test
    public void htmlMergeTest_AddFilterField() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "dataGrid.component.base.html",
            "dataGrid.component.patch.html", false, "html-ng*");
        assertTrue(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER).size() == 5);
    }

    @Test
    public void htmlMergeTest_AddFilterField_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "dataGrid.component.base.html",
            "dataGrid.component.patch.html", true, "html-ng*_override");
        assertTrue(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER).size() == 1);
    }

    @Test
    public void htmlMergeTest_AddDialogAddField() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "addDialog.component.base.html",
            "addDialog.component.patch.html", false, "html-ng*");
        assertTrue(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER).size() == 5);
    }

    @Test
    public void htmlMergeTest_AddDialogAddField_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "addDialog.component.base.html",
            "addDialog.component.patch.html", true, "html-ng*_override");
        assertTrue(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER).size() == 1);
    }

    public Document htmlMerger(String rootPath, String fileBase, String filePatch, boolean patchOverrides,
        String mergeStrategy) {
        File htmlBaseFile = new File(rootPath + fileBase);
        File htmlPatchFile = new File(rootPath + filePatch);

        try (FileReader reader = new FileReader(htmlPatchFile)) {
            String patchString = IOUtils.toString(reader);
            return Jsoup.parse(
                new AngularMerger(mergeStrategy, patchOverrides).merge(htmlBaseFile, patchString, "UTF-8"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred accessing test resources", e);
        }

    }
}
