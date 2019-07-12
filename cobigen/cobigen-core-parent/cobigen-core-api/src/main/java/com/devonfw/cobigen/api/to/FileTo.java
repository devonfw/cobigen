package com.devonfw.cobigen.api.to;

/**
 * Used as a container for the source code text. We need it for sending the content to our server, so that the
 * code gets beautified
 */
public class FileTo {

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
    public FileTo(String content) {

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
