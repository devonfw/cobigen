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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Test methods for different JSON mergers of the plugin
 */
public class JSONMergerTest {

    /**
     * Path for unit test files
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Checks merge for Generic JSON files for OVERRIDE cases
     */
    @Test
    public void jsonGenericMergeTest_Override() {

        // arrange
        File jsonBaseFile = new File(testFileRootPath + "en_json");

        // act
        String mergedContents =
            new JSONMerger("jsonmerge", true).merge(jsonBaseFile, readJsonFile("en_patch_json"), "UTF-8");
        JSONTokener tokensResult = new JSONTokener(mergedContents);
        JSONObject jsonResult = new JSONObject(tokensResult);

        // assert
        assertTrue(jsonResult.getJSONObject("datagrid").getJSONObject("columns").length() == 1);
        assertTrue(jsonResult.has("newdatagrid"));
    }

    /**
     * Checks merge for Generic JSON files for NO OVERRIDE cases
     */
    @Test
    public void jsonGenericMergeTest_NoOverride() {

        // arrange
        File jsonBaseFile = new File(testFileRootPath + "en_json");

        // act
        String mergedContents =
            new JSONMerger("jsonmerge", false).merge(jsonBaseFile, readJsonFile("en_patch_json"), "UTF-8");
        JSONTokener tokensResult = new JSONTokener(mergedContents);
        JSONObject jsonResult = new JSONObject(tokensResult);

        // assert
        assertTrue(jsonResult.getJSONObject("datagrid").getJSONObject("columns").length() == 5);
        assertTrue(jsonResult.has("newdatagrid"));
    }

    /**
     * Checks merge for Sencha Architect. The test will cover {@link JsonObject} and {@link JsonArray} inside
     * one or more {@link JsonObject} merge for NO OVERRIDE cases
     */
    @Test
    public void senchaArchitectMergeTest_NoOverride() {

        // arrange
        File jsonBaseFile = new File(testFileRootPath + "Base_json");

        // act
        String mergedContents =
            new JSONMerger("sencharchmerge", false).merge(jsonBaseFile, readJsonFile("Patch_json"), "UTF-8");
        JSONTokener tokensResult = new JSONTokener(readJsonFile("Result_json"));
        JSONObject jsonResult = new JSONObject(tokensResult);

        // assert
        assertTrue(mergedContents.equals(jsonResult.toString(4)));
    }

    /**
     * Checks merge for Sencha Architect. The test will cover {@link JsonObject} and {@link JsonArray} inside
     * one or more {@link JsonObject} merge for OVERRIDE cases
     */
    @Test
    public void senchaArchitectMergeTest_Override() {

        // arrange
        File jsonBaseFile = new File(testFileRootPath + "Base_json");

        // act
        String mergedContents =
            new JSONMerger("sencharchmerge_override", true).merge(jsonBaseFile, readJsonFile("Patch_json"), "UTF-8");
        JSONTokener tokensResult = new JSONTokener(readJsonFile("ResultOverride_json"));
        JSONObject jsonResult = new JSONObject(tokensResult);
        JSONArray toComp = jsonResult.getJSONObject("descriptor").getJSONObject("app2").getJSONArray("messages");
        JSONTokener tokensJson = new JSONTokener(mergedContents);
        JSONObject json = new JSONObject(tokensJson);
        JSONArray toCompRes = json.getJSONObject("descriptor").getJSONObject("app2").getJSONArray("messages");

        // assert
        assertTrue(toCompRes.toString().equals(toComp.toString()));
    }

    /**
     * Reads the JSON file given by parameter
     * @param fileName
     *            the file to read
     * @return the string with the file contents
     */
    private String readJsonFile(String fileName) {
        File jsonPatchFile = new File(testFileRootPath + fileName);
        String file = jsonPatchFile.getAbsolutePath();
        Reader reader = null;
        String returnString;

        try {
            reader = new FileReader(file);
            returnString = IOUtils.toString(reader);
            reader.close();

        } catch (FileNotFoundException e) {
            throw new MergeException(jsonPatchFile, "Can not read file " + jsonPatchFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(jsonPatchFile, "Can not read the base file " + jsonPatchFile.getAbsolutePath());
        }

        return returnString;
    }
}
