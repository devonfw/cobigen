package com.devonfw.cobigen.javaplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.javaplugin.inputreader.ReflectedJavaModelBuilder;
import com.devonfw.cobigen.javaplugin.model.JavaModelUtil;
import com.devonfw.cobigen.javaplugin.model.ModelConstant;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestClass;
import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.TestInterfaceInheritance;

/**
 * Tests for Class {@link ReflectedJavaModelBuilder}
 */
public class ReflectedJavaModelBuilderTest {

    /**
     * TestAttribute for {@link #testCorrectlyExtractedAttributeTypes()}
     */
    @SuppressWarnings("unused")
    private List<String> parametricTestAttribute;

    /**
     * Tests whether parametric attribute types will be extracted correctly to the model
     */
    @Test
    @SuppressWarnings("null")
    public void testCorrectlyExtractedAttributeTypes() {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(getClass());

        Map<String, Object> pojoMap = JavaModelUtil.getRoot(model);
        assertThat(pojoMap).as(ModelConstant.MODEL_ROOT + " is not accessible in model").isNotNull();
        List<Map<String, Object>> attributes = JavaModelUtil.getFields(model);
        assertThat(attributes).as(ModelConstant.FIELDS + " is not accessible in model").isNotNull();

        Map<String, Object> parametricTestAttributeModel = null;
        for (Map<String, Object> attr : attributes) {
            if ("parametricTestAttribute".equals(attr.get(ModelConstant.NAME))) {
                parametricTestAttributeModel = attr;
                break;
            }
        }

        assertThat(parametricTestAttributeModel)
            .as("There is no field with name 'parametricTestAttribute' in the model").isNotNull();
        // "List<String>" is not possible to retrieve using reflection due to type erasure
        assertThat(parametricTestAttributeModel.get(ModelConstant.TYPE)).isEqualTo("List<?>");
    }

    /**
     * Tests whether super types (extended Type and implemented Types) will be extracted correctly to the
     * model
     */
    @Test
    public void testCorrectlyExtractedImplementedTypes() {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestClass.class);

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
     * Tests whether the inherited type will be correctly extracted and put into the model
     */
    @Test
    public void testCorrectlyExtractedInheritedType() {
        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestClass.class);

