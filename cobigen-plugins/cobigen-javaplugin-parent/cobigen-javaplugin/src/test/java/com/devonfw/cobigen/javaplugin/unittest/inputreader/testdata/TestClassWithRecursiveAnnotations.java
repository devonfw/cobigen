package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({ @JsonSubTypes.Type(value = TestInterface1.class, name = "dog"),
    @JsonSubTypes.Type(value = TestInterface1.class, name = "cat") })
public class TestClassWithRecursiveAnnotations {

}
