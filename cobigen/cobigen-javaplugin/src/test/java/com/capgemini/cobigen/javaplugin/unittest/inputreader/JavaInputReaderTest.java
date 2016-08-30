package com.capgemini.cobigen.javaplugin.unittest.inputreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestClass;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestClassWithAnnotations;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestClassWithAnnotationsContainingObjectArrays;
import com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata.TestClassWithRecursiveAnnotations;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.capgemini.cobigen.javaplugin.util.freemarkerutil.IsAbstractMethod;
import com.capgemini.cobigen.javaplugin.util.freemarkerutil.IsSubtypeOfMethod;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * This class tests the {@link JavaInputReader}. More specific it should test the model extraction by using
 * reflection and java parsing in combination.
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
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
        Map<String, Object> model = javaInputReader.createModel(
            new Object[] { JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

        // Check parser feature (resolving of parametric type variables)
        Map<String, Object> fieldAttributes = JavaModelUtil.getField(model, "customList");
        Assert.assertEquals("Parametric types are not be resolved correctly!", "List<String>",
            fieldAttributes.get(ModelConstant.TYPE));
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
        Map<String, Object> model = javaInputReader.createModel(
            new Object[] { JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

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
        Map<String, Object> model = javaInputReader.createModel(
            new Object[] { JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

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
        Map<String, Object> model = javaInputReader.createModel(
            new Object[] { JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

    }

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class,
     * whereas the models are equal and an annotation contains an object array as property value
     *
     * @throws Exception
     *             test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures_withAnnotationsContainingObjectArrays()
        throws Exception {

        File javaSourceFile =
            new File(testFileRootPath + "TestClassWithAnnotationsContainingObjectArrays.java");
        Class<?> javaClass = TestClassWithAnnotationsContainingObjectArrays.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model = javaInputReader.createModel(
            new Object[] { JavaParserUtil.getFirstJavaClass(new FileReader(javaSourceFile)), javaClass });
        Assert.assertNotNull("No model has been created!", model);

    }

    /**
     * Test method for {@link JavaInputReader#getTemplateMethods(Object)}. Checks whether the returning Map
     * contains isSubtypeOf and isAstract as template methods.
     */
    @Test
    public void testgetTemplateMethods() {
        // create instance
        JavaInputReader reader = new JavaInputReader();

        // create test data
        Map<String, Object> methods = reader.getTemplateMethods(getClass());
        Set<String> keys = methods.keySet();

        // validate
        Assert.assertTrue(keys.contains("isSubtypeOf"));
        Assert.assertTrue(keys.contains("isAbstract"));
        Assert.assertTrue(methods.get("isSubtypeOf") instanceof IsSubtypeOfMethod);
        Assert.assertTrue(methods.get("isAbstract") instanceof IsAbstractMethod);
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
        assertNotNull(classField);
        assertEquals("customList", classField.get(ModelConstant.NAME));
        assertEquals("List<String>", classField.get(ModelConstant.TYPE));
        assertEquals("java.util.List<java.lang.String>", classField.get(ModelConstant.CANONICAL_TYPE));
        assertNotNull(classField.get(ModelConstant.JAVADOC));
        assertEquals("Example JavaDoc", JavaModelUtil.getJavaDocModel(classField).get("comment"));
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

        // test local field of method accessible fields
        Map<String, Object> classFieldAccessible =
            JavaModelUtil.getMethodAccessibleField(model, "customList");
        assertNotNull(classFieldAccessible);
        assertEquals("customList", classFieldAccessible.get(ModelConstant.NAME));
        assertEquals("List<String>", classFieldAccessible.get(ModelConstant.TYPE));
        assertEquals("java.util.List<java.lang.String>",
            classFieldAccessible.get(ModelConstant.CANONICAL_TYPE));

        // currently no javadoc provided
        // assertNotNull(classField.get(ModelConstant.JAVADOC));
        // assertEquals("Example JavaDoc", JavaModelUtil.getJavaDocModel(classField).get("comment"));

        assertEquals("false", classFieldAccessible.get("isId"));
        // test annotations for attribute, getter, setter, is-method
        assertNotNull(classFieldAccessible.get(ModelConstant.ANNOTATIONS));
        // getter
        assertTrue(JavaModelUtil.getAnnotations(classFieldAccessible).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyGetterAnnotation"));
        // Setter
        assertTrue(JavaModelUtil.getAnnotations(classFieldAccessible).containsKey(
            "com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MySetterAnnotation"));
        // is-method
        assertTrue(JavaModelUtil.getAnnotations(classFieldAccessible)
            .containsKey("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyIsAnnotation"));
        // attribute
        assertTrue(JavaModelUtil.getAnnotations(classFieldAccessible)
            .containsKey("com_capgemini_cobigen_javaplugin_unittest_inputreader_testdata_MyFieldAnnotation"));

        // test inherited field of method accessible fields
        Map<String, Object> inheritedField = JavaModelUtil.getMethodAccessibleField(model, "id");
        assertNotNull(inheritedField);
        assertEquals("id", inheritedField.get(ModelConstant.NAME));

        // TODO qDox library returns full qualified names for the superclass' fields
        /*
         * assertEquals("Long", inheritedField.get(ModelConstant.TYPE));
         */
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

}
