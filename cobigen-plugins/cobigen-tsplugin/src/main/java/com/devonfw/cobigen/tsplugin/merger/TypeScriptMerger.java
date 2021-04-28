package com.devonfw.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.externalprocess.ExternalProcess;
import com.devonfw.cobigen.api.externalprocess.ExternalServerMergerProxy;
import com.devonfw.cobigen.api.externalprocess.to.InputFileTo;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * The {@link TypeScriptMerger} merges a patch and the base file. There will be no merging on statement level.
 */
public class TypeScriptMerger extends ExternalServerMergerProxy {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TypeScriptMerger.class);

    /** OS specific line separator */
    private static final String LINE_SEP = System.getProperty("line.separator");

    /** Merger Type to be registered */
    private String type;

    /** Charset that will be used when sending strings to the server */
    private static final String UTF8 = "UTF-8";

    /**
     * Creates a new {@link TypeScriptMerger}
     *
     * @param externalProcess
     *            the singleton instance of the external process
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public TypeScriptMerger(ExternalProcess externalProcess, String type, boolean patchOverrides) {
        super(externalProcess, patchOverrides);
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {

        String mergedContent = super.merge(base, patch, targetCharset);
        String beautifiedMergedContent = runBeautifierExcludingImports(mergedContent);
        if (beautifiedMergedContent != null) {
            return beautifiedMergedContent;
        } else {
            return mergedContent;
        }
    }

    /**
     * Reads the output.ts temporary file to get the merged contents
     * @param content
     *            The content to be beautified
     * @return merged contents already beautified
     */
    private String runBeautifierExcludingImports(String content) {

        StringBuffer importsAndExports = new StringBuffer();
        StringBuffer body = new StringBuffer();

        try (StringReader isr = new StringReader(content); BufferedReader br = new BufferedReader(isr)) {

            LOG.info("Receiving output from Server....");
            Stream<String> s = br.lines();
            s.parallel().forEachOrdered((String line) -> {
                if (line.startsWith("import ") || isExportStatement(line)) {
                    importsAndExports.append(line);
                    importsAndExports.append(LINE_SEP);
                } else {
                    body.append(line);
                    body.append(LINE_SEP);
                }
            });

            InputFileTo fileTo = new InputFileTo("", body.toString(), UTF8);
            String beautifiedContent = externalProcess.postJsonRequest("beautify", fileTo);

            return importsAndExports + LINE_SEP + LINE_SEP + beautifiedContent;
        } catch (IOException e) {
            LOG.warn("Unable to read service response for beautification", e);
            // beautification anyhow is not critical, let's keep returning what we have
            return null;
        }
    }

    /**
     * Check whether this line is an export statement, taking into account that "export class" is not an
     * export statement.
     * @param line
     *            line to check whether it is an export
     * @return true if it is a real export
     */
    private boolean isExportStatement(String line) {
        if (line.startsWith("export ")) {
            Pattern pattern = Pattern.compile(Constants.EXPORT_REGEX);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() == false) {
                return false;
            }
            String exportType = matcher.group(1).toLowerCase();

            if (Constants.NOT_EXPORT_TYPES.get(exportType) == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
