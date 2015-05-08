package com.capgemini.cobigen.javaplugin.unittest.inputreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.inputreader.ReflectedJavaModelBuilder;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestClass;
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

        Assert
            .assertEquals("AbstractTestClass", JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME));
        Assert.assertEquals(
            "com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.AbstractTestClass", JavaModelUtil
                .getExtendedType(model).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata", JavaModelUtil
            .getExtendedType(model).get(ModelConstant.PACKAGE));
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
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testExtractionOfFields() throws FileNotFoundException {

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
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(classField).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation"));
    }
}
