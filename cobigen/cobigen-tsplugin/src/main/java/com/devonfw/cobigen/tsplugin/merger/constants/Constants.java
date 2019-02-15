package com.devonfw.cobigen.tsplugin.merger.constants;

import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;

/** List of constants used on the {@link TypeScriptMerger} */
public class Constants {

    /** Bundled JS Beautifier script */
    public static final String BEAUTIFY_JS = "beautify.js";

    /** Bundled TS Merger script */
    public static final String TSMERGER_JS = "ts-merger.js";

    /** Needed engine name for executing JS */
    public static final String ENGINE_JS = "Graal.js";

    /** Export statement regex */
    public static final String EXPORT_REGEX = "export\\s+([^\\s]+)";

}
