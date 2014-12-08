/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.reader;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.config.entity.ContainerMatcher;
import com.capgemini.cobigen.config.entity.Matcher;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.ITriggerInterpreter;

/**
 * This {@link TestCase} tests the {@link TemplatesConfigurationReader}
 *
 * @author mbrunnli (18.06.2013)
 */
public class TemplatesConfigurationReaderTest extends Assert {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath =
        "src/test/resources/testdata/config/reader/TemplatesConfigurationReaderTest/";

    /**
     * Tests whether all templates of a template package could be retrieved successfully
     *
     * @author mbrunnli (18.06.2013)
     */
    @Test
    public void testTemplatesOfAPackageRetrieval() {

        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

        Trigger trigger =
            new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
                new LinkedList<ContainerMatcher>());
        Template templateMock = mock(Template.class);
        HashMap<String, Template> templates = new HashMap<>();
        templates.put("resources_resources_spring_common", templateMock);
        target.loadIncrements(templates, trigger);
    }

    /**
     * Tests that templates will be correctly resolved by the template-scan mechanism
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testTemplateScan() {

        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

        Trigger trigger =
            new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
                new LinkedList<ContainerMatcher>());
        ITriggerInterpreter triggerInterpreter = null;
        String templateIdSpringCommon = "resources_resources_spring_common";

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // then
        assertNotNull(templates);
        Template templateSpringCommon = templates.get(templateIdSpringCommon);
        assertNotNull(templateSpringCommon);
        assertEquals(templateIdSpringCommon, templateSpringCommon.getId());
        assertEquals("resources/resources/spring/common.xml.ftl", templateSpringCommon.getTemplateFile());
        assertEquals("src/main/resources/resources/spring/common.xml",
            templateSpringCommon.getUnresolvedDestinationPath());
        assertNull(templateSpringCommon.getMergeStrategy());

        String templateIdFooClass = "prefix_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertNotNull(templateFooClass);
        assertEquals(templateIdFooClass, templateFooClass.getId());
        assertEquals("foo/FooClass.java.ftl", templateFooClass.getTemplateFile());
        assertEquals("src/main/java/foo/FooClass.java", templateFooClass.getUnresolvedDestinationPath());
        assertNull(templateFooClass.getMergeStrategy());
    }

    /**
     * Tests that the template-scan mechanism does not overwrite an explicit template declaration with the
     * defaults
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testTemplateScanDoesNotOverwriteExplicitTemplateDeclarations() {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

        Trigger trigger =
            new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
                new LinkedList<ContainerMatcher>());
        ITriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // this one is a predefined template and shall not be overwritten by scan...
        String templateIdFoo2Class = "prefix_Foo2Class.java";
        Template templateFoo2Class = templates.get(templateIdFoo2Class);
        assertNotNull(templateFoo2Class);
        assertEquals(templateIdFoo2Class, templateFoo2Class.getId());
        assertEquals("foo/Foo2Class.java.ftl", templateFoo2Class.getTemplateFile());
        assertEquals("src/main/java/foo/Foo2Class${variable}.java",
            templateFoo2Class.getUnresolvedDestinationPath());
        assertEquals("javamerge", templateFoo2Class.getMergeStrategy());

        String templateIdBarClass = "prefix_BarClass.java";
        Template templateBarClass = templates.get(templateIdBarClass);
        assertNotNull(templateBarClass);
        assertEquals(templateIdBarClass, templateBarClass.getId());
        assertEquals("foo/bar/BarClass.java.ftl", templateBarClass.getTemplateFile());
        assertEquals("src/main/java/foo/bar/BarClass.java", templateBarClass.getUnresolvedDestinationPath());
        assertNull(templateBarClass.getMergeStrategy());
    }

    /**
     * Tests the overriding of all possible attributes by templateExtensions
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testTemplateExtensionDeclarationOverridesTemplateScanDefaults() {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

        Trigger trigger =
            new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
                new LinkedList<ContainerMatcher>());
        ITriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // validation

        // check scan default as precondition for this test. If they change, this test might be worth to be
        // adapted
        String templateIdBarClass = "prefix2_BarClass.java";
        Template templateBarClass = templates.get(templateIdBarClass);
        assertNotNull(templateBarClass);
        // template-scan defaults
        assertEquals(templateIdBarClass, templateBarClass.getId());
        assertEquals("bar/BarClass.java.ftl", templateBarClass.getTemplateFile());
        assertEquals("src/main/java/bar/BarClass.java", templateBarClass.getUnresolvedDestinationPath());
        assertNull(templateBarClass.getMergeStrategy());
        assertEquals("UTF-8", templateBarClass.getTargetCharset());

        // check defaults overwriting by templateExtensions
        String templateIdFooClass = "prefix2_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertNotNull(templateFooClass);
        // template-scan defaults
        assertEquals(templateIdFooClass, templateFooClass.getId());
        assertEquals("bar/FooClass.java.ftl", templateFooClass.getTemplateFile());
        // overwritten by templateExtension
        assertEquals("adapted/path/FooClass.java", templateFooClass.getUnresolvedDestinationPath());
        assertEquals("javamerge", templateFooClass.getMergeStrategy());
        assertEquals("ISO-8859-1", templateFooClass.getTargetCharset());
    }

    /**
     * Tests an empty templateExtensions does not override any defaults
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testEmptyTemplateExtensionDeclarationDoesNotOverrideAnyDefaults() {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

        Trigger trigger =
            new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
                new LinkedList<ContainerMatcher>());
        ITriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // validation
        String templateIdFooClass = "prefix2_Foo2Class.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertNotNull(templateFooClass);
        // template-scan defaults
        assertEquals(templateIdFooClass, templateFooClass.getId());
        assertEquals("bar/Foo2Class.java.ftl", templateFooClass.getTemplateFile());
        assertEquals("src/main/java/bar/Foo2Class.java", templateFooClass.getUnresolvedDestinationPath());
        assertNull(templateFooClass.getMergeStrategy());
        assertEquals("UTF-8", templateFooClass.getTargetCharset());
    }

    /**
     * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidConfiguration() {

        new TemplatesConfigurationReader(new File(testFileRootPath + "templates_faulty.xml"));
    }

    /**
     * Tests whether a duplicate template extension declaration will result in an
     * {@link InvalidConfigurationException}
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateTemplateExtensionDeclaration() {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath
                + "templates_faulty_duplicate_templateExtension.xml"));
        reader.loadTemplates(null, null);
    }

    /**
     * Tests whether a template extension with an id-reference, which does not point on any template, will
     * cause an {@link InvalidConfigurationException}
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnUnhookedTemplateExtensionDeclaration() {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath
                + "templates_faulty_unhooked_templateExtension.xml"));
        reader.loadTemplates(null, null);
    }

    /**
     * Tests whether a two equally named files will result in an {@link InvalidConfigurationException} if they
     * are scanned with the same prefix
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateScannedIds() {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath
                + "templates_faulty_duplicate_scanned_id.xml"));
        reader.loadTemplates(null, null);
    }
}
