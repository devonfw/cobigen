package com.capgemini.cobigen.javaplugin.test.inputreader;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.inputreader.ReflectedJavaModelBuilder;
import com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestClass;
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
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedImplementedTypes() throws FileNotFoundException {

        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestClass.class);

        // check whether implemented Types (interfaces) meet expectations
        List<Map<String, Object>> interfaces = JavaModelUtil.getImplementedTypes(model);

        // interface1
        Assert.assertEquals("TestInterface1", interfaces.get(0).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestInterface1",
            interfaces.get(0).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.test.inputreader.testdata", interfaces.get(0)
            .get(ModelConstant.PACKAGE));

        // interface2
        Assert.assertEquals("TestInterface2", interfaces.get(1).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.test.inputreader.testdata.TestInterface2",
            interfaces.get(1).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.test.inputreader.testdata", interfaces.get(1)
            .get(ModelConstant.PACKAGE));
    }

    /**
     * Tests whether the inherited type will be correctly extracted and put into the model
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (30.09.2014)
     */
    @Test
    public void testCorrectlyExtractedInheritedType() throws FileNotFoundException {
        JavaInputReader javaModelBuilder = new JavaInputReader();
        Map<String, Object> model = javaModelBuilder.createModel(TestClass.class);

        Assert
            .assertEquals("AbstractTestClass", JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.test.inputreader.testdata.AbstractTestClass",
            JavaModelUtil.getExtendedType(model).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.test.inputreader.testdata", JavaModelUtil
            .getExtendedType(model).get(ModelConstant.PACKAGE));
    }

}
