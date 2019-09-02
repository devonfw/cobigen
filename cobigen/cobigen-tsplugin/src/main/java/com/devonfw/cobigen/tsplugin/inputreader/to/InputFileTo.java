package com.devonfw.cobigen.tsplugin.inputreader.to;

/**
 *
 */
public class InputFileTo {


    private String filename;

    private String charset;
    
    private String content;

    /**
     * @param filename
     * @param content
     * @param charset
     */
    public InputFileTo(String filename, String content, String charset) {
        this.filename = filename;
        this.content = content;
        this.charset = charset;
    }


    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public String getFilename() {
        return this.filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }


    public String getContent() {
        return this.content;
    }


    public void setContent(String content) {
        this.content = content;
    }


}
