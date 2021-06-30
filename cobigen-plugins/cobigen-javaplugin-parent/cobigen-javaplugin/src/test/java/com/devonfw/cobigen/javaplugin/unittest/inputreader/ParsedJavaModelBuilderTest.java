package com.devonfw.cobigen.javaplugin.unittest.inputreader;

import static com.devonfw.cobigen.javaplugin.model.JavaModelUtil.getField;
import static com.devonfw.cobigen.javaplugin.model.JavaModelUtil.getJavaDocModel;
import static com.devonfw.cobigen.javaplugin.model.JavaModelUtil.getMethod;
import static com.devonfw.cobigen.javaplugin.model.JavaModelUtil.getRoot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil;
import com.devonfw.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.devonfw.cobigen.javaplugin.model.JavaModelUtil;
import com.devonfw.cobigen.javaplugin.model.ModelConstant;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;
import com.google.common.base.Charsets;

/**
 * Tests for Class {@link ParsedJavaModelBuilderTest}
 */
public class ParsedJavaModelBuilderTest {
    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/inputreader/";

    /**
     * TestAttribute for {@link #testCorrectlyExtractedGenericAttributeTypes()}
     */
    @SuppressWarnings("unused")
    private List<String> parametricTestAttribute;

    /**
     * Tests whether parametric attribute types will be extracted correctly to the model
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedGenericAttributeTypes() throws Exception {

        File file = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));
        Map<String, Object> customList = JavaModelUtil.getField(model, "customList");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        assertThat(customList.get(ModelConstant.TYPE)).isEqualTo("List<String>");
        assertThat(customList.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List<java.lang.String>");
    }

    /**
     * Tests whether super types (extended Type and implemented Types) will be extracted correctly to the
     * model
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedImplementedTypes() throws Exception {

        File classFile = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        // check whether implemented Types (interfaces) meet expectations
        List<Map<String, Object>> interfaces = JavaModelUtil.getImplementedTypes(model);

        // interface1
        assertThat(interfaces.get(0).get(ModelConstant.NAME)).isEqualTo("TestInterface1");
        assertThat(interfaces.get(0).get(ModelConstant.CANONICAL_NAME))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestInterface1");
        assertThat(interfaces.get(0).get(ModelConstant.PACKAGE))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata");

        // interface2
        assertThat(interfaces.get(1).get(ModelConstant.NAME)).isEqualTo("TestInterface2");
        assertThat(interfaces.get(1).get(ModelConstant.CANONICAL_NAME))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestInterface2");
        assertThat(interfaces.get(1).get(ModelConstant.PACKAGE))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata");
    }

    /**
     * Tests whether no {@link NullPointerException} will be thrown if the extended type is in the default
     * package
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedInhertedType_extendedTypeWithoutPackageDeclaration() throws Exception {

        File noPackageFile = new File(testFileRootPath + "NoPackageClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();

        // debug nullPointerException in case of superclass without package
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(noPackageFile)));
        assertThat(JavaModelUtil.getExtendedType(model).get(ModelConstant.PACKAGE)).isEqualTo("");
    }

    /**
     * Tests whether no {@link NullPointerException} will be thrown if an interface taken as an input extends
     * another interface
     * @Issue #250
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyInterpretingInterfaceInheritance() throws Exception {

        File noPackageFile = new File(testFileRootPath + "TestInterface.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(noPackageFile)));
    }

    /**
     * Tests whether the inherited type will be correctly extracted and put into the model
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedInheritedType() throws Exception {
        File classFile = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        Assert.assertEquals("AbstractTestClass", JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME));
        assertThat(JavaModelUtil.getExtendedType(model).get(ModelConstant.CANONICAL_NAME))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.AbstractTestClass");
        assertThat(JavaModelUtil.getExtendedType(model).get(ModelConstant.PACKAGE))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata");
    }

    /**
     * Tests whether the type and the canonical type of a field will be extracted correctly
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyResolvedFieldTypes() throws Exception {

        File file = new File(testFileRootPath + "Pojo.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));
        Map<String, Object> customTypeField = JavaModelUtil.getField(model, "customTypeField");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        assertThat(customTypeField.get(ModelConstant.TYPE)).isEqualTo("AnyOtherType");
        assertThat(customTypeField.get(ModelConstant.CANONICAL_TYPE))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.AnyOtherType");
    }

    /**
     * Tests the correct extraction of 'methodAccessibleFields' for {@link PackageFolder} as input.
     */
    @Test
    @Ignore("see com.devonfw.cobigen.javaplugin.inputreader.ParsedJavaModelBuilder.extractMethodAccessibleFields#L115")
    public void testCorrectExtractionOfInheritedFields_input_packageFolder() {
        File packageFolderFile = new File(testFileRootPath + "packageFolder");
        PackageFolder packageFolder =
            new PackageFolder(packageFolderFile.toURI(), RootClass.class.getPackage().getName());

        JavaInputReader javaInputReader = new JavaInputReader();
        List<Object> objects = javaInputReader.getInputObjects(packageFolder, Charsets.UTF_8);

        assertThat(objects).as("The package folder does not contain any java sources!").isNotNull();
        assertThat(objects).hasSize(2);

        boolean found = false;
        for (Object o : objects) {
            Map<String, Object> model = javaInputReader.createModel(o);
            assertThat(model).as("No model has been created!").isNotNull();
            if (RootClass.class.getSimpleName().equals(JavaModelUtil.getName(model))) {
                List<Map<String, Object>> methodAccessibleFields = JavaModelUtil.getMethodAccessibleFields(model);
                assertThat(methodAccessibleFields).isNotNull();
                assertThat(methodAccessibleFields).hasSize(3);

                Map<String, Object> field = JavaModelUtil.getMethodAccessibleField(model, "value");
                assertThat(field).as("Field 'value' not found!").isNotNull();
                assertThat(field.get(ModelConstant.NAME)).isEqualTo("value");
                assertThat(field.get(ModelConstant.TYPE)).isEqualTo("String");
                assertThat(field.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.lang.String");

                field = JavaModelUtil.getMethodAccessibleField(model, "setterVisibleByte");
                assertThat(field).as("Field 'setterVisibleByte' not found!").isNotNull();
                assertThat(field.get(ModelConstant.NAME)).isEqualTo("setterVisibleByte");
                assertThat(field.get(ModelConstant.TYPE)).isEqualTo("byte");
                assertThat(field.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("byte");

                field = JavaModelUtil.getMethodAccessibleField(model, "genericAccessible");
                assertThat(field).as("Field 'genericAccessible' not found!").isNotNull();
                assertThat(field.get(ModelConstant.NAME)).isEqualTo("genericAccessible");
                // TODO: Known Issue, this is not possible as the SuperClass2 is not in the same folder and
                // thus not parsed. Thus, due to type erasure the parametric type will be lost.
                // assertThat(field.get(ModelConstant.TYPE)).isEqualTo("List<RootClass>");
                // assertThat(field.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List<RootClass>");
                assertThat(field.get(ModelConstant.TYPE)).isEqualTo("List");
                assertThat(field.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List");

                found = true;
            }
        }
        assertTrue("Class " + RootClass.class.getName() + "could not be found as child of the package folder.", found);
    }

    /**
     * Tests the correct extraction of the JavaDoc properties.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectExtractionOfJavaDoc() throws Exception {
        File classFile = new File(testFileRootPath + "DocumentedClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        Map<String, Object> javaDocModel = getJavaDocModel(getRoot(model));
        assertThat(javaDocModel.get(ModelConstant.COMMENT)).isEqualTo("Class Doc.");
        assertThat(javaDocModel.get("author")).isEqualTo("mbrunnli (30.01.2015)");

        javaDocModel = getJavaDocModel(getField(model, "field"));
        assertThat(javaDocModel.get(ModelConstant.COMMENT)).isEqualTo("Field Doc.");

        javaDocModel = getJavaDocModel(getMethod(model, "getField"));
        assertThat(javaDocModel.get(ModelConstant.COMMENT)).isEqualTo("Returns the field 'field'.");
        assertThat(javaDocModel.get("return")).isEqualTo("value of field");
        assertThat(javaDocModel.get("author")).isEqualTo("mbrunnli (30.01.2015)");

        javaDocModel = getJavaDocModel(getMethod(model, "setField"));
        assertThat(javaDocModel.get(ModelConstant.COMMENT)).isEqualTo("Sets the field 'field'.");
        Map<String, Object> params = (Map<String, Object>) javaDocModel.get("params");
        assertThat(params.keySet()).hasSize(4);
        assertThat(params.keySet()).containsExactly("number", "field", "arg1", "arg0");
        assertThat(params.get("field")).isEqualTo("new value of field");
        assertThat(params.get("number")).isEqualTo("just some number");
        assertThat(javaDocModel.get("author")).isEqualTo("mbrunnli (30.01.2015)");

        javaDocModel = getJavaDocModel(getMethod(model, "doSomething"));
        assertThat(javaDocModel.get(ModelConstant.COMMENT)).isEqualTo("Does something");
        Map<String, Object> thrown = (Map<String, Object>) javaDocModel.get("throws");
        assertThat(thrown.keySet()).containsExactly("CobigenRuntimeException", "IOException");
        assertThat(thrown.get("IOException")).isEqualTo("If it would throw one");
        assertThat(thrown.get("CobigenRuntimeException")).isEqualTo("During generation");
        assertThat(javaDocModel.get("author")).isEqualTo("mischuma (04.07.2018)");
    }

    /**
     * Tests whether the input type's fields are extracted correctly (including annotations and javaDoc)
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void testExtractionOfFields() throws Exception {

        File file = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));

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
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");
    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     *
     * @throws Exception
     *             test fails
     */
    @Test
    @Ignore("see com.devonfw.cobigen.javaplugin.inputreader.ParsedJavaModelBuilder.extractMethodAccessibleFields#L115")
    public void testExtractionOfMethodAccessibleFields_inherited() throws Exception {
        File file = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));

        // test inherited field of direct superclass named "id"
        Map<String, Object> inheritedField = JavaModelUtil.getMethodAccessibleField(model, "id");
        assertThat(inheritedField).isNotNull();
        assertThat(inheritedField.get(ModelConstant.NAME)).isEqualTo("id");

        assertThat(inheritedField.get(ModelConstant.TYPE)).isEqualTo("Long");

        assertThat(inheritedField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.lang.Long");

        // is deprecated, so its not necessary to test here
        // assertThat(inheritedField.get("isId")).isEqualTo("false");

        // currently no javadoc provided
        // assertThat(inheritedField.get(ModelConstant.JAVADOC)).isNotNull();
        // assertThat(JavaModelUtil.getJavaDocModel(inheritedField).get("comment")).isEqualTo("Example
        // JavaDoc");

        // TODO Currently qDox library does not return the superclass' annotations
        /*
         * // test annotations for attribute, getter, setter, is-method
         * assertThat(inheritedField.get(ModelConstant.ANNOTATIONS)).isNotNull(); // getter
         * assertTrue(JavaModelUtil.getAnnotations
         * (inheritedField).containsKey("MySuperTypeGetterAnnotation")); // Setter
         * assertTrue(JavaModelUtil.getAnnotations
         * (inheritedField).containsKey("MySuperTypeSetterAnnotation")); // is-method
         * assertThat(JavaModelUtil.getAnnotations(inheritedField).containsKey("MySuperTypeIsAnnotation")).
         * isTrue(); // attribute
         * assertThat(JavaModelUtil.getAnnotations(inheritedField).containsKey("MySuperTypeFieldAnnotation")).
         * isTrue();
         */
    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     *
     * @throws Exception
     *             test fails
     */
    @Test
    @Ignore(value = "This test case is not successfull due to boundaries of qDox")
    public void testExtractionOfMethodAccessibleFields_inheritedInherited() throws Exception {
        File file = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));

        // test inherited field of direct superclass named "id"
        System.out.println(model);
        Map<String, Object> inheritedField = JavaModelUtil.getMethodAccessibleField(model, "superSuperString");
        assertThat(inheritedField).isNotNull();
        assertThat(inheritedField.get(ModelConstant.NAME)).isEqualTo("superSuperString");

        // TODO qDox library returns full qualified names for the superclass' fields
        // actually the expected result of ModelConstant.Type is "String" here, but we insert this test case
        // here with "java.lang.String" so that the test turns into red if there changes anything in qDox
        assertThat(inheritedField.get(ModelConstant.TYPE)).isEqualTo("java.lang.String");

        assertThat(inheritedField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.lang.String");

        // is deprecated, so its not necessary to test here
        // assertThat(inheritedField.get("isId")).isEqualTo("false");

        // currently no javadoc provided
        // assertThat(inheritedField.get(ModelConstant.JAVADOC)).isNotNull();
        // assertThat(JavaModelUtil.getJavaDocModel(inheritedField).get("comment")).isEqualTo("Example
        // JavaDoc");

        // TODO Currently qDox library does not return the superclass' annotations
        /*
         * // test annotations for attribute, getter, setter, is-method
         * assertThat(inheritedField.get(ModelConstant.ANNOTATIONS)).isNotNull(); // getter
         * assertTrue(JavaModelUtil.getAnnotations
         * (inheritedField).containsKey("MySuperSuperTypeGetterAnnotation")); // Setter
         * assertTrue(JavaModelUtil.getAnnotations
         * (inheritedField).containsKey("MySuperSuperTypeSetterAnnotation")); // is-method
         * assertTrue(JavaModelUtil
         * .getAnnotations(inheritedField).containsKey("MySuperSuperTypeIsAnnotation")); // attribute
         * assertTrue
         * (JavaModelUtil.getAnnotations(inheritedField).containsKey("MySuperSuperTypeFieldAnnotation"));
         */
    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     *
     * @throws Exception
     *             test fails
     * @author fkreis (08.05.2015)
     */
    @Test
    public void testExtractionOfMethodAccessibleFields_local() throws Exception {
        File file = new File(testFileRootPath + "TestClass.java");

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));

        // test local field
        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertThat(classField).isNotNull();
        assertThat(classField.get(ModelConstant.NAME)).isEqualTo("customList");
        assertThat(classField.get(ModelConstant.TYPE)).isEqualTo("List<String>");
        assertThat(classField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List<java.lang.String>");

        // currently no javadoc provided
        // assertThat(classField.get(ModelConstant.JAVADOC)).isNotNull();
        // assertThat(JavaModelUtil.getJavaDocModel(classField).get("comment")).isEqualTo("Example JavaDoc");

        assertThat(classField.get("isId")).isEqualTo("false");
        // test annotations for attribute, getter, setter, is-method
        assertThat(classField.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");
    }

}
