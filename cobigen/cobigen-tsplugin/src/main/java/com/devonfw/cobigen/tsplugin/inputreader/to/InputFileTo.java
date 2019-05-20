package com.devonfw.cobigen.tsplugin.inputreader.to;

/**
 *
 */
public class InputFileTo {

    private String path;

    private String charset;

    /**
     * @param path
     * @param charset
     */
    public InputFileTo(String path, String charset) {
        this.path = path;
        this.charset = charset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
