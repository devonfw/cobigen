/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.javaplugin;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import com.capgemini.cobigen.javaplugin.inputreader.FreeMarkerUtil;
import com.capgemini.cobigen.javaplugin.inputreader.ModelConstant;
import com.capgemini.cobigen.model.JaxenXPathSupportNodeModel;
import com.capgemini.cobigen.model.ModelConverter;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * TestCase testing the FreeMarker util class integration
 * 
 * @author mbrunnli (16.04.2013)
 */
public class FreeMarkerUtilIntegrationTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/FreeMarkerUtilIntegrationTest/";

    /**
     * Tests all cases of the subtype relation check
     * 
     * @throws TemplateException
     * @throws IOException
     * @author mbrunnli (12.04.2013)
     */
    @Test
    public void testIsSubtypeOf() throws TemplateException, IOException {

        StringWriter strWriter = new StringWriter();
        generateTemplateAndWritePatch(strWriter, loadTemplate("template_isSubtypeOf.ftl"));
        Assert.assertEquals("truetruefalse", strWriter.toString().trim());
    }

    /**
     * Tests all cases of the subtype relation check
     * 
     * @throws TemplateException
     * @throws IOException
     * @author mbrunnli (12.04.2013)
     */
    @Test
    public void testIsAbstract() throws TemplateException, IOException {

        StringWriter strWriter = new StringWriter();
        generateTemplateAndWritePatch(strWriter, loadTemplate("template_isAbstract.ftl"));
        Assert.assertEquals("falsetrue", strWriter.toString().trim());
    }

    /**
     * Generates the given template contents using the given model and writes the contents into the given {@link Writer}
     * 
     * @param out
     *        {@link Writer} in which the contents will be written (the {@link Writer} will be flushed and closed)
     * @param template
     *        FreeMarker template which will generate the contents
     * @throws TemplateException
     *         if an exception occurs during template processing
     * @throws IOException
     *         if an I/O exception occurs (during writing to the writer)
     * @author mbrunnli (12.03.2013)
     */
    private void generateTemplateAndWritePatch(Writer out, Template template) throws TemplateException, IOException {

        Document doc = new ModelConverter(new HashMap<String, Object>()).convertToDOM();
        Environment env = template.createProcessingEnvironment(doc, out);
        env.setOutputEncoding(template.getOutputEncoding());
        env.setCurrentVisitorNode(new JaxenXPathSupportNodeModel(doc));
        env.setGlobalVariable(ModelConstant.UTILS, new BeanModel(new FreeMarkerUtil(this.getClass().getClassLoader()),
                new DefaultObjectWrapper()));
        env.process();
        out.flush();
        out.close();
    }

    /**
     * Loads a template from test root path
     * 
     * @param template
     *        to be loaded
     * @return {@link Template}
     * @throws IOException
     * @author mbrunnli (16.04.2013)
     */
    private Template loadTemplate(String template) throws IOException {

        Configuration freeMarkerConfig = new Configuration();
        freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
        freeMarkerConfig.setEncoding(Locale.GERMANY, "UTF-8");
        freeMarkerConfig.setDefaultEncoding("UTF-8");
        freeMarkerConfig.setDirectoryForTemplateLoading(new File(testFileRootPath));
        return freeMarkerConfig.getTemplate(template);
    }
}
