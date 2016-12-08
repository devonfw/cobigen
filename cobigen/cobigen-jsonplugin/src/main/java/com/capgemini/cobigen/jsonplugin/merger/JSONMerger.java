package com.capgemini.cobigen.jsonplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
 * The {@link JSONMerger} merges a patch and the base file of the same JSON file. The merger is a recursive
 * method that goes through all children of each {@link JsonElement} merging them if necessary
 *
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

        List<JsonObject> patchColumns = getPatchColumns(objPatch);
        String result = null;
        switch (type) {
        case "sencharchmerge":
            result = senchArchMerge(patchColumns, objBase, patchOverrides, objPatch);
            break;
        case "sencharchmerge_override":
            result = senchArchMerge(patchColumns, objBase, patchOverrides, objPatch);
            break;
        default:
            throw new MergeException(base, "Merge strategy not yet supported!");
        }

        JSONTokener tokensBase = new JSONTokener(result);
        JSONObject jsonBase = new JSONObject(tokensBase);
        return jsonBase.toString(4);
    }

    /**
     * @param objPatch
     * @return
     */
    private List<JsonObject> getPatchColumns(JsonObject objPatch) {
        List<JsonObject> columns = new LinkedList<>();
        Set<Entry<String, JsonElement>> patchEntry = objPatch.entrySet();
        Iterator<Entry<String, JsonElement>> it = patchEntry.iterator();
        while (it.hasNext()) {
            Entry<String, JsonElement> next = it.next();
            if (next.getKey().equals("cn")) {
                JsonObject table = next.getValue().getAsJsonArray().get(1).getAsJsonObject();
                JsonArray cols = table.get("cn").getAsJsonArray();
                for (int i = 1; i < cols.size() - 1; i++) {
                    columns.add(cols.get(i).getAsJsonObject());
                }
            }
        }
        return columns;
    }

    /**
     * Merge a collection of JSON patch files
     * @param destinationObject
     *            the destination {@link JsonObject}
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @param objs
     *            collection of patches
     * @return the result string of the merge
     */
    public String senchArchMerge(List<JsonObject> patchColumns, JsonObject destinationObject,
        boolean patchOverrides, JsonObject... objs) {
        for (JsonElement obj : objs) {
            senchArchMerge(patchColumns, destinationObject, obj.getAsJsonObject(), patchOverrides);
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
     */
    private void senchArchMerge(List<JsonObject> patchColumns, JsonObject leftObj, JsonObject rightObj,
        boolean patchOverrides) {
        for (Map.Entry<String, JsonElement> rightEntry : rightObj.entrySet()) {
            String rightKey = rightEntry.getKey();
            JsonElement rightVal = rightEntry.getValue();
            if (leftObj.has(rightKey)) {
                // conflict
                JsonElement leftVal = leftObj.get(rightKey);
                if (leftVal.isJsonArray() && rightVal.isJsonArray()) {
                    JsonArray leftArr = leftVal.getAsJsonArray();
                    JsonArray rightArr = rightVal.getAsJsonArray();

                    if (patchOverrides) {
                        int size = leftArr.size();
                        for (int i = 0; i < size; i++) {
                            leftArr.remove(0);
                        }
                        leftArr.addAll(rightArr);
                    } else {
                        // add patch elements without add the duplicates
                        int size = rightArr.size();
                        boolean exist = false;
                        int posToAdd = 0;
                        for (int i = 0; i < size; i++) {
                            int size2 = leftArr.size();
                            for (int j = 0; j < size2; j++) {
                                if (leftArr.get(j).equals(rightArr.get(i))) {
                                    exist = true;
                                    break;
                                } else {
                                    posToAdd = i;
                                }
                                if (rightArr.get(i).isJsonObject() && leftArr.get(j).isJsonObject()) {
                                    JsonObject baseObject = rightArr.get(i).getAsJsonObject();
                                    JsonObject patchObject = leftArr.get(j).getAsJsonObject();
                                    if (baseObject.get("userConfig").getAsJsonObject().get("reference")
                                        .equals(patchObject.get("userConfig").getAsJsonObject()
                                            .get("reference"))) {
                                        System.out.println("es la misma tabla " + patchColumns.size());
                                        exist = true;
                                        for (JsonObject column : patchColumns) {
                                            System.out.println(baseObject.get("cn").getAsJsonArray().size());
                                            if (!baseObject.get("cn").getAsJsonArray().contains(column)) {
                                                System.out.println("no contiene " + column.toString());
                                                baseObject.get("cn").getAsJsonArray().add(column);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            if (!exist) {
                                leftArr.add(rightArr.get(posToAdd));
                            }
                            exist = false;
                        }
                    }
                } else if (leftVal.isJsonObject() && rightVal.isJsonObject()) {
                    // recursive merging
                    senchArchMerge(patchColumns, leftVal.getAsJsonObject(), rightVal.getAsJsonObject(),
                        patchOverrides);
                } else {// not both arrays or objects, normal merge with conflict resolution
                    if (patchOverrides
                        && !(rightKey.equals("designerId") || rightKey.equals("viewControllerInstanceId")
                            || rightKey.equals("viewModelInstanceId"))) {
                        leftObj.add(rightKey, rightVal);// right side auto-wins, replace left val with its val
                    }
                }
            } else {// no conflict, add to the object
                leftObj.add(rightKey, rightVal);
            }
        }
    }
}
