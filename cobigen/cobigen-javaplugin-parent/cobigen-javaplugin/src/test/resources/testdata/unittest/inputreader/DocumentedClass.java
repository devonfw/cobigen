package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

/**
 * Class Doc.
 * @author mbrunnli (30.01.2015)
 */
public class DocumentedClass {

    /**
     * Field Doc.
     */
    private String field;

    /**
     * Returns the field 'field'.
     * @return value of field
     * @author mbrunnli (30.01.2015)
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the field 'field'.
     * @param field
     *            new value of field
     * @param number 
     *            just some number
     * @author mbrunnli (30.01.2015)
     */
    public void setField(String field,int number) {
        this.field = field;
    }

    /**
     * Does something
     * @author mischuma (04.07.2018)
     * @throws IOException If it would throw one
     * @throws CobigenRuntimeException During generation
     */
    public void doSomething() throws IOException, CobigenRuntimeException{
        
    }
}
