package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

@MyMultiAnnotation({ @MyMultiAnnotation.Type(value = TestInterface1.class, name = "dog"),
@MyMultiAnnotation.Type(value = TestInterface1.class, name = "cat") })
public class TestClassWithRecursiveAnnotations {

}
