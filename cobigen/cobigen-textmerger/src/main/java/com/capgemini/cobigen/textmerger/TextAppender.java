package com.capgemini.cobigen.textmerger;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.capgemini.cobigen.extension.IMerger;

/**
 * The {@link TextAppender} allows appending the patch to the base file
 * @author mbrunnli (03.06.2014)
 */
public class TextAppender implements IMerger {

    /**
     * Type (or name) of the instance
     */
    private String type;

    /**
     * States whether the patch should be appended after adding a new line to the base file
     */
    private boolean withNewLineBeforehand;

    /**
     * Creates a new text appender which appends the patch to the base file. If {@link #withNewLineBeforehand}
     * is set, the patch will be appended by first adding a new line to the base file
     * @param type
     *            of the text appender instance
     * @param withNewLineBeforehand
     *            if <code>true</code> a new line will be inserted before each appended text iff the appended
     *            text is not empty<br>
     *            <code>false</code>, otherwise.
     * @author mbrunnli (03.06.2014)
     */
    public TextAppender(String type, boolean withNewLineBeforehand) {
        this.type = type;
        this.withNewLineBeforehand = withNewLineBeforehand;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws Exception {
        String mergedString = FileUtils.readFileToString(base, targetCharset);
        if (withNewLineBeforehand && StringUtils.isNotEmpty(patch)) {
            mergedString += System.lineSeparator();
        }
        mergedString += patch;
        return mergedString;
    }
}
