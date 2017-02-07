package com.capgemini.cobigen.jsonplugin.merger.generic;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 */
public class GenericJSONMerger {

    private JsonObject base;

    private JsonObject patch;

    public GenericJSONMerger(JsonObject base, JsonObject patch) {
        this.base = base;
        this.patch = patch;
    }

    public String merge(boolean patchOverrides) {
        try {
            extendJsonObject(base, patchOverrides, patch);
        } catch (JsonObjectExtensionConflictException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return base.toString();
    }

    public static class JsonObjectExtensionConflictException extends Exception {

        public JsonObjectExtensionConflictException(String message) {
            super(message);
        }

    }

    public static void extendJsonObject(JsonObject destinationObject, boolean patchOverrides, JsonObject objPatch)
        throws JsonObjectExtensionConflictException {
        extendJsonObject(destinationObject, objPatch, patchOverrides);
    }

    private static void extendJsonObject(JsonObject leftObj, JsonObject rightObj, boolean patchOverrides)
        throws JsonObjectExtensionConflictException {
        for (Map.Entry<String, JsonElement> rightEntry : rightObj.entrySet()) {
            String rightKey = rightEntry.getKey();
            JsonElement rightVal = rightEntry.getValue();
            if (leftObj.has(rightKey)) {
                if (patchOverrides) {
                    handleMergeConflict(rightKey, leftObj, rightVal);
                } else {
                    JsonElement leftVal = leftObj.get(rightKey);
                    if (leftVal.isJsonArray() && rightVal.isJsonArray()) {
                        JsonArray leftArr = leftVal.getAsJsonArray();
                        JsonArray rightArr = rightVal.getAsJsonArray();
                        // concat the arrays -- there cannot be a conflict in an array, it's just a collection
                        // of stuff
                        for (int i = 0; i < rightArr.size(); i++) {
                            leftArr.add(rightArr.get(i));
                        }
                    } else if (leftVal.isJsonObject() && rightVal.isJsonObject()) {
                        // recursive merging
                        extendJsonObject(leftVal.getAsJsonObject(), rightVal.getAsJsonObject(), patchOverrides);
                    } else {// not both arrays or objects, normal merge with conflict resolution
                        handleMergeConflict(rightKey, leftObj, rightVal);
                    }
                }
            } else {// no conflict, add to the object
                leftObj.add(rightKey, rightVal);
            }
        }
    }

    private static void handleMergeConflict(String key, JsonObject leftObj, JsonElement rightVal)
        throws JsonObjectExtensionConflictException {

        leftObj.remove(key);
        leftObj.add(key, rightVal);

    }
}
