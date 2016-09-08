package com.capgemini.cobigen.javaplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.inputreader.ReflectedJavaModelBuilder;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestClass;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestInterfaceInheritance;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;

/**
 * Tests for Class {@link ReflectedJavaModelBuilder}
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
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
    public void testCorrectlyExtractedAttributeTypes() {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(getClass());

        Map<String, Object> pojoMap = JavaModelUtil.getRoot(model);
        Assert.assertNotNull(ModelConstant.ROOT + " is not accessible in model", pojoMap);
        List<Map<String, Object>> attributes = JavaModelUtil.getFields(model);
        Assert.assertNotNull(ModelConstant.FIELDS + " is not accessible in model", attributes);

        Map<String, Object> parametricTestAttributeModel = null;
        for (Map<String, Object> attr : attributes) {
            if ("parametricTestAttribute".equals(attr.get(ModelConstant.NAME))) {
                parametricTestAttributeModel = attr;
                break;
            }
        }

        Assert.assertNotNull("There is no field with name 'parametricTestAttribute' in the model",
            parametricTestAttributeModel);
        // "List<String>" is not possible to retrieve using reflection due to type erasure
        Assert.assertEquals("List<?>", parametricTestAttributeModel.get(ModelConstant.TYPE));
    }

    /**
     * Tests whether supertypes (extended Type and implemented Types) will be extracted correctly to the model
     */
    @Test
    public void testCorrectlyExtractedImplementedTypes() {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestClass.class);

        // check whether implemented Types (interfaces) meet expectations
        List<Map<String, Object>> interfaces = JavaModelUtil.getImplementedTypes(model);

        // interface1
        Assert.assertEquals("TestInterface1", interfaces.get(0).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestInterface1",
            interfaces.get(0).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata",
            interfaces.get(0).get(ModelConstant.PACKAGE));

        // interface2
        Assert.assertEquals("TestInterface2", interfaces.get(1).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestInterface2",
            interfaces.get(1).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata",
            interfaces.get(1).get(ModelConstant.PACKAGE));
    }

    /**
     * Tests whether the inherited type will be correctly extracted and put into the model
     * @author mbrunnli (30.09.2014)
     */
    @Test
    public void testCorrectlyExtractedInheritedType() {
        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestClass.class);

        Assert.assertEquals("AbstractTestClass",
            JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME));
        Assert.assertEquals(
            "com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.AbstractTestClass",
            JavaModelUtil.getExtendedType(model).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata",
            JavaModelUtil.getExtendedType(model).get(ModelConstant.PACKAGE));
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
     * @author mbrunnli (17.11.2014)
     */
    @Test
    public void testExtractionOfMethodAccessibleFields() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(RootClass.class);

        Assert.assertNotNull(JavaModelUtil.getMethodAccessibleFields(model));
        Assert.assertEquals(3, JavaModelUtil.getMethodAccessibleFields(model).size());

        Map<String, Object> setterVisibleByteField =
            JavaModelUtil.getMethodAccessibleField(model, "setterVisibleByte");
        Assert.assertNotNull(setterVisibleByteField);
        Assert.assertEquals("setterVisibleByte", setterVisibleByteField.get(ModelConstant.NAME));
        Assert.assertEquals("byte", setterVisibleByteField.get(ModelConstant.TYPE));
        Assert.assertEquals("byte", setterVisibleByteField.get(ModelConstant.CANONICAL_TYPE));

        Map<String, Object> valueField = JavaModelUtil.getMethodAccessibleField(model, "value");
        Assert.assertNotNull(valueField);
        Assert.assertEquals("value", valueField.get(ModelConstant.NAME));
        Assert.assertEquals("String", valueField.get(ModelConstant.TYPE));
        Assert.assertEquals("java.lang.String", valueField.get(ModelConstant.CANONICAL_TYPE));
    }

    /**
     * Tests whether the input type's fields are extracted correctly (including annotations and javaDoc)
     */
    @Test
    public void testExtractionOfFields() {

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getField(model, "customList");

        assertNotNull(classField);
        assertEquals("customList", classField.get(ModelConstant.NAME));
        assertEquals("List<?>", classField.get(ModelConstant.TYPE));
        assertEquals("java.util.List", classField.get(ModelConstant.CANONICAL_TYPE));
        assertEquals("false", classField.get("isId"));

        // test annotations for attribute, getter, setter, is-method
        assertNotNull(classField.get(ModelConstant.ANNOTATIONS));
        // getter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation"));
        // is-method
        assertTrue(JavaModelUtil.getAnnotations(classField)
            .containsKey("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(classField)
            .containsKey("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation"));
    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     * @author fkreis (27.05.2015)
     */
    @Test
    public void testExtractionOfMethodAccessibleFields_inherited() {

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        // test inherited field
        Map<String, Object> inheritedField = JavaModelUtil.getMethodAccessibleField(model, "id");
        assertNotNull(inheritedField);
        assertEquals("id", inheritedField.get(ModelConstant.NAME));

        assertEquals("Long", inheritedField.get(ModelConstant.TYPE));

        assertEquals("java.lang.Long", inheritedField.get(ModelConstant.CANONICAL_TYPE));

        // is deprecated, so its not necessary to test here
        // assertEquals("false", inheritedField.get("isId"));

        // currently no javadoc provided
        // assertNotNull(inheritedField.get(ModelConstant.JAVADOC));
        // assertEquals("Example JavaDoc", JavaModelUtil.getJavaDocModel(inheritedField).get("comment"));

        // test annotations for attribute, getter, setter, is-method
        assertNotNull(inheritedField.get(ModelConstant.ANNOTATIONS));
        // getter
        assertTrue(JavaModelUtil.getAnnotations(inheritedField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeGetterAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(inheritedField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeSetterAnnotation"));
        // is-method
        assertTrue(JavaModelUtil.getAnnotations(inheritedField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(inheritedField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperTypeFieldAnnotation"));

    }

    /**
     * Tests whether the input type's extracted fields are complete (including annotations and javaDoc)
     * @author fkreis (08.05.2015)
     */
    @Test
    public void testExtractionOfMethodAccessibleFields_local() {

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        // test local field
        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertNotNull(classField);
        assertEquals("customList", classField.get(ModelConstant.NAME));
        assertEquals("List<?>", classField.get(ModelConstant.TYPE));
        assertEquals("java.util.List", classField.get(ModelConstant.CANONICAL_TYPE));

        // currently no javadoc provided
        // assertNotNull(classField.get(ModelConstant.JAVADOC));
        // assertEquals("Example JavaDoc", JavaModelUtil.getJavaDocModel(classField).get("comment"));

        assertEquals("false", classField.get("isId"));

        // test annotations for attribute, getter, setter, is-method
        assertNotNull(classField.get(ModelConstant.ANNOTATIONS));
        // getter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation"));
        // is-method
        assertTrue(JavaModelUtil.getAnnotations(classField)
            .containsKey("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(classField)
            .containsKey("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation"));
    }

    /**
     * Tests whether the input type's annotations are extracted complete
     * @author fkreis (21.06.2015)
     */
    @Test
    public void testAnnotationExtraction() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertNotNull(classField);
        @SuppressWarnings("unchecked")
        Map<String, Object> annotation = (Map<String, Object>) JavaModelUtil.getAnnotations(classField)
            .get("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");
        assertNotNull(annotation);
        // currently all values are provided as Strings
        assertEquals("0", annotation.get("b"));
        assertEquals("1", annotation.get("s"));
        assertEquals("2", annotation.get("i"));
        assertEquals("3", annotation.get("l"));
        assertEquals("4.0", annotation.get("f"));
        assertEquals("5.0", annotation.get("d"));
        assertEquals("c", annotation.get("c"));
        assertEquals("true", annotation.get("bool"));
        assertEquals("TestString", annotation.get("str"));
    }

    /**
     * Tests whether the the input type's annotations are available and whether the annotation parameters have
     * the correct type
     * @author fkreis (24.06.2015)
     */
    @Test
    @Ignore("Currently we provide all parameter values as Strings")
    public void testAnnotationExtraction_Paramtypes() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertNotNull(classField);
        @SuppressWarnings("unchecked")
        Map<String, Object> annotation = (Map<String, Object>) JavaModelUtil.getAnnotations(classField)
            .get("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation");
        assertNotNull(annotation);
        assertTrue(annotation.get("b") instanceof Byte);
        assertEquals((byte) 0, annotation.get("b"));
        assertTrue(annotation.get("s") instanceof Short);
        assertEquals((short) 1, annotation.get("s"));
        assertTrue(annotation.get("i") instanceof Integer);
        assertEquals(2, annotation.get("i"));
        assertTrue(annotation.get("l") instanceof Long);
        assertEquals(3L, annotation.get("l"));
        assertTrue(annotation.get("f") instanceof Float);
        assertEquals(4F, annotation.get("f"));
        assertTrue(annotation.get("d") instanceof Double);
        assertEquals(5D, annotation.get("d"));
        assertTrue(annotation.get("c") instanceof Character);
        assertEquals('c', annotation.get("c"));
        assertTrue(annotation.get("bool") instanceof Boolean);
        assertEquals(true, annotation.get("bool"));
        assertTrue(annotation.get("str") instanceof String);
        assertEquals("TestString", annotation.get("str"));
    }

    /**
     * Tests whether the annotation of super super types are available
     * @author fkreis (21.06.2015)
     */
    @Test
    public void testAnnotationExtractionOfSuperSuperTypes() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "superSuperString");
        assertNotNull(classField);

        // test annotations for attribute, getter, setter, is-method
        assertNotNull(classField.get(ModelConstant.ANNOTATIONS));
        // getter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeGetterAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeSetterAnnotation"));
        // is-method
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeFieldAnnotation"));
        @SuppressWarnings("unchecked")
        Map<String, Object> annotation = (Map<String, Object>) JavaModelUtil.getAnnotations(classField).get(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeFieldAnnotation");
        assertNotNull(annotation);
        // currently all values are provided as Strings
        assertEquals("0", annotation.get("b"));
        assertEquals("1", annotation.get("s"));
        assertEquals("2", annotation.get("i"));
        assertEquals("3", annotation.get("l"));
        assertEquals("4.0", annotation.get("f"));
        assertEquals("5.0", annotation.get("d"));
        assertEquals("c", annotation.get("c"));
        assertEquals("true", annotation.get("bool"));
        assertEquals("TestString", annotation.get("str"));
    }

    /**
     * Tests whether the annotation of super super types are available and whether the annotation parameters
     * have the correct type
     * @author fkreis (24.06.2015)
     */
    @Test
    @Ignore("Currently we provide all parameter values as Strings")
    public void testAnnotationExtractionOfSuperSuperTypes_Paramtypes() {
        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(TestClass.class);

        Map<String, Object> classField = JavaModelUtil.getMethodAccessibleField(model, "superSuperString");
        assertNotNull(classField);

        // test annotations for attribute, getter, setter, is-method
        assertNotNull(classField.get(ModelConstant.ANNOTATIONS));
        // getter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeGetterAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeSetterAnnotation"));
        // is-method
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeFieldAnnotation"));
        @SuppressWarnings("unchecked")
        Map<String, Object> annotation = (Map<String, Object>) JavaModelUtil.getAnnotations(classField).get(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySuperSuperTypeFieldAnnotation");
        assertNotNull(annotation);
        assertTrue(annotation.get("b") instanceof Byte);
        assertEquals((byte) 0, annotation.get("b"));
        assertTrue(annotation.get("s") instanceof Short);
        assertEquals((short) 1, annotation.get("s"));
        assertTrue(annotation.get("i") instanceof Integer);
        assertEquals(2, annotation.get("i"));
        assertTrue(annotation.get("l") instanceof Long);
        assertEquals(3L, annotation.get("l"));
        assertTrue(annotation.get("f") instanceof Float);
        assertEquals(4F, annotation.get("f"));
        assertTrue(annotation.get("d") instanceof Double);
        assertEquals(5D, annotation.get("d"));
        assertTrue(annotation.get("c") instanceof Character);
        assertEquals('c', annotation.get("c"));
        assertTrue(annotation.get("bool") instanceof Boolean);
        assertEquals(true, annotation.get("bool"));
        assertTrue(annotation.get("str") instanceof String);
        assertEquals("TestString", annotation.get("str"));
    }

}
