package com.devonfw.cobigen.javaplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.junit.Test;

import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil;
import com.devonfw.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestClass;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestClassWithAnnotations;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestClassWithAnnotationsContainingObjectArrays;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestClassWithRecursiveAnnotations;
import com.devonfw.cobigen.javaplugin.model.JavaModelUtil;
import com.devonfw.cobigen.javaplugin.model.ModelConstant;
import com.google.common.collect.Lists;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * This class tests the {@link JavaInputReader}. More specific it should test the model extraction by using
 * reflection and java parsing in combination.
 */
public class JavaInputReaderTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/inputreader/";

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures() throws Exception {

        File javaSourceFile = new File(testFileRootPath + "TestClass.java");
        Class<?> javaClass = TestClass.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        assertThat(model).as("No model has been created!").isNotNull();

        // Check parser feature (resolving of parametric type variables)
        Map<String, Object> fieldAttributes = JavaModelUtil.getField(model, "customList");
        assertThat(fieldAttributes.get(ModelConstant.TYPE)).as("Parametric types are not be resolved correctly!")
            .isEqualTo("List<String>");
    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas one model does not provide any fields and/or methods
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_oneModelEmpty() throws Exception {

        File javaSourceFile = new File(testFileRootPath + "TestClass_empty.java");
        Class<?> javaClass = TestClass.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        assertThat(model).as("No model has been created!").isNotNull();

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and contain boolean values within annotations
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withAnnotations() throws Exception {

        File javaSourceFile = new File(testFileRootPath + "TestClassWithAnnotations.java");
        Class<?> javaClass = TestClassWithAnnotations.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        assertThat(model).as("No model has been created!").isNotNull();

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and contain recursive annotations
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withRecursiveAnnotations() throws Exception {

        File javaSourceFile = new File(testFileRootPath + "TestClassWithRecursiveAnnotations.java");
        Class<?> javaClass = TestClassWithRecursiveAnnotations.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        assertThat(model).as("No model has been created!").isNotNull();

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and an annotation contains an object array as property value
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withAnnotationsContainingObjectArrays() throws Exception {

        File javaSourceFile = new File(testFileRootPath + "TestClassWithAnnotationsContainingObjectArrays.java");
        Class<?> javaClass = TestClassWithAnnotationsContainingObjectArrays.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
            javaInputReader.createModel(new Object[] {
                JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        assertThat(model).as("No model has been created!").isNotNull();

    }

    /**
     * Test method for {@link JavaInputReader#createModel(Object)}. Checks whether the model includes all
     * field information (especially annotations) if two inputs (.java and .class) are used.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testExtractionOfFields() throws Exception {
        // create instance
        JavaInputReader reader = new JavaInputReader();

        // create test data
        File file = new File(testFileRootPath + "TestClass.java");
        JavaClass parsedJavaClass = JavaParserUtil.getFirstJavaClass(new FileReader(file));
        Class<?> reflectedJavaClass = TestClass.class;
        Object[] inputArray = new Object[2];
        inputArray[0] = parsedJavaClass;
        inputArray[1] = reflectedJavaClass;

        Map<String, Object> model = reader.createModel(inputArray);

        // validate
        // test field annotations
        Map<String, Object> classField = JavaModelUtil.getField(model, "customList");
        assertThat(classField).isNotNull();
        assertThat(classField.get(ModelConstant.NAME)).isEqualTo("customList");
        assertThat(classField.get(ModelConstant.TYPE)).isEqualTo("List<String>");
        assertThat(classField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List<java.lang.String>");
        assertThat(classField.get(ModelConstant.JAVADOC)).isNotNull();
        assertThat(JavaModelUtil.getJavaDocModel(classField).get("comment")).isEqualTo("Example JavaDoc");
        assertThat(classField.get("isId")).isEqualTo("false");
        // test annotations for attribute, getter, setter, is-method
        assertThat(classField.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");

        // test local field of method accessible fields
        Map<String, Object> classFieldAccessible = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertThat(classFieldAccessible).isNotNull();
        assertThat(classFieldAccessible.get(ModelConstant.NAME)).isEqualTo("customList");
        assertThat(classFieldAccessible.get(ModelConstant.TYPE)).isEqualTo("List<String>");
        assertThat(classFieldAccessible.get(ModelConstant.CANONICAL_TYPE))
            .isEqualTo("java.util.List<java.lang.String>");

        // currently no javadoc provided
        // assertThat(classField.get(ModelConstant.JAVADOC)).isNotNull();
        // assertThat(JavaModelUtil.getJavaDocModel(classField).get("comment")).isEqualTo("Example JavaDoc");

        assertThat(classFieldAccessible.get("isId")).isEqualTo("false");
        // test annotations for attribute, getter, setter, is-method
        assertThat(classFieldAccessible.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(classFieldAccessible)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(classFieldAccessible)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(classFieldAccessible)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(classFieldAccessible)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");

        // test inherited field of method accessible fields
        Map<String, Object> inheritedField = JavaModelUtil.getMethodAccessibleField(model, "id");
        assertThat(inheritedField).isNotNull();
        assertThat(inheritedField.get(ModelConstant.NAME)).isEqualTo("id");

        // TODO qDox library returns full qualified names for the superclass' fields
        /*
         * assertThat(inheritedField.get(ModelConstant.TYPE)).isEqualTo("Long");
         */
        assertThat(inheritedField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.lang.Long");

        // is deprecated, so its not necessary to test here
        // assertThat(inheritedField.get("isId")).isEqualTo("false");

        // currently no javadoc provided
        // assertThat(inheritedField.get(ModelConstant.JAVADOC)).isNotNull();
        // assertThat(JavaModelUtil.getJavaDocModel(inheritedField).get("comment")).isEqualTo("Example
        // JavaDoc");

        // test annotations for attribute, getter, setter, is-method
        assertThat(inheritedField.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(inheritedField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(inheritedField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeSetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(inheritedField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(inheritedField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeFieldAnnotation");

    }

    /**
     * Test if the method input list returned by
     * {@link JavaInputReader#getInputObjectsRecursively(Object, Charset)} has a proper order to match the
     * expected preview at any time
     */
    @Test
    public void testGetInputObjectsRecursively_resultOrder() {
        File javaSourceFile = new File(testFileRootPath + "packageFolder");
        PackageFolder pkg = new PackageFolder(javaSourceFile.toURI(), "notOfInterest");
        assertThat(pkg).isNotNull();

        JavaInputReader input = new JavaInputReader();

        List<Object> list = input.getInputObjectsRecursively(pkg, Charsets.UTF_8);
        assertThat(list).hasSize(3);

        List<String> simpleNames = Lists.newArrayList();
        for (Object o : list) {
            if (o instanceof JavaClass) {
                simpleNames.add(((JavaClass) o).getName());
            }
        }
        assertThat(simpleNames).containsExactlyInAnyOrder("RootClass", "SuperClass1", "SuperClass2");
    }

}
