/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.reader;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;

import com.capgemini.cobigen.config.entity.Matcher;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;

/**
 * This {@link TestCase} tests the {@link TemplatesConfigurationReader}
 * 
 * @author mbrunnli (18.06.2013)
 */
public class TemplatesConfigurationReaderTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/TemplatesConfigurationReaderTest/";

    /**
     * Tests whether all templates of a template package could be retrieved successfully
     * 
     * @author mbrunnli (18.06.2013)
     * @throws InvalidConfigurationException
     */
    @Test
    public void testTemplatesOfAPackageRetrieval() throws InvalidConfigurationException {

        TemplatesConfigurationReader target =
                new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>());
        Template templateMock = mock(Template.class);
        HashMap<String, Template> templates = new HashMap<String, Template>();
        templates.put("resources_resources_spring_common", templateMock);
        target.loadIncrements(templates, trigger);
    }

    /**
     * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
     * 
     * @throws InvalidConfigurationException
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

        new TemplatesConfigurationReader(new File(testFileRootPath + "templates_faulty.xml"));
    }

}
