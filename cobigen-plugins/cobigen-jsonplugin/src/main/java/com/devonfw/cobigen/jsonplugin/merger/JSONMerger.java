package com.devonfw.cobigen.jsonplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.jsonplugin.merger.general.constants.Constants;
import com.devonfw.cobigen.jsonplugin.merger.generic.GenericJSONMerger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * The {@link JSONMerger} merges a patch and the base file of the same JSON file. The merger is a recursive method that
 * goes through all children of each {@link JsonElement} merging them if necessary
 */
public class JSONMerger implements Merger {

  /** Merger Type to be registered */
  private String type;

  /** The conflict resolving mode */
  private boolean patchOverrides;

  /**
   * Creates a new {@link JSONMerger}
   *
   * @param type merger type
   * @param patchOverrides if <code>true</code>, conflicts will be resolved by using the patch contents<br>
   *        if <code>false</code>, conflicts will be resolved by using the base contents
   */
  public JSONMerger(String type, boolean patchOverrides) {

    this.type = type;
    this.patchOverrides = patchOverrides;
  }

  @Override
  public String getType() {

    return this.type;
  }

  @Override
  public String merge(File base, String patch, String targetCharset) throws MergeException {

    String file = base.getAbsolutePath();
    JsonObject objBase = null;
    JsonObject objPatch = null;

    try (InputStream in = Files.newInputStream(base.toPath());
        InputStreamReader inSR = new InputStreamReader(in, Charset.forName(targetCharset));
        JsonReader reader = new JsonReader(inSR);) {

      JsonParser parser = new JsonParser();
      JsonElement jsonBase = parser.parse(reader);
      objBase = jsonBase.getAsJsonObject();
    } catch (JsonIOException e) {
      throw new MergeException(base, "Not JSON file", e);
    } catch (JsonSyntaxException e) {
      throw new MergeException(base, "JSON syntax error. ", e);
    } catch (FileNotFoundException e) {
      throw new MergeException(base, "File not found", e);
    } catch (IOException e) {
      throw new MergeException(base, "Could not read " + file, e);
    }

    try {
      JsonParser parser = new JsonParser();
      JsonElement jsonPatch = parser.parse(patch);
      objPatch = jsonPatch.getAsJsonObject();
    } catch (JsonIOException e) {
      throw new MergeException(base, "Not JSON patch code", e);
    } catch (JsonSyntaxException e) {
      throw new MergeException(base, "JSON Patch syntax error. ", e);
    }

    JsonObject result = null;

    // Override would be defined by patchOverrides at PluginActivator
    if (this.type.contains(Constants.GENERIC_MERGE)) {
      GenericJSONMerger ng2merge = new GenericJSONMerger(objBase, objPatch);
      result = ng2merge.merge(this.patchOverrides);
    } else {
      throw new MergeException(base, "Merge strategy not yet supported!");
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(result);
  }

}
