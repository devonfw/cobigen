package com.capgemini.cobigen.jsonplugin.merger.senchaarchitect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.capgemini.cobigen.jsonplugin.merger.senchaarchitect.constants.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Sencha Architect Merger that merges Sencha Architect metadata json files
 */
public class SenchaArchitectMerger {

    /**
     * {@link JsonObject} base
     */
    private JsonObject objBase;

    /**
     * {@link JsonObject} patch
     */
    private JsonObject objPatch;

    /**
     * Constructor
     * @param objBase
     *            the base {@link JsonObject}
     * @param objPatch
     *            the patch {@link JsonObject}
     */
    public SenchaArchitectMerger(JsonObject objBase, JsonObject objPatch) {
        this.objBase = objBase;
        this.objPatch = objPatch;
    }

    /**
     * Gets the columns from patch mapped with his referenced name
     *
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
            if (next.getKey().equals(Constants.CN_OBJECT)) {
                JsonObject table = next.getValue().getAsJsonArray().get(1).getAsJsonObject();
                if (table.has(Constants.CN_OBJECT)) {
                    JsonArray cols = table.get(Constants.CN_OBJECT).getAsJsonArray();
                    for (int i = 0; i < cols.size(); i++) {
                        if (cols.get(i).getAsJsonObject().get(Constants.TYPE_FIELD).getAsString()
                            .equals(Constants.COLUMN_TYPE)) {
                            String name = cols.get(i).getAsJsonObject().get(Constants.NAME_FIELD).getAsString();
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
     * Merges a collection of JSON patch files
     *
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @return the result string of the merge
     */
    public String senchArchMerge(boolean patchOverrides) {
        Map<String, JsonObject> patchColumns = getPatchColumns(objBase);

        List<String> baseModelFields = getBaseModelFields(objPatch);
        senchArchMerge(patchColumns, baseModelFields, objBase, objPatch, patchOverrides);

        return objBase.toString();
    }

