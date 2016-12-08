package com.capgemini.cobigen.jsonplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
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

        Map<String, JsonObject> patchColumns = getPatchColumns(objPatch);
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
     *            the patch grid
     * @return columns of the grid
     */
    private Map<String, JsonObject> getPatchColumns(JsonObject objPatch) {
        Map<String, JsonObject> columns = new HashMap<>();
        Set<Entry<String, JsonElement>> patchEntry = objPatch.entrySet();
        Iterator<Entry<String, JsonElement>> it = patchEntry.iterator();
        while (it.hasNext()) {
            Entry<String, JsonElement> next = it.next();
            if (next.getKey().equals("cn")) {
                JsonObject table = next.getValue().getAsJsonArray().get(1).getAsJsonObject();
                if (table.has("cn")) {
                    JsonArray cols = table.get("cn").getAsJsonArray();
                    for (int i = 0; i < cols.size(); i++) {
                        if (cols.get(i).getAsJsonObject().get("type").getAsString()
                            .equals("Ext.grid.column.Column")) {
                            String name = cols.get(i).getAsJsonObject().get("name").getAsString();
                            JsonObject column = cols.get(i).getAsJsonObject();
                            columns.put(name, column);
                        }

                    }
                }

            }
        }
        return columns;
    }

    /**
     * Merge a collection of JSON patch files
     * @param patchColumns
     *            columns of the grid to patch
     * @param destinationObject
     *            the destination {@link JsonObject}
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @param objs
     *            collection of patches
     * @return the result string of the merge
     */
    public String senchArchMerge(Map<String, JsonObject> patchColumns, JsonObject destinationObject,
        boolean patchOverrides, JsonObject... objs) {
        for (JsonElement obj : objs) {
            senchArchMerge(patchColumns, destinationObject, obj.getAsJsonObject(), patchOverrides);
        }

        return destinationObject.toString();
    }

    /**
     * @param patchColumns
     *            columns of the grid to patch
     * @param leftObj
     *            The patch object
     * @param rightObj
     *            the base object
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    private void senchArchMerge(Map<String, JsonObject> patchColumns, JsonObject leftObj, JsonObject rightObj,
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
                                    if (baseObject.get("userConfig").getAsJsonObject().has("reference")
                                        && patchObject.get("userConfig").getAsJsonObject().has("reference")) {
                                        if (baseObject.get("userConfig").getAsJsonObject().get("reference")
                                            .equals(patchObject.get("userConfig").getAsJsonObject()
                                                .get("reference"))) {
                                            List<String> baseColumns = getBaseGridColumns(patchObject);
                                            exist = true;
                                            for (String column : baseColumns) {
                                                if (!patchColumns.containsKey(column)) {
                                                    patchObject.get("cn").getAsJsonArray().add(column);
                                                }
                                            }
                                            // for (JsonObject column : patchColumns) {
                                            // if (!patchObject.get("cn").getAsJsonArray()
                                            // .contains(column)) {
                                            // patchObject.get("cn").getAsJsonArray().add(column);
                                            // }
                                            // }
                                            break;
                                        }
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

    /**
     * @param patchObject
     *            the base grid
     * @return the columns of the base grid
     */
    private List<String> getBaseGridColumns(JsonObject patchObject) {
        JsonArray fields = patchObject.get("cn").getAsJsonArray();
        List<String> columns = new LinkedList<>();
        for (int i = 0; i < fields.size(); i++) {
            JsonObject field = fields.get(i).getAsJsonObject();
            if (field.get("type").equals("Ext.grid.column.Column")) {
                System.out.println(field.get("name").getAsString());
                columns.add(field.get("name").getAsString());
            }
        }
        return columns;
    }
}
