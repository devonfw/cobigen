package com.capgemini.cobigen.javaplugin.unittest.merger.libextension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.exceptions.MergeException;
import com.capgemini.cobigen.javaplugin.merger.libextension.CustomModelWriter;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

/**
 * The class <code>CustomModelWriterTest</code> contains tests for the class {@link CustomModelWriter}
 *
 * @author mbrunnli (12.04.2013)
 *
 */
public class CustomModelWriterTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/libextension/";

    /**
     * Run the ModelWriter writeField(JavaField) method test
     *
     * @throws FileNotFoundException
     *             test fails
     * @throws MergeException
     */
    @Test
    public void testWriteField() throws FileNotFoundException, MergeException {

        File baseFile = new File(testFileRootPath + "ClassFile_field.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaField field =
            JavaParserUtil.getFirstJavaClass(new FileReader(baseFile)).getFieldByName("baseFieldUndefined");
        target.writeField(field);
        Assert.assertEquals("private int baseFieldUndefined;", target.toString().trim());
    }

    /**
     * Tests whether the header will be rewritten after parsing and printing with QDox
     *
     * @throws IOException
     *             test fails
     * @author mbrunnli (12.04.2013)
     * @throws MergeException
     */
    @Test
    @Ignore("Not yet implemented --> QDox ignores comments while parsing")
    public void testExistenceOfHeader() throws IOException, MergeException {

        File file = new File(testFileRootPath + "ClassFile_header.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeSource(parsedClass.getSource());
        Assert.assertTrue(target.toString().contains("/* HEADER */"));
    }

    /**
     * Tests whether generics will be rewritten after parsing and printing with QDox
     *
     * @throws IOException
     *             test fails
     * @author mbrunnli (12.04.2013)
     * @throws MergeException
     */
    @Test
    public void testCorrectlyWrittenGenerics() throws IOException, MergeException {

        File file = new File(testFileRootPath + "ClassFile_generics.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        Assert.assertTrue(target.toString().contains("class Clazz<T extends Object, Z extends Clazz>"));
        Assert.assertTrue(target.toString().contains("Map<String,T>"));
        Assert.assertTrue(target.toString().contains("private T t;"));
        Assert.assertTrue(target.toString().contains("public T get()"));
        Assert.assertTrue(target.toString().contains("public <U extends Number> void inspect(U u)"));
    }

    /**
     * Tests whether modifiers of classes, fields, methods and method parameters are written correctly
     *
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (17.06.2013)
     * @throws MergeException
     */
    @Test
    // @Ignore("Not yet implemented --> QDox defect")
    public void testCorrectlyWrittenModifiers() throws FileNotFoundException, MergeException {

        File file = new File(testFileRootPath + "ClassFile_modifiers.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        Assert.assertTrue(reprintedClass.contains("public final class FooBar"));
        Assert.assertTrue(reprintedClass.contains("private static final volatile int baseFieldUndefined"));
        Assert.assertTrue(reprintedClass.contains("public final void method1(String parameter)"));
        Assert.assertTrue(reprintedClass.contains("method2(final String parameter)"));
    }

    /**
     * Tests whether 'value=' is not written for annotation parameters, whereas value is the only parameter.
     * See https://github.com/devonfw/tools-cobigen/issues/143
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (Jun 26, 2015)
     * @throws MergeException
     */
    @Test
    public void testDoNotWriteDefaultValueIdentifierOfAnnotations()
        throws FileNotFoundException, MergeException {
        File file = new File(testFileRootPath + "ClassFile_annotation_defaultvalue.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        Assert.assertTrue(reprintedClass.contains("@SuppressWarnings(\"unchecked\")"));
    }

    @Test
    public void testCorrectlySetValueAttributOnAnnotationsWithMultipleAttributes()
        throws FileNotFoundException, MergeException {
        File file = new File(testFileRootPath + "ClassFile_annotation_defaultvalue.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        System.out.println(reprintedClass);
        Assert.assertTrue(reprintedClass.contains("@Multipart(value=\"binaryObjectEto\""));
    }
}
