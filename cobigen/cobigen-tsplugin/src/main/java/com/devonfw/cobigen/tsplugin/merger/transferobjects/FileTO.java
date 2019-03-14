package com.devonfw.cobigen.tsplugin.merger.transferobjects;

/**
 * Used as a container for the source code text. We need it for sending the content tou our server, so that
 * the code gets beautified
 */
public class FileTO {

    /**
     * Content of the source file
     */
    private String content;

    /**
     * The constructor.
     *
     * @param content
     *            Content of the source file
     */
    public FileTO(String content) {

        super();
        this.content = content;
    }

    /**
     * @return content
     */
    public String getContent() {

        return content;
    }

    /**
     * @param content
     *            new value of {@link #content}.
     */
    public void setContent(String content) {

        this.content = content;
    }

}
