package com.devonfw.cobigen.jsonplugin.merger.generic;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * JSON Merger for generic purposes
 */
public class GenericJSONMerger {

  /**
   * Existent JSON file
   */
  private JsonObject base;

  /**
   * JSON file to patch
   */
  private JsonObject patch;

  /**
   * Constructor
   *
   * @param base existent file
   * @param patch file to patch
   */
  public GenericJSONMerger(JsonObject base, JsonObject patch) {

    this.base = base;
    this.patch = patch;
  }

  /**
   * Calls the recursive method
   *
   * @param patchOverrides states if the merge must override the base or not
   * @return the JSON resulting from the merge
   */
  public JsonObject merge(boolean patchOverrides) {

    extendJsonObject(this.base, this.patch, patchOverrides);
    return this.base;
  }

  /**
   * Merge the file being recursive for each {@link JsonObject}
   *
   * @param leftObj The base {@link JsonObject}
   * @param rightObj The patch {@link JsonObject}
   * @param patchOverrides merge strategy
   */
  private static void extendJsonObject(JsonObject leftObj, JsonObject rightObj, boolean patchOverrides) {

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
            JsonArray rightArr = rightVal.getAsJsonArray(); // concat the arrays -- there cannot
                                                            // be a conflict in an array, it's
                                                            // just a collection // of stuff
            for (int i = 0; i < rightArr.size(); i++) {
              leftArr.add(rightArr.get(i));
            }
          } else if (leftVal.isJsonObject() && rightVal.isJsonObject()) { // recursive merging
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

  /**
   * Handles conflicts removing previous key and adding the new one
   *
   * @param key key to patch
   * @param leftObj base {@link JsonObject}
   * @param rightVal patch value for the giving key
   */
  private static void handleMergeConflict(String key, JsonObject leftObj, JsonElement rightVal) {

    leftObj.remove(key);
    leftObj.add(key, rightVal);

  }
}
