package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;

/**
 * Tests for Class {@link ParsedJavaModelBuilderTest}
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class ParsedJavaModelBuilderTest {
    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/JavaInputReaderTests/";

    /**
     * TestAttribute for {@link #testCorrectlyExtractedGenericAttributeTypes()}
     */
    @SuppressWarnings("unused")
    private List<String> parametricTestAttribute;

    /**
     * Tests whether parametric attribute types will be extracted correctly to the model
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedGenericAttributeTypes() throws FileNotFoundException {

        File file = new File(testFileRootPath + "TestClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));
        Map<String, Object> customList = JavaModelUtil.getField(model, "customList");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        Assert.assertEquals("List<String>", customList.get(ModelConstant.TYPE));
        Assert.assertEquals("java.util.List<java.lang.String>", customList.get(ModelConstant.CANONICAL_TYPE));
    }

    /**
     * Tests whether supertypes (extended Type and implemented Types) will be extracted correctly to the model
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyExtractedImplementedTypes() throws FileNotFoundException {

        File classFile = new File(testFileRootPath + "TestClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        // check whether implemented Types (interfaces) meet expectations
        List<Map<String, Object>> interfaces = JavaModelUtil.getImplementedTypes(model);

        // interface1
        Assert.assertEquals("TestInterface1", interfaces.get(0).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.testdata.TestInterface1",
            interfaces.get(0).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.testdata",
            interfaces.get(0).get(ModelConstant.PACKAGE));

        // interface2
        Assert.assertEquals("TestInterface2", interfaces.get(1).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.testdata.TestInterface2",
            interfaces.get(1).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.testdata",
            interfaces.get(1).get(ModelConstant.PACKAGE));
    }

    /**
     * Tests whether no {@link NullPointerException} will be thrown if the extended type is in the default
     * package
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (30.09.2014)
     */
    @Test
    public void testCorrectlyExtractedInhertedType_extendedTypeWithoutPackageDeclaration()
        throws FileNotFoundException {

        File noPackageFile = new File(testFileRootPath + "NoPackageClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();

        // debug nullPointerException in case of superclass without package
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(noPackageFile)));
        Assert.assertEquals(JavaModelUtil.getExtendedType(model).get(ModelConstant.PACKAGE), "");
    }

    /**
     * Tests whether the inherited type will be correctly extracted and put into the model
     * @throws FileNotFoundException
     *             test fails
     * @author mbrunnli (30.09.2014)
     */
    @Test
    public void testCorrectlyExtractedInheritedType() throws FileNotFoundException {
        File classFile = new File(testFileRootPath + "TestClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(classFile)));

        Assert
            .assertEquals("AbstractTestClass", JavaModelUtil.getExtendedType(model).get(ModelConstant.NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.testdata.AbstractTestClass",
            JavaModelUtil.getExtendedType(model).get(ModelConstant.CANONICAL_NAME));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.testdata", JavaModelUtil
            .getExtendedType(model).get(ModelConstant.PACKAGE));
    }

    /**
     * Tests whether the type and the canonical type of a field will be extracted correctly
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    public void testCorrectlyResolvedFieldTypes() throws FileNotFoundException {

        File file = new File(testFileRootPath + "Pojo.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(file)));
        Map<String, Object> customTypeField = JavaModelUtil.getField(model, "customTypeField");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        Assert.assertEquals("AnyOtherType", customTypeField.get(ModelConstant.TYPE));
        Assert.assertEquals("com.capgemini.cobigen.javaplugin.inputreader.AnyOtherType",
            customTypeField.get(ModelConstant.CANONICAL_TYPE));
    }

    /**
     * Tests whether inherited fields will also be included in the model
     *
     * @throws FileNotFoundException
     *             test fails
     */
    @Test
    @Ignore("Logic not implemented. Has to be discussed whether this logic is intended as default.")
    public void testModelBuildingWithInheritance() throws FileNotFoundException {

        File subClass = new File(testFileRootPath + "TestClass.java");
        File superClass = new File(testFileRootPath + "AbstractTestClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model =
            javaModelBuilder.createModel(JavaParserUtil.getFirstJavaClass(new FileReader(subClass),
                new FileReader(superClass)));

        Assert.assertEquals(2, JavaModelUtil.getFields(model).size());
        Assert.assertNotNull(JavaModelUtil.getField(model, "id"));
        Assert.assertNotNull(JavaModelUtil.getField(model, "customList"));
    }

}
