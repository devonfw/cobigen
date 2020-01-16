package com.devonfw.cobigen.javaplugin.unittest.merger.libextension;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil;
import com.devonfw.cobigen.javaplugin.merger.libextension.CustomModelWriter;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

/**
 * The class <code>CustomModelWriterTest</code> contains tests for the class {@link CustomModelWriter}
 */
public class CustomModelWriterTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/libextension/";

    /**
     * Run the ModelWriter writeField(JavaField) method test
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testWriteField() throws Exception {

        File baseFile = new File(testFileRootPath + "ClassFile_field.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaField field =
            JavaParserUtil.getFirstJavaClass(new FileReader(baseFile)).getFieldByName("baseFieldUndefined");
        target.writeField(field);
        assertThat(target.toString().trim()).isEqualTo("private int baseFieldUndefined;");
    }

    /**
     * Tests whether the header will be rewritten after parsing and printing with QDox
     *
     * @throws Exception
     *             test fails
     */
    @Test
    @Ignore("Not yet implemented --> QDox ignores comments while parsing")
    public void testExistenceOfHeader() throws Exception {

        File file = new File(testFileRootPath + "ClassFile_header.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeSource(parsedClass.getSource());
        assertThat(target.toString()).contains("/* HEADER */");
    }

    /**
     * Tests whether generics will be rewritten after parsing and printing with QDox
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyWrittenGenerics() throws Exception {

        File file = new File(testFileRootPath + "ClassFile_generics.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        assertThat(target.toString()).contains("class Clazz<T extends Object, Z extends Clazz>");
        assertThat(target.toString()).contains("Map<String,T>");
        assertThat(target.toString()).contains("private T t;");
        assertThat(target.toString()).contains("public T get()");
        assertThat(target.toString()).contains("public <U extends Number> void inspect(U u)");
    }

    /**
     * Tests whether modifiers of classes, fields, methods and method parameters are written correctly
     *
     * @throws Exception
     *             test fails
     */
    @Test
    // @Ignore("Not yet implemented --> QDox defect")
    public void testCorrectlyWrittenModifiers() throws Exception {

        File file = new File(testFileRootPath + "ClassFile_modifiers.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        assertThat(reprintedClass).contains("public final class FooBar");
        assertThat(reprintedClass).contains("private static final volatile int baseFieldUndefined");
        assertThat(reprintedClass).contains("public final void method1(String parameter)");
        assertThat(reprintedClass).contains("method2(final String parameter)");
    }

    /**
     * Tests whether 'value=' is not written for annotation parameters, whereas value is the only parameter.
     * See https://github.com/devonfw/tools-cobigen/issues/143
     * @throws Exception
     *             test fails
     */
    @Test
    public void testDoNotWriteDefaultValueIdentifierOfAnnotations() throws Exception {
        File file = new File(testFileRootPath + "ClassFile_annotation_defaultvalue.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        assertThat(reprintedClass).contains("@SuppressWarnings(\"unchecked\")");
    }

    /**
     * Bugfix #143: ignore annotation variable printing if annotation just declare one default value.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlySetValueAttributOnAnnotationsWithMultipleAttributes() throws Exception {
        File file = new File(testFileRootPath + "ClassFile_annotation_defaultvalue.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        assertThat(reprintedClass).contains("@Multipart(value=\"binaryObjectEto\"");
    }

    /**
     * Tests the output of the CustomModelWriter with respect to the syntax of the array notation for
     * annotation parameter values with Annotation[].
     *
     * See https://github.com/devonfw/tools-cobigen/issues/290
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectSyntaxOutputForArrays() throws Exception {
        File file = new File(testFileRootPath + "ArraySyntax.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        Pattern p = Pattern.compile("@[A-Za-z]+\\(\\{.+\\{.+\\s*.+\\}.+\\{.+\\}.+\\}\\)\\s*public.+", Pattern.DOTALL);
        assertThat(reprintedClass).matches(p);
    }

    /**
     * Tests the output of the CustomModelWriter with respect to the syntax of the array notation for
     * annotation parameter values with only one Annotation.
     *
     * See https://github.com/devonfw/tools-cobigen/issues/290
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectSyntaxOutputForArrayswithOnlyOneAnnotation() throws Exception {
        File file = new File(testFileRootPath + "ArraySyntaxOnlyOneAnnotation.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        Pattern p = Pattern.compile("@[A-Za-z]+\\(.*\\{.*\\}.*\\).*\\s*public.+", Pattern.DOTALL);
        assertThat(reprintedClass).matches(p);
    }

    /**
     * Tests the output of the CustomModelWriter regarding the full qualified names of the nested annotations
     * used in it.
     *
     * See https://github.com/devonfw/tools-cobigen/issues/291
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectNameOutputForNestedAnnotations() throws Exception {
        File file = new File(testFileRootPath + "ArraySyntax.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        assertThat(reprintedClass).doesNotContain("javax.persistence.NamedEntityGraphs");
    }

    /**
     * Tests the output of the CustomModelWriter regarding an own Annotation containing two Strings.
     *
     * See https://github.com/devonfw/tools-cobigen/issues/290
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectNameOutputForOwnAnnotation() throws Exception {
        File file = new File(testFileRootPath + "OwnAnnotation.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        assertThat(reprintedClass).contains("{\"abc\", \"cde\"}");
    }

    /**
     * Tests the output of the CustomModelWriter regarding an own Annotation with multiple grouped annotations
     * and the focus on correct comma placement.
     *
     * See https://github.com/devonfw/cobigen/issues/1070
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectSyntaxOutputForArraysCommaPlacement() throws Exception {
        File file = new File(testFileRootPath + "ArraySyntax.java");
        CustomModelWriter target = new CustomModelWriter();
        JavaClass parsedClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        target.writeClass(parsedClass);

        String reprintedClass = target.toString();
        Pattern p1 = Pattern.compile(
            "@[A-Za-z]+\\(\\{.+\\{.+\\s*.+\\}.+\\{.+((\\(.+\\(.+\\).+\\)\\,.+)|(\\(.+\\(.+\\).+\\).+\\,.+))+(\\(.+\\(.+\\).+\\)[^\\,]+){1,1}\\}\\).+\\}\\)\\s*public.+",
            Pattern.DOTALL);
        assertThat(reprintedClass).matches(p1);
    }
}
