package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

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
    @SuppressWarnings("unchecked")
    public void testCorrectlyExtractedAttributeTypes() throws FileNotFoundException {

        File file = new File(testFileRootPath + "TestClass.java");

        ParsedJavaModelBuilder javaModelBuilder = new ParsedJavaModelBuilder();
        Map<String, Object> model = javaModelBuilder.createModel(getJavaClass(new FileReader(file)));

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
        Assert.assertEquals("List<String>", parametricTestAttribute.get(ModelConstant.TYPE));
    }

    /**
     * Returns the {@link JavaClass} parsed by the given {@link Reader}
     * 
     * @param reader
     *        {@link Reader} which contents should be parsed
     * @return the parsed {@link JavaClass}
     * @author mbrunnli (19.03.2013)
     */
    private ModifyableJavaClass getJavaClass(Reader reader) {

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        JavaSource source = classLibraryBuilder.addSource(reader);
        return (ModifyableJavaClass) source.getClasses().get(0);
    }
}