    /**
     * Merges two {@link JsonObject}
     *
     * @param patchColumns
     *            columns of the grid to patch
     * @param baseModelFields
     *            the fields of the model file
     * @param leftObj
     *            The patch object
     * @param rightObj
     *            the base object
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    private void senchArchMerge(Map<String, JsonObject> patchColumns, List<String> baseModelFields, JsonObject leftObj,
        JsonObject rightObj, boolean patchOverrides) {
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

                                    // check if object is label to avoid duplicates of Label types
                                    exist = isLabelType(baseObject, patchObject);
                                    if (exist) {
                                        break;
                                    }
                                    // check if object is model field
                                    exist = isModel(baseObject, patchObject, baseModelFields);
                                    if (exist) {
                                        break;
                                    }

                                    // check if object is userConfig to add columns
                                    exist = isUserConfigWithReferenceAddColumns(baseObject, patchObject, patchColumns);
                                    if (exist) {
                                        break;
                                    }
                                }
                            }
                            // if not exists, not model neither userConfig, add
                            if (!exist) {
                                leftArr.add(rightArr.get(posToAdd));
                            }
                            exist = false;
                        }
                    }
                } else if (leftVal.isJsonObject() && rightVal.isJsonObject()) {
                    // recursive merging
                    senchArchMerge(patchColumns, baseModelFields, leftVal.getAsJsonObject(), rightVal.getAsJsonObject(),
                        patchOverrides);
                } else {
                    handleConflictresolution(leftObj, rightKey, rightVal, patchOverrides);
                }
            } else {// no conflict, add to the object
                leftObj.add(rightKey, rightVal);
            }
        }
    }

    /**
     * Handles the conflict resolution depending the merge strategy
     *
     * @param leftObj
     *            the base @link JsonObject}
     * @param rightKey
     *            the patch key to resolve
     * @param rightVal
     *            the value of patch key
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    private void handleConflictresolution(JsonObject leftObj, String rightKey, JsonElement rightVal,
        boolean patchOverrides) {
        if (patchOverrides && !(rightKey.equals(Constants.DESIGNERID)
            || rightKey.equals(Constants.VIEWCONTROLLERINSTANCEID) || rightKey.equals(Constants.VIEWMODELINSTANCEID))) {
            leftObj.add(rightKey, rightVal);
        }
    }

    /**
     * Check if the {@link JsonObject} is label to avoid Label duplications on merge
     *
     * @param baseObject
     *            the existent {@link JsonObject}
     * @param patchObject
     *            the patch {@link JsonObject}
     * @return existent
     */
    private boolean isLabelType(JsonObject baseObject, JsonObject patchObject) {
        if (baseObject.has(Constants.TYPE_FIELD) && patchObject.has(Constants.TYPE_FIELD)) {
            if (baseObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.LABEL_TYPE)
                && patchObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.LABEL_TYPE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the object has the patch columns adding them if not
     *
     * @param baseObject
     *            the base {@link JsonObject}
     * @param patchObject
     *            the patch {@link JsonObject}
     * @param patchColumns
     *            the columns to patch
     * @return existent
     */
    private boolean isUserConfigWithReferenceAddColumns(JsonObject baseObject, JsonObject patchObject,
        Map<String, JsonObject> patchColumns) {
        if (baseObject.get(Constants.USERCONFIG_FIELD).getAsJsonObject().has(Constants.REFERENCE)
            && patchObject.get(Constants.USERCONFIG_FIELD).getAsJsonObject().has(Constants.REFERENCE)) {
            if (baseObject.get(Constants.USERCONFIG_FIELD).getAsJsonObject().get(Constants.REFERENCE)
                .equals(patchObject.get(Constants.USERCONFIG_FIELD).getAsJsonObject().get(Constants.REFERENCE))) {
                List<String> baseNameColumns = getBaseGridColumnNames(patchObject);
                List<String> patchNameColumns = getPatchGridColumnNames(baseObject);
                for (String column : patchNameColumns) {
                    if (!baseNameColumns.contains(column)) {
                        patchObject.get(Constants.CN_OBJECT).getAsJsonArray().add(patchColumns.get(column));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the model has the field
     *
     * @param baseObject
     *            the base {@link JsonObject}
     * @param patchObject
     *            the patch {@link JsonObject}
     * @param baseModelFields
     *            list of the fields of the base model
     * @return existent
     */
    private boolean isModel(JsonObject baseObject, JsonObject patchObject, List<String> baseModelFields) {
        if (baseObject.has(Constants.TYPE_FIELD) && patchObject.has(Constants.TYPE_FIELD)) {
            if (!baseObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.LABEL_TYPE)
                && !baseObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.COLUMN_TYPE)
                && !baseObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.PANEL_TYPE)
                && !baseObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.CONTROLLER_TYPE)) {
                if (!patchObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.LABEL_TYPE)
                    && !patchObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.COLUMN_TYPE)
                    && !patchObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.PANEL_TYPE)
                    && !patchObject.get(Constants.TYPE_FIELD).getAsString().equals(Constants.CONTROLLER_TYPE)) {
                    if (baseModelFields.contains(baseObject.get(Constants.NAME_FIELD).getAsString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the name field of the grid columns from the patch
     *
     * @param baseObject
     *            the JsonObject to extract the column names
     *
     * @return list of column names field
     */
    private List<String> getPatchGridColumnNames(JsonObject baseObject) {
        JsonArray fields = baseObject.get(Constants.CN_OBJECT).getAsJsonArray();
        List<String> columns = new LinkedList<>();
        for (int i = 0; i < fields.size(); i++) {
            JsonObject field = fields.get(i).getAsJsonObject();
            if (field.get(Constants.TYPE_FIELD).getAsString().equals(Constants.COLUMN_TYPE)) {
                columns.add(field.get(Constants.NAME_FIELD).getAsString());
            }
        }
        return columns;
    }

    /**
     * Gets the name field of the grid columns from the base
     *
     * @param patchObject
     *            the base grid
     * @return the columns of the base grid
     */
    private List<String> getBaseGridColumnNames(JsonObject patchObject) {
        JsonArray fields = patchObject.get(Constants.CN_OBJECT).getAsJsonArray();
        List<String> columns = new LinkedList<>();
        for (int i = 0; i < fields.size(); i++) {
            JsonObject field = fields.get(i).getAsJsonObject();
            if (field.get(Constants.TYPE_FIELD).getAsString().equals(Constants.COLUMN_TYPE)) {
                columns.add(field.get(Constants.NAME_FIELD).getAsString());
            }
        }
        return columns;
    }

    /**
     * Gets the list of the fields of the base model file
     *
     * @param baseObject
     *            the base model {@link JsonObject}
     * @return list of field names
     */
    private List<String> getBaseModelFields(JsonObject baseObject) {
        List<String> modelFields = new LinkedList<>();
        if (baseObject.has(Constants.TYPE_FIELD)) {
            String type = baseObject.get(Constants.TYPE_FIELD).getAsString();
            if (!type.equals(Constants.COLUMN_TYPE) && !type.equals(Constants.LABEL_TYPE)
                && !type.equals(Constants.PANEL_TYPE) && !type.equals(Constants.CONTROLLER_TYPE)) {
                if (baseObject.has(Constants.CN_OBJECT)) {
                    JsonArray fields = baseObject.get(Constants.CN_OBJECT).getAsJsonArray();
                    for (int i = 0; i < fields.size(); i++) {
                        JsonObject field = fields.get(i).getAsJsonObject();
                        modelFields.add(field.get(Constants.NAME_FIELD).getAsString());
                    }
                }
            }
        }
        return modelFields;
    }
}
