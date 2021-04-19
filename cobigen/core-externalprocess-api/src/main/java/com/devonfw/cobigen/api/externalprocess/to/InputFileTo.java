package com.devonfw.cobigen.api.externalprocess.to;

/**
 * Used as a container for the source code text. We need it for sending the content to our server, so that the
 * code gets beautified
 */
public class InputFileTo {

    /**
     * Name of the file including its file extension
     */
    private String filename;

    /**
     * Content of the source file
     */
    private String content;

    /**
     * Charset to be used when reading the file
     */
    private String charset;

    /**
     * Constructor of InputFileTo
     * @param filename
     *            name of file including its extension
     * @param content
     *            string content of the file
     * @param charset
     *            which charset needs to be used when reading the file
     */
    public InputFileTo(String filename, String content, String charset) {
        this.filename = filename;
        this.content = content;
        this.charset = charset;
    }

    /**
     * Getter for file name
     * @return file name
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the name of the file
     * @param filename
     *            file name to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Getter for charset
     * @return charset used for reading the file
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Set type of charset
     * @param charset
     *            to be used for reading the file
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Getter for content
     * @return the string content of the file
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *            set file content
     */
    public void setContent(String content) {
        this.content = content;
    }

}
