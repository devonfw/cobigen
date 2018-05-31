package com.capgemini.cobigen.jsonplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.jsonplugin.merger.general.constants.Constants;
import com.capgemini.cobigen.jsonplugin.merger.generic.GenericJSONMerger;
import com.capgemini.cobigen.jsonplugin.merger.senchaarchitect.SenchaArchitectMerger;
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

        String result = null;

        // Override would be defined by patchOverrides at PluginActivator
        if (type.contains(Constants.SENCHA_ARCHITECT)) {
            SenchaArchitectMerger senchArchMerger = new SenchaArchitectMerger(objBase, objPatch);
            result = senchArchMerger.senchArchMerge(patchOverrides);
        } else if (type.contains(Constants.GENERIC_MERGE)) {
            GenericJSONMerger ng2merge = new GenericJSONMerger(objBase, objPatch);
            result = ng2merge.merge(patchOverrides);
        } else {
            throw new MergeException(base, "Merge strategy not yet supported!");
        }

        JSONTokener tokensBase = new JSONTokener(result);
        JSONObject jsonBase = new JSONObject(tokensBase);
        return jsonBase.toString(4);
    }

}
