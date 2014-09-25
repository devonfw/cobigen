package com.capgemini.cobigen.javaplugin.inputreader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

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
    @SuppressWarnings("unchecked")
    public void testCorrectlyExtractedAttributeTypes() {

        ReflectedJavaModelBuilder javaModelBuilder = new ReflectedJavaModelBuilder();
        Map<String, Object> model = javaModelBuilder.createModel(getClass());

        Map<String, Object> pojoMap = (Map<String, Object>) model.get(ModelConstant.ROOT);
        Assert.assertNotNull(ModelConstant.ROOT + " is not accessible in model", pojoMap);
        List<Map<String, Object>> attributes = (List<Map<String, Object>>) pojoMap.get(ModelConstant.FIELDS);
        Assert.assertNotNull(ModelConstant.FIELDS + " is not accessible in model", attributes);

        Map<String, Object> parametricTestAttribute = null;
        for (Map<String, Object> attr : attributes) {
            if ("parametricTestAttribute".equals(attr.get(ModelConstant.NAME))) {
                parametricTestAttribute = attr;
                break;
            }
        }

        Assert.assertNotNull("There is no field with name 'parametricTestAttribute' in the model",
            parametricTestAttribute);
        // "List<String>" is not possible to retrieve using reflection due to type erasure
        Assert.assertEquals("List<?>", parametricTestAttribute.get(ModelConstant.TYPE));
    }

    /**
     * Tests whether supertypes (extended Type and implemented Types) will be extracted correctly to the model
     *
     */
    @Test
    public void testCorrectlyExtractedSuperTypes() {

        ReflectedJavaModelBuilder javaModelBuilder = new ReflectedJavaModelBuilder();
        Map<String, Object> model = javaModelBuilder.createModel(getClass());

        // check whether extended Type meets expectations
        @SuppressWarnings("unchecked")
        Map<String, Object> pojoMap = (Map<String, Object>) model.get(ModelConstant.ROOT);
        Assert.assertNotNull(ModelConstant.ROOT + " is not accessible in model", pojoMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> supertype = (Map<String, Object>) pojoMap.get(ModelConstant.EXTENDED_TYPE);
        Assert.assertNotNull(ModelConstant.EXTENDED_TYPE + " is not accessible in model", supertype);
        System.out.println(supertype);
        Assert.assertEquals(supertype.get(ModelConstant.NAME), "java.lang.Object");
        Assert.assertEquals(supertype.get(ModelConstant.CANONICAL_NAME), "java.lang.Object");
        Assert.assertEquals(supertype.get(ModelConstant.PACKAGE), "java.lang");

        // check whether implemented Types (interfaces) meet expectations (should be empty list)
        Assert.assertEquals(new LinkedList<Map<String, Object>>(),
            pojoMap.get(ModelConstant.IMPLEMENTED_TYPES));
    }
}
