package utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test class for {@link JavaUtil}
 */
public class JavaUtilTest{

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns true when a primitive is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithPrimitive(){
        assertTrue(new JavaUtil().equalsJavaPrimitive("int"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a boxed java primitive class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithBoxedPrimitive(){
        assertFalse(new JavaUtil().equalsJavaPrimitive("Integer"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a java primitive array is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithPrimitiveArray(){
        assertFalse(new JavaUtil().equalsJavaPrimitive("int[]"));
    }


    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a non-primitive but existing class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWOPrimitive(){
        assertFalse(new JavaUtil().equalsJavaPrimitive("util.JavaUtilTest"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a non-primitive non-existing class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWOExistingClass(){
        assertFalse(new JavaUtil().equalsJavaPrimitive("DefinitelyNotAClass"));
    }


    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns true for a java primitive
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArrays(){
        assertTrue(new JavaUtil().equalsJavaPrimitiveIncludingArrays("int"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns true for a java primitive array
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArraysWPrimitiveArray(){
        assertTrue(new JavaUtil().equalsJavaPrimitiveIncludingArrays("int[]"));
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns false for a java non-primitive array
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArraysWArray(){
        assertFalse(new JavaUtil().equalsJavaPrimitiveIncludingArrays("Integer[]"));
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(String)} returns the object wrapper for a java primitive
     */
    @Test
    public void testBoxJavaPrimitive() throws Exception{
        assertEquals("Integer", new JavaUtil().boxJavaPrimitives("int"));
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(String)} returns the input String for a primitive array
     */
    @Test
    public void testBoxJavaPrimitiveWPrimitiveArray() throws Exception{
        assertEquals("int[]", new JavaUtil().boxJavaPrimitives("int[]"));
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(String)} returns the input String for a non primitive
     */
    @Test
    public void testBoxJavaPrimitiveWOPrimitive() throws Exception{
        assertEquals("Notint", new JavaUtil().boxJavaPrimitives("Notint"));
    }

    /**
     * tests if {@link utils.JavaUtil#boxJavaPrimitives(String, String)} casts the var name if it is primitive
     */
    @Test
    public void testBoxJavaPrimitiveStatement() throws Exception{
        assertEquals("((Integer)var)", new JavaUtil().boxJavaPrimitives("int", "var"));
    }

    /**
     * tests if {@link utils.JavaUtil#boxJavaPrimitives(String, String)} doesn't cast the var name if it isn't a primitive
     */
    @Test
    public void testBoxJavaPrimitiveStatementWOCast() throws Exception{
        assertEquals("", new JavaUtil().boxJavaPrimitives("Integer", "var"));
    }

}