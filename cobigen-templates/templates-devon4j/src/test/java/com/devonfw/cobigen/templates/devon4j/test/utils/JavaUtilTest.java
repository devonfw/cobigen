package com.devonfw.cobigen.templates.devon4j.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.test.utils.resources.TestClass;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.TestFirstEntity;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.TestSecondEntity;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.TestTwoClass;
import com.devonfw.cobigen.templates.devon4j.utils.JavaUtil;

/**
 * Test class for {@link JavaUtil}
 */
public class JavaUtilTest {

    private static Class<?> clazz;

    private static Class<?> clazzTwo;

    private static Class<?> firstEntity;

    private static Class<?> secondEntity;

    private static JavaUtil util;

    @BeforeClass
    public static void beforeAll() {

        clazz = new TestClass().getClass();
        clazzTwo = new TestTwoClass().getClass();
        firstEntity = new TestFirstEntity().getClass();
        secondEntity = new TestSecondEntity().getClass();
        util = new JavaUtil();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns true when a primitive is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithPrimitive() {

        assertThat(new JavaUtil().equalsJavaPrimitive("int")).isTrue();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class&lt;?>,String)} returns true when a primitive is
     * passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWithPrimitive() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitive(clazz, "primitive")).isTrue();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a boxed java primitive class
     * is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithBoxedPrimitive() {

        assertThat(new JavaUtil().equalsJavaPrimitive("Integer")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class&lt;?>,String)} returns false when a boxed java
     * primitive class is passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWithBoxedPrimitive() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitive(clazz, "boxed")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a java primitive array is
     * passed
     */
    @Test
    public void testEqualsJavaPrimitiveWithPrimitiveArray() {

        assertThat(new JavaUtil().equalsJavaPrimitive("int[]")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class&lt;?>,String)} returns false when a java primitive
     * array is passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWithPrimitiveArray() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitive(clazz, "primitiveArray")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a non-primitive but existing
     * class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWOPrimitive() {

        assertThat(new JavaUtil().equalsJavaPrimitive("util.JavaUtilTest")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class,String)} returns false when a non-primitive but
     * existing class is passed
     */
    @Test
    public void testPojoEqualsJavaPrimitiveWOPrimitive() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitive(clazz, "object")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(String)} returns false when a non-primitive non-existing
     * class is passed
     */
    @Test
    public void testEqualsJavaPrimitiveWOExistingClass() {

        assertThat(new JavaUtil().equalsJavaPrimitive("DefinitelyNotAClass")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitive(Class,String)} throws an exception when a non-existing
     * field is accessed
     */
    @Test(expected = NoSuchFieldException.class)
    public void testPojoEqualsJavaPrimitiveWOExistingClass() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitive(clazz, "definitelyNotAField")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns true for a java primitive
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArrays() {

        assertThat(new JavaUtil().equalsJavaPrimitiveIncludingArrays("int")).isTrue();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(Class,String)} returns true for a java
     * primitive
     */
    @Test
    public void testPojoEqualsJavaPrimitiveIncludingArrays() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitiveIncludingArrays(clazz, "primitive")).isTrue();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns true for a java primitive
     * array
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArraysWPrimitiveArray() {

        assertThat(new JavaUtil().equalsJavaPrimitiveIncludingArrays("int[]")).isTrue();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(Class,String)} returns true for a java
     * primitive array
     */
    @Test
    public void testPojoEqualsJavaPrimitiveIncludingArraysWPrimitiveArray()
        throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitiveIncludingArrays(clazz, "primitiveArray")).isTrue();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(String)} returns false for a java
     * non-primitive array
     */
    @Test
    public void testEqualsJavaPrimitiveIncludingArraysWArray() {

        assertThat(new JavaUtil().equalsJavaPrimitiveIncludingArrays("Integer[]")).isFalse();
    }

    /**
     * Tests if {@link JavaUtil#equalsJavaPrimitiveIncludingArrays(Class,String)} returns false for a java
     * non-primitive array
     */
    @Test
    public void testPojoEqualsJavaPrimitiveIncludingArraysWArray() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().equalsJavaPrimitiveIncludingArrays(clazz, "objectArray")).isFalse();
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(String)} returns the object wrapper for a java
     * primitive
     */
    @Test
    public void testBoxJavaPrimitive() throws Exception {

        assertThat(new JavaUtil().boxJavaPrimitives("int")).isEqualTo("Integer");
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(Class,String)} returns the object wrapper for a java
     * primitive
     */
    @Test
    public void testPojoBoxJavaPrimitive() throws Exception {

        assertThat(new JavaUtil().boxJavaPrimitives(clazz, "primitive")).isEqualTo("Integer");
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(String)} returns the input String for a primitive
     * array
     */
    @Test
    public void testBoxJavaPrimitiveWPrimitiveArray() throws Exception {

        assertThat(new JavaUtil().boxJavaPrimitives("int[]")).isEqualTo("int[]");
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(Class,String)} returns the input String for a
     * primitive array
     */
    @Test
    public void testPojoBoxJavaPrimitiveWPrimitiveArray() throws Exception {

        assertThat(new JavaUtil().boxJavaPrimitives(clazz, "primitiveArray")).isEqualTo("int[]");
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(String)} returns the input String for a non primitive
     */
    @Test
    public void testBoxJavaPrimitiveWOPrimitive() throws Exception {

        assertThat(new JavaUtil().boxJavaPrimitives("Notint")).isEqualTo("Notint");
    }

    /**
     * Tests if {@link utils.JavaUtil#boxJavaPrimitives(Class,String)} returns the simple type name for a non
     * primitive
     */
    @Test
    public void testPojoBoxJavaPrimitiveWOPrimitive() throws Exception {

        assertThat(new JavaUtil().boxJavaPrimitives(clazz, "object")).isEqualTo("String");
    }

    /**
     * tests if {@link utils.JavaUtil#boxJavaPrimitives(String, String)} casts the var name if it is primitive
     */
    @Test
    public void testCastJavaPrimitiveStatement() throws Exception {

        assertThat(new JavaUtil().castJavaPrimitives("int", "var")).isEqualTo("((Integer)var)");
    }

    /**
     * tests if {@link utils.JavaUtil#boxJavaPrimitives(Class,String)} casts the var name if it is primitive
     */
    @Test
    public void testPojoCastJavaPrimitiveStatement() throws Exception {

        assertThat(new JavaUtil().castJavaPrimitives(clazz, "primitive")).isEqualTo("((Integer)primitive)");
    }

    /**
     * tests if {@link utils.JavaUtil#boxJavaPrimitives(String, String)} doesn't cast the var name if it isn't
     * a primitive
     */
    @Test
    public void testCastJavaPrimitiveStatementWOCast() throws Exception {

        assertThat(new JavaUtil().castJavaPrimitives("Integer", "var")).isEqualTo("");
    }

    /**
     * tests if {@link utils.JavaUtil#boxJavaPrimitives(Class,String)} doesn't cast the var name if it isn't a
     * primitive
     */
    @Test
    public void testPojoCastJavaPrimitiveStatementWOCast() throws Exception {

        assertThat(new JavaUtil().castJavaPrimitives(clazz, "object")).isEqualTo("");
    }

    /**
     * tests if the field is {@link java.util.List} or {@link java.util.Set}
     */
    @Test
    public void testIsCollection() throws NoSuchFieldException, SecurityException {

        assertThat(new JavaUtil().isCollection(clazz, "entitys")).isTrue();
        assertThat(new JavaUtil().isCollection(clazz, "setEntitys")).isTrue();
        assertThat(new JavaUtil().isCollection(clazz, "boxed")).isFalse();
    }

    /**
     * tests if the return type is properly retrieved
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @Test
    public void testGetReturnType() throws NoSuchMethodException, SecurityException {
        for (Method m : clazz.getMethods()) {

            System.out.println(m.getName());
        }
        assertThat(new JavaUtil().getReturnType(clazz, "methodWithReturnType")).isEqualTo("String");
        assertThat(new JavaUtil().getReturnType(clazz, "methodWithVoidReturnType")).isEqualTo("-");
    }

    /**
     * tests if the table name of a given entity is properly returned
     */
    @Test
    public void testGetEntityTableName() {
        assertThat(util.getEntityTableName(firstEntity.getCanonicalName())).isEqualTo("my_table");
        assertThat(util.getEntityTableName(secondEntity.getCanonicalName())).isEqualTo("TestSecond");
        assertThat(util.getEntityTableName(clazz.getCanonicalName())).isNull();
    }

    /**
     * tests if a given field (including field from parent class) is a collection or not
     */
    @Test
    public void testIsCollection2() {
        assertThat(util.isCollection2(firstEntity, "listIntegers")).isTrue();
    }

    /**
     * tests if the canonical name of a given field is return properly
     */
    @Test
    public void testGetCanonicalNameOfField() {
        assertThat(util.getCanonicalNameOfField(firstEntity.getCanonicalName(), "firstName"))
            .isEqualTo("java.lang.String");
        assertThat(util.getCanonicalNameOfField(firstEntity.getCanonicalName(), "id")).isEqualTo("java.lang.Long");
    }

    /**
     * tests if the primary key of a given entity class is properly returned
     */
    @Test
    public void testGetPrimaryKey() {
        assertThat(util.getPrimaryKey(firstEntity.getCanonicalName())).isEqualTo("java.lang.Long,id");
        assertThat(util.getPrimaryKey(secondEntity.getCanonicalName()))
            .isEqualTo("java.lang.String,self_assigned_name");
    }
}