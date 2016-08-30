package com.capgemini.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.impl.config.entity.ContainerMatcher;
import com.capgemini.cobigen.impl.config.entity.Increment;
import com.capgemini.cobigen.impl.config.entity.Matcher;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.reader.TemplatesConfigurationReader;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link TemplatesConfigurationReader}
 *
 * @author mbrunnli (18.06.2013)
 */
public class TemplatesConfigurationReaderTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath =
        "src/test/resources/testdata/unittest/config/reader/TemplatesConfigurationReaderTest/";

    /**
     * Tests whether all templates of a template package could be retrieved successfully.
     * @throws Exception
     *             test fails
     * @author mbrunnli (18.06.2013)
     */
    @Test
    public void testTemplatesOfAPackageRetrieval() throws Exception {

        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        Template templateMock = mock(Template.class);
        HashMap<String, Template> templates = new HashMap<>();
        templates.put("resources_resources_spring_common", templateMock);
        target.loadIncrements(templates, trigger);
    }

    /**
     * Tests that templates will be correctly resolved by the template-scan mechanism.
     * @throws Exception
     *             test fails
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testTemplateScan() throws Exception {

        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        TriggerInterpreter triggerInterpreter = null;
        String templateIdSpringCommon = "resources_resources_spring_common";

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // then
        assertThat(templates).isNotNull().hasSize(7);
        Template templateSpringCommon = templates.get(templateIdSpringCommon);
        assertThat(templateSpringCommon).isNotNull();
        assertThat(templateSpringCommon.getName()).isEqualTo(templateIdSpringCommon);
        assertThat(templateSpringCommon.getTemplateFile()).isEqualTo("resources/resources/spring/common.xml.ftl");
        assertThat(templateSpringCommon.getUnresolvedDestinationPath())
            .isEqualTo("src/main/resources/resources/spring/common.xml");
        assertThat(templateSpringCommon.getMergeStrategy()).isNull();

        String templateIdFooClass = "prefix_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getTemplateFile()).isEqualTo("foo/FooClass.java.ftl");
        assertThat(templateFooClass.getUnresolvedDestinationPath()).isEqualTo("src/main/java/foo/FooClass.java");
        assertThat(templateFooClass.getMergeStrategy()).isNull();
    }

    /**
     * Tests that the template-scan mechanism does not overwrite an explicit template declaration with the
     * defaults
     * @throws Exception
     *             test fails
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testTemplateScanDoesNotOverwriteExplicitTemplateDeclarations() throws Exception {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        TriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // this one is a predefined template and shall not be overwritten by scan...
        String templateIdFoo2Class = "prefix_Foo2Class.java";
        Template templateFoo2Class = templates.get(templateIdFoo2Class);
        assertThat(templateFoo2Class).isNotNull();
        assertThat(templateFoo2Class.getName()).isEqualTo(templateIdFoo2Class);
        assertThat(templateFoo2Class.getTemplateFile()).isEqualTo("foo/Foo2Class.java.ftl");
        assertThat(templateFoo2Class.getUnresolvedDestinationPath())
            .isEqualTo("src/main/java/foo/Foo2Class${variable}.java");
        assertThat(templateFoo2Class.getMergeStrategy()).isEqualTo("javamerge");

        String templateIdBarClass = "prefix_BarClass.java";
        Template templateBarClass = templates.get(templateIdBarClass);
        assertThat(templateBarClass).isNotNull();
        assertThat(templateBarClass.getName()).isEqualTo(templateIdBarClass);
        assertThat(templateBarClass.getTemplateFile()).isEqualTo("foo/bar/BarClass.java.ftl");
        assertThat(templateBarClass.getUnresolvedDestinationPath()).isEqualTo("src/main/java/foo/bar/BarClass.java");
        assertThat(templateBarClass.getMergeStrategy()).isNull();
    }

    /**
     * Tests the overriding of all possible attributes by templateExtensions
     * @throws Exception
     *             test fails
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testTemplateExtensionDeclarationOverridesTemplateScanDefaults() throws Exception {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        TriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // validation

        // check scan default as precondition for this test. If they change, this test might be worth to be
        // adapted
        String templateIdBarClass = "prefix2_BarClass.java";
        Template templateBarClass = templates.get(templateIdBarClass);
        assertThat(templateBarClass).isNotNull();
        // template-scan defaults
        assertThat(templateBarClass.getName()).isEqualTo(templateIdBarClass);
        assertThat(templateBarClass.getTemplateFile()).isEqualTo("bar/BarClass.java.ftl");
        assertThat(templateBarClass.getUnresolvedDestinationPath()).isEqualTo("src/main/java/bar/BarClass.java");
        assertThat(templateBarClass.getMergeStrategy()).isNull();
        assertThat(templateBarClass.getTargetCharset()).isEqualTo("UTF-8");

        // check defaults overwriting by templateExtensions
        String templateIdFooClass = "prefix2_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        // template-scan defaults
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getTemplateFile()).isEqualTo("bar/FooClass.java.ftl");
        // overwritten by templateExtension
        assertThat(templateFooClass.getUnresolvedDestinationPath()).isEqualTo("adapted/path/FooClass.java");
        assertThat(templateFooClass.getMergeStrategy()).isEqualTo("javamerge");
        assertThat(templateFooClass.getTargetCharset()).isEqualTo("ISO-8859-1");
    }

    /**
     * Tests an empty templateExtensions does not override any defaults
     * @throws Exception
     *             test fails
     * @author mbrunnli (12.11.2014)
     */
    @Test
    public void testEmptyTemplateExtensionDeclarationDoesNotOverrideAnyDefaults() throws Exception {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        TriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);

        // validation
        String templateIdFooClass = "prefix2_Foo2Class.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        // template-scan defaults
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getTemplateFile()).isEqualTo("bar/Foo2Class.java.ftl");
        assertThat(templateFooClass.getUnresolvedDestinationPath()).isEqualTo("src/main/java/bar/Foo2Class.java");
        assertThat(templateFooClass.getMergeStrategy()).isNull();
        assertThat(templateFooClass.getTargetCharset()).isEqualTo("UTF-8");
    }

    /**
     * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

        new TemplatesConfigurationReader(new File(testFileRootPath + "faulty").toPath());
    }

    /**
     * Tests whether a duplicate template extension declaration will result in an
     * {@link InvalidConfigurationException}
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateTemplateExtensionDeclaration() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader = new TemplatesConfigurationReader(
            new File(testFileRootPath + "faulty_duplicate_template_extension").toPath());
        reader.loadTemplates(null, null);
    }

    /**
     * Tests whether a template extension with an id-reference, which does not point on any template, will
     * cause an {@link InvalidConfigurationException}
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnUnhookedTemplateExtensionDeclaration() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader = new TemplatesConfigurationReader(
            new File(testFileRootPath + "faulty_unhooked_template_extension").toPath());
        reader.loadTemplates(null, null);
    }

    /**
     * Tests whether a two equally named files will result in an {@link InvalidConfigurationException} if they
     * are scanned with the same prefix
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateScannedIds() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath + "faulty_duplicate_scanned_id").toPath());
        reader.loadTemplates(null, null);
    }

    /**
     * Tests the correct resolution of template scan references in increments.
     * @throws InvalidConfigurationException
     *             test fails
     * @author mbrunnli (Jun 19, 2015)
     */
    @Test
    public void testCorrectResolutionOfTemplateScanReferences() throws InvalidConfigurationException {

        // given
        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid_template_scan_references").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        TriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = reader.loadTemplates(trigger, triggerInterpreter);
        Map<String, Increment> increments = reader.loadIncrements(templates, trigger);

        // validation
        assertThat(templates).containsOnlyKeys("prefix_foo_BarClass.java", "prefix_bar_Foo2Class.java",
            "prefix_foo_FooClass.java");
        assertThat(increments).containsOnlyKeys("test");
        assertThat(increments.get("test").getTemplates()).extracting("name").containsOnly("prefix_foo_BarClass.java",
            "prefix_foo_FooClass.java");
    }

    /**
     * Tests the correct detection of duplicate template scan names.
     * @throws InvalidConfigurationException
     *             expected
     * @author mbrunnli (Jun 19, 2015)
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateTemplateScanNames() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader = new TemplatesConfigurationReader(
            new File(testFileRootPath + "faulty_duplicate_template_scan_name").toPath());
        reader.loadTemplates(null, null);
    }

    /**
     * Tests the correct detection of invalid template scan references.
     * @throws InvalidConfigurationException
     *             expected
     * @author mbrunnli (Jun 19, 2015)
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidTemplateScanReference() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath + "faulty_invalid_template_scan_ref").toPath());
        reader.loadTemplates(null, null);
    }

    /**
     * Tests the correct resolution of references of templates / templateScans / increments.
     * @author mbrunnli (Jun 25, 2015)
     */
    @Test
    public void testIncrementComposition_combiningAllPossibleReferences() {

        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath + "valid_increment_composition").toPath());

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
        TriggerInterpreter triggerInterpreter = null;

        // when
        Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);
        Map<String, Increment> increments = target.loadIncrements(templates, trigger);

        // validation

        assertThat(templates).containsOnlyKeys("templateDecl", "prefix_scanned", "scanned", "prefix_scanned2",
            "scanned2");
        assertThat(increments).containsOnlyKeys("0", "1", "2");
        assertThat(increments.values()).hasSize(3);
        assertThat(increments.get("0").getTemplates()).extracting("name").containsOnly("templateDecl");
        assertThat(increments.get("1").getTemplates()).extracting("name").containsOnly("templateDecl", "prefix_scanned",
            "scanned", "scanned2");
        assertThat(increments.get("2").getTemplates()).extracting("name").containsOnly("templateDecl", "prefix_scanned",
            "scanned", "prefix_scanned2");

    }

    /**
     * Test for <a href="https://github.com/devonfw/tools-cobigen/issues/167">Issue 167</a>. Tests if the
     * exception message from {@link #testErrorOnDuplicateScannedIds()} contains the name of the file causing
     * the exception
     *
     * @author sholzer (Dec 18, 2015)
     */
    @Test
    public void testExceptionMessageForDuplicateTemplateNames() {
        String message = "";
        try {
            testErrorOnDuplicateScannedIds();
            fail("An Exception should have been thrown");
        } catch (Exception e) {
            message = e.getMessage();
        }
        assertFalse(message.indexOf("Bar") == -1);
    }
}
