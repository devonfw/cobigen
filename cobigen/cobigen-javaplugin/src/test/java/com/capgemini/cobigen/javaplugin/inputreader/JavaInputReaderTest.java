package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.inputreader.testdata.TestClass;
import com.capgemini.cobigen.javaplugin.util.ParserUtil;

/**
 * This class tests the {@link JavaInputReader}. More specific it should test the model extraction by using reflection
 * and java parsing in combination.
 * 
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class JavaInputReaderTest extends AbstractJavaParserTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/JavaInputReaderTests/";

    /**
     * Tests whether both features can be used when providing parsed and reflected inputs for one java class
     * 
     * @throws FileNotFoundException
     *         test fails
     */
    @Test
    public void provideParsingAndReflectionModelFeatures() throws FileNotFoundException {

        File javaSourceFile = new File(testFileRootPath + "TestClass.java");
        Class<?> javaClass = TestClass.class;

        JavaInputReader javaInputReader = new JavaInputReader();
        Map<String, Object> model =
                javaInputReader.createModel(new Object[] { ParserUtil.getJavaClass(new FileReader(javaSourceFile)),
                javaClass });
        Assert.assertNotNull("No model has been created!", model);

        // Check parser feature (resolving of parametric type variables)
        Map<String, Object> fieldAttributes = getField(model, "customList");
        Assert.assertEquals("Parametric types are not be resolved correctly!", "List<String>",
                fieldAttributes.get(ModelConstant.TYPE));

        // Check reflection feature (existence of util for classpath dependent checks)
        Assert.assertNotNull("Reflection Util not attached to model!", model.get(ModelConstant.UTILS));
    }
}
