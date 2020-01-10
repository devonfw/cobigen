package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import org.codehaus.jackson.annotate.JsonSubTypes;

@JsonSubTypes({ @JsonSubTypes.Type(value = TestInterface1.class, name = "dog"),
    @JsonSubTypes.Type(value = TestInterface1.class, name = "cat") })
public class TestClassWithRecursiveAnnotations {

}
