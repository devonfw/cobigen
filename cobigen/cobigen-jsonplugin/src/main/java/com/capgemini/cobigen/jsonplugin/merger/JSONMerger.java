package com.capgemini.cobigen.jsonplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author rudiazma (Sep 22, 2016)
 */
public class JSONMerger implements Merger {

    /**
     * Merger Type to be registered
     */
    private String type;

    /**
     * The conflict resolving mode
     */
    private boolean patchOverrides;

    /**
     * Creates a new {@link JSONMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @author rudiazma (Sep 22, 2016)
     */
    public JSONMerger(String type, boolean patchOverrides) {

        this.type = type;
        this.patchOverrides = patchOverrides;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {
        String file = base.getAbsolutePath();
        JsonObject objBase = null;

        JsonObject objPatch = null;

        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonBase = parser.parse(new FileReader(file));
            objBase = jsonBase.getAsJsonObject();
        } catch (JsonIOException e) {
            throw new MergeException(base, "Not JSON file");
        } catch (JsonSyntaxException e) {
            throw new MergeException(base, "JSON syntax error. " + e.getMessage());
        } catch (FileNotFoundException e) {
            throw new MergeException(base, "File not found");
        }

        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonPatch = parser.parse(patch);
            objPatch = jsonPatch.getAsJsonObject();
        } catch (JsonIOException e) {
            throw new MergeException(base, "Not JSON patch code");
        } catch (JsonSyntaxException e) {
            throw new MergeException(base, "JSON Patch syntax error. " + e.getMessage());
        }

        String result = jsonObjectMerge(objBase, patchOverrides, objPatch);
        JSONTokener tokensBase = new JSONTokener(result);
        JSONObject jsonBase = new JSONObject(tokensBase);
        return jsonBase.toString(4);
    }

    /**
     * @param destinationObject
     *            the destination Json Object
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @param objs
     *            collection of patches
     * @return the result string of the merge
     * @author rudiazma (26 de sept. de 2016)
     */
    public String jsonObjectMerge(JsonObject destinationObject, boolean patchOverrides, JsonObject... objs) {
        for (JsonElement obj : objs) {
            jsonObjectMerge(destinationObject, obj.getAsJsonObject(), patchOverrides);
        }

        return destinationObject.toString();
    }

    /**
     * @param leftObj
     *            The patch object
     * @param rightObj
     *            the base object
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @author rudiazma (26 de sept. de 2016)
     */
    private void jsonObjectMerge(JsonObject leftObj, JsonObject rightObj, boolean patchOverrides) {
        for (Map.Entry<String, JsonElement> rightEntry : rightObj.entrySet()) {
            String rightKey = rightEntry.getKey();
            JsonElement rightVal = rightEntry.getValue();
            if (leftObj.has(rightKey)) {
                // conflict
                JsonElement leftVal = leftObj.get(rightKey);
                if (leftVal.isJsonArray() && rightVal.isJsonArray()) {
                    JsonArray leftArr = leftVal.getAsJsonArray();
                    JsonArray rightArr = rightVal.getAsJsonArray();
                    leftArr = rightArr;
                    // if (patchOverrides) {
                    //
                    // int size = leftArr.size();
                    // for (int i = 0; i < size; i++) {
                    // leftArr.remove(0);
                    // }
                    // for (int i = 0; i < rightArr.size(); i++) {
                    // leftArr.add(rightArr.get(i));
                    // }
                    // } else {
                    // // add patch elements without add the duplicates
                    // int size = rightArr.size();
                    // boolean exist = false;
                    // int posToAdd = 0;
                    // for (int i = 0; i < size; i++) {
                    // int size2 = leftArr.size();
                    // for (int j = 0; j < size2; j++) {
                    // if (leftArr.get(j).equals(rightArr.get(i))) {
                    // exist = true;
                    // break;
                    // } else {
                    // posToAdd = i;
                    // }
                    // }
                    // if (!exist) {
                    // leftArr.add(rightArr.get(posToAdd));
                    // }
                    // exist = false;
                    // }
                    // }
                } else if (leftVal.isJsonObject() && rightVal.isJsonObject()) {
                    // recursive merging
                    jsonObjectMerge(leftVal.getAsJsonObject(), rightVal.getAsJsonObject(), patchOverrides);
                } else {// not both arrays or objects, normal merge with conflict resolution
                    if (patchOverrides) {
                        leftObj.add(rightKey, rightVal);// right side auto-wins, replace left val with its val
                    }
                    if (rightKey.equals("designerId") || rightKey.equals("designerId")
                        || rightKey.equals("designerId") || rightKey.equals("viewControllerInstanceId")
                        || rightKey.equals("viewModelInstanceId")) {
                        leftObj.add(rightKey, rightVal);
                    }

                }
            } else {// no conflict, add to the object
                leftObj.add(rightKey, rightVal);
            }
        }
    }
}
