package com.capgemini.cobigen_jsonplugin.unittest.merger;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.jsonplugin.merger.JSONMerger;

@SuppressWarnings("javadoc")
public class JSONMergerTest {

    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    @Test
    public void jsonMergeTest_NoOverride() {
        File jsonBaseFile = new File(testFileRootPath + "Base_json");
        File jsonPatchFile = new File(testFileRootPath + "Patch_json");
        File jsonResultFile = new File(testFileRootPath + "Result_json");

        String file = jsonPatchFile.getAbsolutePath();

        String fileResult = jsonResultFile.getAbsolutePath();

        Reader reader = null;
        String patchString;
        String resultString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(jsonPatchFile, "Can not read file " + jsonPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonPatchFile, "Can not read the base file " + jsonPatchFile.getAbsolutePath());
        }

        try {
            reader = new FileReader(fileResult);
            resultString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(jsonResultFile, "Can not read file " + jsonResultFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonResultFile, "Can not read the base file " + jsonResultFile.getAbsolutePath());
        }

        String mergedContents = new JSONMerger("sencharchmerge", false).merge(jsonBaseFile, patchString, "UTF-8");
        JSONTokener tokensResult = new JSONTokener(resultString);
        JSONObject jsonResult = new JSONObject(tokensResult);
        assertTrue(mergedContents.equals(jsonResult.toString(4)));
    }

    @Test
    public void jsonMergeTest_Override() {
        File jsonBaseFile = new File(testFileRootPath + "Base_json");
        File jsonPatchFile = new File(testFileRootPath + "Patch_json");
        File jsonResultFile = new File(testFileRootPath + "ResultOverride_json");

        String file = jsonPatchFile.getAbsolutePath();

        String fileResult = jsonResultFile.getAbsolutePath();

        Reader reader = null;
        String patchString;
        String resultString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(jsonPatchFile, "Can not read file " + jsonPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonPatchFile, "Can not read the base file " + jsonPatchFile.getAbsolutePath());
        }

        try {
            reader = new FileReader(fileResult);
            resultString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(jsonResultFile, "Can not read file " + jsonResultFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonResultFile, "Can not read the base file " + jsonResultFile.getAbsolutePath());
        }

        String mergedContents =
            new JSONMerger("sencharchmerge_override", true).merge(jsonBaseFile, patchString, "UTF-8");
        JSONTokener tokensResult = new JSONTokener(resultString);
        JSONObject jsonResult = new JSONObject(tokensResult);
        JSONArray toComp = jsonResult.getJSONObject("descriptor").getJSONObject("app2").getJSONArray("messages");
        JSONTokener tokensJson = new JSONTokener(mergedContents);
        JSONObject json = new JSONObject(tokensJson);
        JSONArray toCompRes = json.getJSONObject("descriptor").getJSONObject("app2").getJSONArray("messages");
        assertTrue(toCompRes.toString().equals(toComp.toString()));

    }

    @Test
    public void jsonGenericMergeTest_Override() {
        File jsonBaseFile = new File(testFileRootPath + "en_json");
        File jsonPatchFile = new File(testFileRootPath + "en_patch_json");

        String file = jsonPatchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(jsonPatchFile, "Can not read file " + jsonPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonPatchFile, "Can not read the base file " + jsonPatchFile.getAbsolutePath());
        }

        String mergedContents = new JSONMerger("jsonmerge", true).merge(jsonBaseFile, patchString, "UTF-8");
        JSONTokener tokensResult = new JSONTokener(mergedContents);
        JSONObject jsonResult = new JSONObject(tokensResult);
        assertTrue(jsonResult.getJSONObject("datagrid").getJSONObject("columns").length() == 1);
        assertTrue(jsonResult.has("newdatagrid"));
    }

    @Test
    public void jsonGenericMergeTest_NoOverride() {
        File jsonBaseFile = new File(testFileRootPath + "en_json");
        File jsonPatchFile = new File(testFileRootPath + "en_patch_json");

        String file = jsonPatchFile.getAbsolutePath();

        Reader reader = null;
        String patchString;

        try {
            reader = new FileReader(file);
            patchString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(jsonPatchFile, "Can not read file " + jsonPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonPatchFile, "Can not read the base file " + jsonPatchFile.getAbsolutePath());
        }

        String mergedContents = new JSONMerger("jsonmerge", false).merge(jsonBaseFile, patchString, "UTF-8");
        JSONTokener tokensResult = new JSONTokener(mergedContents);
        JSONObject jsonResult = new JSONObject(tokensResult);
        assertTrue(jsonResult.getJSONObject("datagrid").getJSONObject("columns").length() == 5);
        assertTrue(jsonResult.has("newdatagrid"));
    }
}
