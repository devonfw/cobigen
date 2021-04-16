package com.devonfw.cobigen.htmlplugin.unittest.merger.ng2;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.devonfw.cobigen.htmlplugin.merger.AngularMerger;
import com.devonfw.cobigen.htmlplugin.merger.constants.Constants;

/** Test suite for {@link AngularMerger} regarding NG2 templates */
public class HTMLNG2MergerTest {

    /** Resource root path of test resources */
    private static final String testFileRootPathNG2 = "src/test/resources/testdata/unittest/merger/ng2/";

    @Test
    public void htmlMergeTest_AddSideBarButton() {
        Document mergedContents =
            htmlMerger(testFileRootPathNG2, "app.component.base.html", "app.component.patch.html", false, "html-ng*");
        Element sideBar = mergedContents.getElementsByTag(Constants.MD_NAV_LIST).first();
        Elements listEntry = sideBar.getElementsByTag(Constants.A_REF);
        assertThat(listEntry).hasSize(3);
    }

    @Test
    public void htmlMergeTest_AddSideBarButton_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "app.component.base.html", "app.component.patch.html",
            true, "html-ng*_override");
        Element sideBar = mergedContents.getElementsByTag(Constants.MD_NAV_LIST).first();
        Elements listEntry = sideBar.getElementsByTag(Constants.A_REF);
        assertThat(listEntry).hasSize(2);
    }

    @Test
    public void htmlMergeTest_AddFilterField() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "dataGrid.component.base.html",
            "dataGrid.component.patch.html", false, "html-ng*");
        assertThat(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER)).hasSize(5);
    }

    @Test
    public void htmlMergeTest_AddFilterField_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "dataGrid.component.base.html",
            "dataGrid.component.patch.html", true, "html-ng*_override");
        assertThat(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER)).hasSize(1);
    }

    @Test
    public void htmlMergeTest_AddDialogAddField() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "addDialog.component.base.html",
            "addDialog.component.patch.html", false, "html-ng*");
        assertThat(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER)).hasSize(5);
    }

    @Test
    public void htmlMergeTest_AddDialogAddField_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "addDialog.component.base.html",
            "addDialog.component.patch.html", true, "html-ng*_override");
        assertThat(mergedContents.getElementsByTag(Constants.INPUT_CONTAINER)).hasSize(1);
    }

    public Document htmlMerger(String rootPath, String fileBase, String filePatch, boolean patchOverrides,
        String mergeStrategy) {
        File htmlBaseFile = new File(rootPath + fileBase).getAbsoluteFile();
        File htmlPatchFile = new File(rootPath + filePatch).getAbsoluteFile();

        try (FileReader reader = new FileReader(htmlPatchFile)) {
            String patchString = IOUtils.toString(reader);
            return Jsoup.parse(
                new AngularMerger(mergeStrategy, patchOverrides).merge(htmlBaseFile, patchString, "UTF-8"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace(); // stack trace does not seem to be printed without -X anymore
            throw new AssertionError("An error occurred accessing test resources", e);
        }

    }
}