        assertThat(JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME)).isEqualTo("AbstractTestClass");
        assertThat(JavaModelUtil.getExtendedType(model).get(ModelConstant.CANONICAL_NAME))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.AbstractTestClass");
        assertThat(JavaModelUtil.getExtendedType(model).get(ModelConstant.PACKAGE))
            .isEqualTo("com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata");
    }

    /**
     * Tests whether no {@link NullPointerException} will be thrown if an interface taken as an input extends
     * another interface. Test for issue #250.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyInterpretingInterfaceInheritance() throws Exception {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        javaModelBuilder.createModel(TestInterfaceInheritance.class);
    }

    /**
     * Tests whether inherited methods will be resolved as well.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testCorrectlyExtractingMethodsInInterfaceInheritance() throws Exception {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestInterfaceInheritance.class);

        assertThat(JavaModelUtil.getMethods(model)).hasSize(2);
    }

    /**
     * Tests the inclusion of all fields accessible by setter and getter methods into the model. This also
     * includes inherited accessible fields.
     */
    @Test
    public void testExtractionOfMethodAccessibleFields() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(RootClass.class);

        assertThat(JavaModelUtil.getMethodAccessibleFields(model)).isNotNull();
        assertThat(JavaModelUtil.getMethodAccessibleFields(model)).hasSize(3);

        Map<String, Object> setterVisibleByteField = JavaModelUtil.getMethodAccessibleField(model, "setterVisibleByte");
        assertThat(setterVisibleByteField).isNotNull();
        assertThat(setterVisibleByteField.get(ModelConstant.NAME)).isEqualTo("setterVisibleByte");
        assertThat(setterVisibleByteField.get(ModelConstant.TYPE)).isEqualTo("byte");
        assertThat(setterVisibleByteField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("byte");

        Map<String, Object> valueField = JavaModelUtil.getMethodAccessibleField(model, "value");
        assertThat(valueField).isNotNull();
        assertThat(valueField.get(ModelConstant.NAME)).isEqualTo("value");
        assertThat(valueField.get(ModelConstant.TYPE)).isEqualTo("String");
        assertThat(valueField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.lang.String");
    }

    /**
     * Tests whether the input type's fields are extracted correctly (including annotations and javaDoc)
     */
    @Test
    public void testExtractionOfFields() {

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getField(model, "customList");

        assertThat(classField).isNotNull();
        assertThat(classField.get(ModelConstant.NAME)).isEqualTo("customList");
        assertThat(classField.get(ModelConstant.TYPE)).isEqualTo("List<?>");
        assertThat(classField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List");
        assertThat(classField.get("isId")).isEqualTo("false");

        // test annotations for attribute, getter, setter, is-method
        assertThat(classField.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation");
    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     */
    @Test
    public void testExtractionOfMethodAccessibleFields_inherited() {

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        // test inherited field
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

        // test annotations for attribute, getter, setter, is-method
        assertThat(inheritedField.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(inheritedField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(inheritedField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeSetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(inheritedField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(inheritedField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeFieldAnnotation");

    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     */
    @Test
    public void testExtractionOfMethodAccessibleFields_local() {

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        // test local field
        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertThat(classField).isNotNull();
        assertThat(classField.get(ModelConstant.NAME)).isEqualTo("customList");
        assertThat(classField.get(ModelConstant.TYPE)).isEqualTo("List<?>");
        assertThat(classField.get(ModelConstant.CANONICAL_TYPE)).isEqualTo("java.util.List");

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

    /**
     * Tests whether the input type's annotations are extracted complete
     */
    @Test
    public void testAnnotationExtraction() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertThat(classField).isNotNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> annotation = (Map<String, Object>) JavaModelUtil.getAnnotations(classField)
            .get("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");
        assertThat(annotation).isNotNull();
        // currently all values are provided as Strings
        assertThat(annotation.get("b")).isEqualTo("0");
        assertThat(annotation.get("s")).isEqualTo("1");
        assertThat(annotation.get("i")).isEqualTo("2");
        assertThat(annotation.get("l")).isEqualTo("3");
        assertThat(annotation.get("f")).isEqualTo("4.0");
        assertThat(annotation.get("d")).isEqualTo("5.0");
        assertThat(annotation.get("c")).isEqualTo("c");
        assertThat(annotation.get("bool")).isEqualTo("true");
        assertThat(annotation.get("str")).isEqualTo("TestString");
    }

    /**
     * Tests whether the annotation of super super types are available
     */
    @Test
    public void testAnnotationExtractionOfSuperSuperTypes() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "superSuperString");
        assertThat(classField).isNotNull();

        // test annotations for attribute, getter, setter, is-method
        assertThat(classField.get(ModelConstant.ANNOTATIONS)).isNotNull();
        // getter
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeGetterAnnotation");
        // Setter
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeSetterAnnotation");
        // is-method
        assertThat(JavaModelUtil.getAnnotations(classField))
            .containsKey("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeIsAnnotation");
        // attribute
        assertThat(JavaModelUtil.getAnnotations(classField)).containsKey(
            "com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeFieldAnnotation");
        @SuppressWarnings("unchecked")
        Map<String, Object> annotation = (Map<String, Object>) JavaModelUtil.getAnnotations(classField)
            .get("com_devonfw_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeFieldAnnotation");

        assertThat(annotation).isNotNull();
        // currently all values are provided as Strings
        assertThat(annotation.get("b")).isEqualTo("0");
        assertThat(annotation.get("s")).isEqualTo("1");
        assertThat(annotation.get("i")).isEqualTo("2");
        assertThat(annotation.get("l")).isEqualTo("3");
        assertThat(annotation.get("f")).isEqualTo("4.0");
        assertThat(annotation.get("d")).isEqualTo("5.0");
        assertThat(annotation.get("c")).isEqualTo("c");
        assertThat(annotation.get("bool")).isEqualTo("true");
        assertThat(annotation.get("str")).isEqualTo("TestString");
    }

}
