package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.util.ModelUtil;
import com.capgemini.cobigen.javaplugin.util.ParserUtil;
import com.thoughtworks.qdox.model.JavaClass;

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
            javaModelBuilder.createModel(ParserUtil.getJavaClass(new FileReader(file)));
        Map<String, Object> customList = ModelUtil.getField(model, "customList");

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
    public void testCorrectlyExtractedSuperTypes() throws FileNotFoundException {

        File classFile = new File(testFileRootPath + "TestClass.java");
        File superClassFile = new File(testFileRootPath + "AbstractTestClass.java");
        File interface1File = new File(testFileRootPath + "TestInterface1.java");
        File interface2File = new File(testFileRootPath + "TestInterface2.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model =
            javaModelBuilder.createModel(ParserUtil.getJavaClass(new FileReader(classFile)));

        // check whether extended Type meets expectations
        Map<String, Object> supermodel = ModelUtil.getExtendedType(model);
        JavaClass superClass = ParserUtil.getJavaClass(new FileReader(superClassFile));

        Assert.assertEquals(supermodel.get(ModelConstant.NAME), superClass.getName());
        Assert.assertEquals(supermodel.get(ModelConstant.CANONICAL_NAME), superClass.getCanonicalName());
        Assert.assertEquals(supermodel.get(ModelConstant.PACKAGE), superClass.getPackage().getName());

        // check whether implemented Types (interfaces) meet expectations
        List<Map<String, Object>> interfaces = ModelUtil.getImplementedTypes(model);
        JavaClass interfaceClass1 = ParserUtil.getJavaClass(new FileReader(interface1File));
        JavaClass interfaceClass2 = ParserUtil.getJavaClass(new FileReader(interface2File));
        System.out.println(interfaces);
        // interface1
        Assert.assertEquals(interfaces.get(0).get(ModelConstant.NAME), interfaceClass1.getName());
        Assert.assertEquals(interfaces.get(0).get(ModelConstant.CANONICAL_NAME),
            interfaceClass1.getCanonicalName());
        Assert.assertEquals(interfaces.get(0).get(ModelConstant.PACKAGE), interfaceClass1.getPackage()
            .getName());
        // interface2
        Assert.assertEquals(interfaces.get(1).get(ModelConstant.NAME), interfaceClass2.getName());
        Assert.assertEquals(interfaces.get(1).get(ModelConstant.CANONICAL_NAME),
            interfaceClass2.getCanonicalName());
        Assert.assertEquals(interfaces.get(1).get(ModelConstant.PACKAGE), interfaceClass2.getPackage()
            .getName());
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
            javaModelBuilder.createModel(ParserUtil.getJavaClass(new FileReader(file)));
        Map<String, Object> customTypeField = ModelUtil.getField(model, "customTypeField");

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
            javaModelBuilder.createModel(ParserUtil.getJavaClass(new FileReader(subClass), new FileReader(
                superClass)));

        Assert.assertEquals(2, ModelUtil.getFields(model).size());
        Assert.assertNotNull(ModelUtil.getField(model, "id"));
        Assert.assertNotNull(ModelUtil.getField(model, "customList"));
    }

}
