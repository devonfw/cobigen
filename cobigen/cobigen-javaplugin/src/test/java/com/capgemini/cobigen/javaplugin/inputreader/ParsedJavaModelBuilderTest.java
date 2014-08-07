package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.util.ParserUtil;

/**
 * Tests for Class {@link ParsedJavaModelBuilderTest}
 * 
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class ParsedJavaModelBuilderTest extends AbstractJavaParserTest {
    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/JavaInputReaderTests/";

    /**
     * TestAttribute for {@link #testCorrectlyExtractedAttributeTypes()}
     */
    @SuppressWarnings("unused")
    private List<String> parametricTestAttribute;

    /**
     * Tests whether parametric attribute types will be extracted correctly to the model
     * 
     * @throws FileNotFoundException
     *         test fails
     */
    @Test
    public void testCorrectlyExtractedAttributeTypes() throws FileNotFoundException {

        File file = new File(testFileRootPath + "TestClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model = javaModelBuilder.createModel(ParserUtil.getJavaClass(new FileReader(file)));
        Map<String, Object> customList = getField(model, "customList");

        // "List<String>" is not possible to retrieve using reflection due to type erasure
        Assert.assertEquals("List<String>", customList.get(ModelConstant.TYPE));
    }

    /**
     * Tests whether inherited fields will also be included in the model
     * 
     * @throws FileNotFoundException
     *         test fails
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

        Assert.assertEquals(2, getFields(model).size());
        Assert.assertNotNull(getField(model, "id"));
        Assert.assertNotNull(getField(model, "customList"));
    }

}
