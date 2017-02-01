package com.capgemini.cobigen_htmlplugin.unittest.merger;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.htmlplugin.merger.HTMLMerger;
import com.capgemini.cobigen.htmlplugin.merger.utils.ng2.ConstantsNG2;

@SuppressWarnings("javadoc")
public class HTMLMergerTest {

    private static String testFileRootPathNG2 = "src/test/resources/testdata/unittest/merger/ng2/";

    @Test
    public void htmlMergeTest_AddSideBarButton() {
        Document mergedContents =
            htmlMerger(testFileRootPathNG2, "app.component.html_Base", "app.component.html_Patch", false, "htmlng2");
        Element sideBar = mergedContents.getElementsByTag(ConstantsNG2.MD_NAV_LIST).first();
        Elements listEntry = sideBar.getElementsByTag(ConstantsNG2.A_REF);
        assertTrue(listEntry.size() == 3);
    }

    @Test
    public void htmlMergeTest_AddSideBarButton_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "app.component.html_Base", "app.component.html_Patch",
            true, "htmlng2_override");
        Element sideBar = mergedContents.getElementsByTag(ConstantsNG2.MD_NAV_LIST).first();
        Elements listEntry = sideBar.getElementsByTag(ConstantsNG2.A_REF);
        assertTrue(listEntry.size() == 2);
    }

    @Test
    public void htmlMergeTest_AddFilterField() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "dataGrid.component.html_Base",
            "dataGrid.component.html_Patch", false, "htmlng2");
        assertTrue(mergedContents.getElementsByTag(ConstantsNG2.INPUT_CONTAINER).size() == 5);
    }

    @Test
    public void htmlMergeTest_AddFilterField_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "dataGrid.component.html_Base",
            "dataGrid.component.html_Patch", true, "htmlng2_override");
        assertTrue(mergedContents.getElementsByTag(ConstantsNG2.INPUT_CONTAINER).size() == 1);
    }

    @Test
    public void htmlMergeTest_AddDialogAddField() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "addDialog.component.html_Base",
            "addDialog.component.html_Patch", false, "htmlng2");
        assertTrue(mergedContents.getElementsByTag(ConstantsNG2.INPUT_CONTAINER).size() == 5);
    }

    @Test
    public void htmlMergeTest_AddDialogAddField_Override() {
        Document mergedContents = htmlMerger(testFileRootPathNG2, "addDialog.component.html_Base",
            "addDialog.component.html_Patch", true, "htmlng2_override");
        assertTrue(mergedContents.getElementsByTag(ConstantsNG2.INPUT_CONTAINER).size() == 1);
    }

    public Document htmlMerger(String rootPath, String fileBase, String filePatch, boolean patchOverrides,
        String mergeStrategy) {
        File htmlBaseFile = new File(rootPath + fileBase);
        File htmlPatchFile = new File(rootPath + filePatch);

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(htmlPatchFile);
            patchString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(htmlPatchFile, "Can not read file " + htmlPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(htmlPatchFile, "Can not read the base file " + htmlPatchFile.getAbsolutePath());
        }
        return Jsoup.parse(new HTMLMerger(mergeStrategy, patchOverrides).merge(htmlBaseFile, patchString, "UTF-8"),
            "UTF-8");

    }
}
