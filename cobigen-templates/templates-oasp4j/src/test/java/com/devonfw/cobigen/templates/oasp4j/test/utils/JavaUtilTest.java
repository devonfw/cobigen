package com.devonfw.cobigen.templates.oasp4j.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.templates.oasp4j.test.utils.resources.TestClass;
import com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil;

/**
 * Test class for {@link JavaUtil}
 */
public class JavaUtilTest {

    private static Class<?> clazz;

    @BeforeClass
    public static void beforeAll() {

        clazz = new TestClass().getClass();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns true when a primitive is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithPrimitive() {

        assertTrue(new JavaUtil().equalsJavaPrimitive("int"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class&lt;?>,String)} returns true when a primitive is
     * passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWithPrimitive() throws NoSuchFieldException, SecurityException {

        assertTrue(new JavaUtil().equalsJavaPrimitive(clazz, "primitive"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a boxed java primitive class
     * is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithBoxedPrimitive() {

        assertFalse(new JavaUtil().equalsJavaPrimitive("Integer"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class&lt;?>,String)} returns false when a boxed java
     * primitive class is passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWithBoxedPrimitive() throws NoSuchFieldException, SecurityException {

        assertFalse(new JavaUtil().equalsJavaPrimitive(clazz, "boxed"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a java primitive array is
     * passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithPrimitiveArray() {

        assertFalse(new JavaUtil().equalsJavaPrimitive("int[]"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class&lt;?>,String)} returns false when a java primitive
     * array is passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWithPrimitiveArray() throws NoSuchFieldException, SecurityException {

        assertFalse(new JavaUtil().equalsJavaPrimitive(clazz, "primitiveArray"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a non-primitive but existing
     * class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWOPrimitive() {

        assertFalse(new JavaUtil().equalsJavaPrimitive("util.JavaUtilTest"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class,String)} returns false when a non-primitive but
     * existing class is passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWOPrimitive() throws NoSuchFieldException, SecurityException {

        assertFalse(new JavaUtil().equalsJavaPrimitive(clazz, "object"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a non-primitive non-existing
     * class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWOExistingClass() {

        assertFalse(new JavaUtil().equalsJavaPrimitive("DefinitelyNotAClass"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class,String)} throws an exception when a non-existing
     * field is accessed
     */
    @Test(expected = NoSuchFieldException.class)
    public void testPojoEqualsJavaPrimitiveWOExistingClass() throws NoSuchFieldException, SecurityException {

        assertFalse(new JavaUtil().equalsJavaPrimitive(clazz, "definitelyNotAField"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns true for a java primitive
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArrays() {

        assertTrue(new JavaUtil().equalsJavaPrimitiveIncludingArrays("int"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(Class,String)} returns true for a java
     * primitive
     */
    @Test
    public void testPojoEqualsJavaPrimitiveIncludingArrays() throws NoSuchFieldException, SecurityException {

        assertTrue(new JavaUtil().equalsJavaPrimitiveIncludingArrays(clazz, "primitive"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns true for a java primitive
     * array
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArraysWPrimitiveArray() {

        assertTrue(new JavaUtil().equalsJavaPrimitiveIncludingArrays("int[]"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(Class,String)} returns true for a java
     * primitive array
     */
    @Test
    public void testPojoEqualsJavaPrimitiveIncludingArraysWPrimitiveArray()
        throws NoSuchFieldException, SecurityException {

        assertTrue(new JavaUtil().equalsJavaPrimitiveIncludingArrays(clazz, "primitiveArray"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns false for a java
     * non-primitive array
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArraysWArray() {

        assertFalse(new JavaUtil().equalsJavaPrimitiveIncludingArrays("Integer[]"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(Class,String)} returns false for a java
     * non-primitive array
     */
    @Test
    public void testPojoEqualsJavaPrimitiveIncludingArraysWArray() throws NoSuchFieldException, SecurityException {

        assertFalse(new JavaUtil().equalsJavaPrimitiveIncludingArrays(clazz, "objectArray"));
    }

    /**
     * Tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(String)} returns the object wrapper for a java
     * primitive
     */
    @Test
    public void testBoxJavaPrimitive() throws Exception {

        assertEquals("Integer", new JavaUtil().boxJavaPrimitives("int"));
    }

    /**
     * Tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(Class,String)} returns the object wrapper for a java
     * primitive
     */
    @Test
    public void testPojoBoxJavaPrimitive() throws Exception {

        assertEquals("Integer", new JavaUtil().boxJavaPrimitives(clazz, "primitive"));
    }

    /**
     * Tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(String)} returns the input String for a primitive
     * array
     */
    @Test
    public void testBoxJavaPrimitiveWPrimitiveArray() throws Exception {

        assertEquals("int[]", new JavaUtil().boxJavaPrimitives("int[]"));
    }

    /**
     * Tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(Class,String)} returns the input String for a
     * primitive array
     */
    @Test
    public void testPojoBoxJavaPrimitiveWPrimitiveArray() throws Exception {

        assertEquals("int[]", new JavaUtil().boxJavaPrimitives(clazz, "primitiveArray"));
    }

    /**
     * Tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(String)} returns the input String for a non primitive
     */
    @Test
    public void testBoxJavaPrimitiveWOPrimitive() throws Exception {

        assertEquals("Notint", new JavaUtil().boxJavaPrimitives("Notint"));
    }

    /**
     * Tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(Class,String)} returns the simple type name for a non
     * primitive
     */
    @Test
    public void testPojoBoxJavaPrimitiveWOPrimitive() throws Exception {

        assertEquals("String", new JavaUtil().boxJavaPrimitives(clazz, "object"));
    }

    /**
     * tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(String, String)} casts the var name if it is primitive
     */
    @Test
    public void testCastJavaPrimitiveStatement() throws Exception {

        assertEquals("((Integer)var)", new JavaUtil().castJavaPrimitives("int", "var"));
    }

    /**
     * tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(Class,String)} casts the var name if it is primitive
     */
    @Test
    public void testPojoCastJavaPrimitiveStatement() throws Exception {

        assertEquals("((Integer)primitive)", new JavaUtil().castJavaPrimitives(clazz, "primitive"));
    }

    /**
     * tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(String, String)} doesn't cast the var name if it isn't
     * a primitive
     */
    @Test
    public void testCastJavaPrimitiveStatementWOCast() throws Exception {

        assertEquals("", new JavaUtil().castJavaPrimitives("Integer", "var"));
    }

    /**
     * tests if {@link com.devonfw.cobigen.templates.oasp4j.utils.JavaUtil#boxJavaPrimitives(Class,String)} doesn't cast the var name if it isn't a
     * primitive
     */
    @Test
    public void testPojoCastJavaPrimitiveStatementWOCast() throws Exception {

        assertEquals("", new JavaUtil().castJavaPrimitives(clazz, "object"));
    }

    /**
     * tests if the field is {@link java.util.List} or {@link java.util.Set}
     */
    @Test
    public void testIsCollection() throws NoSuchFieldException, SecurityException {

        assertTrue(new JavaUtil().isCollection(clazz, "entitys"));
        assertTrue(new JavaUtil().isCollection(clazz, "setEntitys"));
        assertFalse(new JavaUtil().isCollection(clazz, "boxed"));
    }
}