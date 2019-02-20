package com.devonfw.cobigen.tsplugin.merger.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;

/** List of constants used on the {@link TypeScriptMerger} */
public class Constants {

    /** Bundled JS Beautifier script */
    public static final String BEAUTIFY_JS = "beautify.js";

    /** Bundled TS Merger script */
    public static final String TSMERGER_JS = "ts-merger.js";

    /** Needed engine name for executing JS */
    public static final String ENGINE_JS = "nashorn";

    /** Export statement regex */
    public static final String EXPORT_REGEX = "export\\s+([^\\s]+)";

    public static final Map<String, Boolean> NOT_EXPORT_TYPES;

    static {
        final Map<String, Boolean> notExportTypes = new HashMap<>();
        notExportTypes.put("class", false);
        notExportTypes.put("interface", false);
        notExportTypes.put("const", false);
        notExportTypes.put("function", false);
        notExportTypes.put("enum", false);
        notExportTypes.put("let", false);
        notExportTypes.put("var", false);
        notExportTypes.put("public", false);
        notExportTypes.put("namespace", false);
        notExportTypes.put("default", false);
        notExportTypes.put("=", false);

        NOT_EXPORT_TYPES = Collections.unmodifiableMap(notExportTypes);
    }

}
