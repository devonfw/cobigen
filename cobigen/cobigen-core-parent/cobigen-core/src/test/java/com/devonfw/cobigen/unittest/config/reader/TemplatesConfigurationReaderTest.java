package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.TemplateExtension;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScan;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReader;
import com.devonfw.cobigen.impl.config.reader.TemplatesConfigurationReader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link TemplatesConfigurationReader}
 */
public class TemplatesConfigurationReaderTest extends AbstractUnitTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath =
        "src/test/resources/testdata/unittest/config/reader/TemplatesConfigurationReaderTest/";

    /**
     * Tests whether all templates of a template package could be retrieved successfully.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testTemplatesOfAPackageRetrieval() throws Exception {

        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid");

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
     */
    @Test
    public void testTemplateScan() throws Exception {

        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // then
        assertThat(templates).isNotNull().hasSize(6);

        String templateIdFooClass = "prefix_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getRelativeTemplatePath()).isEqualTo("foo/FooClass.java.ftl");
        assertThat(templateFooClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/foo/FooClass.java");
        assertThat(templateFooClass.getMergeStrategy()).isNull();
    }

    @Test
    public void testTemplatesSourceFolder() {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid_source_folder");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // then
        assertThat(templates).isNotNull().hasSize(6);

        String templateIdFooClass = "prefix_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getRelativeTemplatePath()).isEqualTo("foo/FooClass.java.ftl");
        assertThat(templateFooClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/foo/FooClass.java");
        assertThat(templateFooClass.getMergeStrategy()).isNull();
    }

    /**
     * Tests that the template-scan mechanism does not overwrite an explicit template declaration with the
     * defaults
     * @throws Exception
     *             test fails
     */
    @Test
    public void testTemplateScanDoesNotOverwriteExplicitTemplateDeclarations() throws Exception {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // this one is a predefined template and shall not be overwritten by scan...
        String templateIdFoo2Class = "prefix_Foo2Class.java";
        Template templateFoo2Class = templates.get(templateIdFoo2Class);
        assertThat(templateFoo2Class).isNotNull();
        assertThat(templateFoo2Class.getName()).isEqualTo(templateIdFoo2Class);
        assertThat(templateFoo2Class.getRelativeTemplatePath()).isEqualTo("foo/Foo2Class.java.ftl");
        assertThat(templateFoo2Class.getUnresolvedTargetPath())
            .isEqualTo("src/main/java/foo/Foo2Class${variable}.java");
        assertThat(templateFoo2Class.getMergeStrategy()).isEqualTo("javamerge");

        String templateIdBarClass = "prefix_BarClass.java";
        Template templateBarClass = templates.get(templateIdBarClass);
        assertThat(templateBarClass).isNotNull();
        assertThat(templateBarClass.getName()).isEqualTo(templateIdBarClass);
        assertThat(templateBarClass.getRelativeTemplatePath()).isEqualTo("foo/bar/BarClass.java.ftl");
        assertThat(templateBarClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/foo/bar/BarClass.java");
        assertThat(templateBarClass.getMergeStrategy()).isNull();
    }

    /**
     * Tests the overriding of all possible attributes by templateExtensions
     * @throws Exception
     *             test fails
     */
    @Test
    public void testTemplateExtensionDeclarationOverridesTemplateScanDefaults() throws Exception {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // validation

        // check scan default as precondition for this test. If they change, this test might be worth to be
        // adapted
        String templateIdBarClass = "prefix2_BarClass.java";
        Template templateBarClass = templates.get(templateIdBarClass);
        assertThat(templateBarClass).isNotNull();
        // template-scan defaults
        assertThat(templateBarClass.getName()).isEqualTo(templateIdBarClass);
        assertThat(templateBarClass.getRelativeTemplatePath()).isEqualTo("bar/BarClass.java.ftl");
        assertThat(templateBarClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/bar/BarClass.java");
        assertThat(templateBarClass.getMergeStrategy()).isNull();
        assertThat(templateBarClass.getTargetCharset()).isEqualTo("UTF-8");

        // check defaults overwriting by templateExtensions
        String templateIdFooClass = "prefix2_FooClass.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        // template-scan defaults
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getRelativeTemplatePath()).isEqualTo("bar/FooClass.java.ftl");
        // overwritten by templateExtension
        assertThat(templateFooClass.getUnresolvedTargetPath()).isEqualTo("adapted/path/FooClass.java");
        assertThat(templateFooClass.getMergeStrategy()).isEqualTo("javamerge");
        assertThat(templateFooClass.getTargetCharset()).isEqualTo("ISO-8859-1");
    }

    /**
     * Tests an empty templateExtensions does not override any defaults
     * @throws Exception
     *             test fails
     */
    @Test
    public void testEmptyTemplateExtensionDeclarationDoesNotOverrideAnyDefaults() throws Exception {
        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // validation
        String templateIdFooClass = "prefix2_Foo2Class.java";
        Template templateFooClass = templates.get(templateIdFooClass);
        assertThat(templateFooClass).isNotNull();
        // template-scan defaults
        assertThat(templateFooClass.getName()).isEqualTo(templateIdFooClass);
        assertThat(templateFooClass.getRelativeTemplatePath()).isEqualTo("bar/Foo2Class.java.ftl");
        assertThat(templateFooClass.getUnresolvedTargetPath()).isEqualTo("src/main/java/bar/Foo2Class.java");
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

        new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "faulty");
    }

    /**
     * Tests whether a duplicate template extension declaration will result in an
     * {@link InvalidConfigurationException}
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateTemplateExtensionDeclaration() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "faulty_duplicate_template_extension");
        reader.loadTemplates(null);
    }

    /**
     * Tests whether a template extension with an id-reference, which does not point on any template, will
     * cause an {@link InvalidConfigurationException}
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnUnhookedTemplateExtensionDeclaration() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "faulty_unhooked_template_extension");
        reader.loadTemplates(null);
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
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "faulty_duplicate_scanned_id");
        reader.loadTemplates(null);
    }

    /**
     * Tests the correct resolution of template scan references in increments.
     * @throws InvalidConfigurationException
     *             test fails
     */
    @Test
    public void testCorrectResolutionOfTemplateScanReferences() throws InvalidConfigurationException {

        // given
        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid_template_scan_references");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = reader.loadTemplates(trigger);
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
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnDuplicateTemplateScanNames() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "faulty_duplicate_template_scan_name");
        reader.loadTemplates(null);
    }

    /**
     * Tests the correct detection of invalid template scan references.
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testErrorOnInvalidTemplateScanReference() throws InvalidConfigurationException {

        TemplatesConfigurationReader reader =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "faulty_invalid_template_scan_ref");
        reader.loadTemplates(null);
    }

    /**
     * Tests the correct resolution of references of templates / templateScans / increments.
     */
    @Test
    public void testIncrementComposition_combiningAllPossibleReferences() {

        // given
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid_increment_composition");

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);
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
     * Tests the correct resolution of TemplateRef from outside the current templates file.
     */
    @Test
    public void testTemplateRefOutsideCurrentFile() {
        // given
        Trigger trigger = new Trigger("testingTrigger", "asdf", "valid_external_templateref", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        ConfigurationHolder configurationHolder =
            new ConfigurationHolder(Paths.get(new File(testFileRootPath).toURI()));

        TemplatesConfiguration templatesConfiguration = configurationHolder.readTemplatesConfiguration(trigger);
        Map<String, Increment> increments = templatesConfiguration.getIncrements();

        assertThat(templatesConfiguration.getTrigger().getId()).isEqualTo("testingTrigger");

        // validation
        Increment incrementThree = increments.get("3");
        LinkedList<String> templateNamesThree = new LinkedList<>();
        for (Template tmplate : incrementThree.getTemplates()) {
            templateNamesThree.add(tmplate.getName());
        }
        assertThat(templateNamesThree).containsExactly("templateDecl");

        Increment incrementFour = increments.get("4");
        LinkedList<String> templateNamesFour = new LinkedList<>();
        for (Template tmplate : incrementFour.getTemplates()) {
            templateNamesFour.add(tmplate.getName());
        }
        assertThat(templateNamesFour).containsExactly("ExplicitlyDefined");
    }

    /**
     * Tests the correct resolution of incrementsRef from outside the current templates file. (Issue #678)
     */
    @Test
    public void testIncrementRefOutsideCurrentFile() {

        // given
        Trigger trigger = new Trigger("testingTrigger", "asdf", "valid_external_incrementref", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        ConfigurationHolder configurationHolder =
            new ConfigurationHolder(Paths.get(new File(testFileRootPath).toURI()));

        TemplatesConfiguration templatesConfiguration = configurationHolder.readTemplatesConfiguration(trigger);
        Map<String, Increment> increments = templatesConfiguration.getIncrements();

        // validation
        assertThat(templatesConfiguration.getTrigger().getId()).isEqualTo("testingTrigger");
        assertThat(increments).containsOnlyKeys("3", "4", "5");

        Increment incrementThree = increments.get("3").getDependentIncrements().get(0);
        assertThat(incrementThree.getName()).isEqualTo("0");
        assertThat(incrementThree.getTemplates().size()).isEqualTo(1);

        Increment incrementFour = increments.get("4").getDependentIncrements().get(0);
        assertThat(incrementFour.getName()).isEqualTo("1");
        assertThat(incrementFour.getTemplates().size()).isEqualTo(4);

        Increment incrementFive = increments.get("5").getDependentIncrements().get(0);
        assertThat(incrementFive.getName()).isEqualTo("2");
        assertThat(incrementFive.getTemplates().size()).isEqualTo(4);
    }

    /**
     * Tests the correct detection of invalid external increment reference.
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidIncrementRefOutsideCurrentFile() {

        new ContextConfigurationReader(Paths.get(new File(testFileRootPath).toURI()));

        // given
        ConfigurationHolder configurationHolder =
            new ConfigurationHolder(Paths.get(new File(testFileRootPath).toURI()));

        TemplatesConfigurationReader target = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "faulty_invalid_external_incrementref", configurationHolder);

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);
        target.loadIncrements(templates, trigger);
    }

    /**
     * Tests the correct detection of invalid external increment reference.
     * @throws InvalidConfigurationException
     *             expected
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidTemplateRefOutsideCurrentFile() {

        new ContextConfigurationReader(Paths.get(new File(testFileRootPath).toURI()));

        // given
        ConfigurationHolder configurationHolder =
            new ConfigurationHolder(Paths.get(new File(testFileRootPath).toURI()));

        TemplatesConfigurationReader target = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "faulty_invalid_external_templateref", configurationHolder);

        Trigger trigger = new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);
        target.loadIncrements(templates, trigger);
    }

    /**
     * Test for <a href="https://github.com/devonfw/cobigen/issues/167">Issue 167</a>. Tests if the
     * exception message from {@link #testErrorOnDuplicateScannedIds()} contains the name of the file causing
     * the exception
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

    /**
     * Tests the rewriting of the destination path of a scanned template by using the
     * {@link TemplateExtension} configuration element. The explicitly configured destination path from the
     * configuration should have precedence over the relocated path of the template scan.
     */
    @Test
    public void testRelocate_overlappingTemplateExtensionAndScan() {
        // given
        String templateScanDestinationPath = "src/main/java/";
        String templatesConfigurationRoot = testFileRootPath + "valid_relocate_templateExt_vs_scan/";
        TemplatesConfigurationReader target = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "valid_relocate_templateExt_vs_scan/");

        Trigger trigger = new Trigger("id", "type", "valid_relocate", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);
        assertThat(templates).hasSize(2);

        // validation
        String staticRelocationPrefix = "../api/";
        String scanRelTemplatePath = "$_rootpackage_$/$_component_$/common/api/";
        Template template = verifyScannedTemplate(templates, "$_EntityName_$.java", scanRelTemplatePath,
            templatesConfigurationRoot, staticRelocationPrefix, templateScanDestinationPath);

        String templateName = "$_EntityName_$2.java";
        template = templates.get(templateName);
        assertThat(template).isNotNull();
        String pathWithName = scanRelTemplatePath + templateName;
        assertThat(template.getRelativeTemplatePath()).isEqualTo("templates/" + pathWithName);
        assertThat(template.getAbsoluteTemplatePath().toString().replace('\\', '/'))
            .isEqualTo(templatesConfigurationRoot + "templates/" + pathWithName);
        assertThat(template.getUnresolvedTemplatePath()).isEqualTo(templateName);
        assertThat(template.getUnresolvedTargetPath()).isEqualTo(templateName);
    }

    /**
     * Tests an overlapping configuration according to the destination paths of a relocated folder within a
     * template scan and a explicitly defined destination path of a template configuration XML node. The
     * destination path of a template configuration should not be affected by any relocation of any template
     * scan.
     */
    @Test
    public void testRelocate_overlappingExplicitTemplateDestinationPathAndRelocatedScanPath() {
        // given
        String templateScanDestinationPath = "src/main/java/";
        String templatesConfigurationRoot = testFileRootPath + "valid_relocate_template_vs_scan/";
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid_relocate_template_vs_scan/");

        Trigger trigger = new Trigger("id", "type", "valid_relocate", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);
        assertThat(templates).hasSize(2);

        // validation
        String staticRelocationPrefix = "../api/";
        String scanRelTemplatePath = "$_rootpackage_$/$_component_$/common/api/";
        Template template = verifyScannedTemplate(templates, "$_EntityName_$.java", scanRelTemplatePath,
            templatesConfigurationRoot, staticRelocationPrefix, templateScanDestinationPath);

        template = templates.get("ExplicitlyDefined");
        assertThat(template).isNotNull();
        assertThat(template.getRelativeTemplatePath()).isEqualTo("OuterTemplate.java");
        assertThat(template.getAbsoluteTemplatePath().toString().replace('\\', '/'))
            .isEqualTo(templatesConfigurationRoot + "OuterTemplate.java");
        // the destination path has designed to match a relocated path during the scan by intention
        String destinationPath = "src/main/java/$_rootpackage_$/$_component_$/common/api/ExplicitlyDefined.java";
        assertThat(template.getUnresolvedTemplatePath()).isEqualTo(destinationPath);
        assertThat(template.getUnresolvedTargetPath()).isEqualTo(destinationPath);
        assertThat(template.getVariables().asMap()).hasSize(0);
    }

    /**
     * Tests the correct property inheritance and resolution of cobigen.properties within a template set read
     * by a template scan.
     */
    @Test
    public void testRelocate_propertiesResolution() {
        // arrange
        String templatesConfigurationRoot = testFileRootPath + "valid_relocate_propertiesresolution/";
        TemplatesConfigurationReader target = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "valid_relocate_propertiesresolution/");

        Trigger trigger = new Trigger("id", "type", "valid_relocate", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        // act
        Map<String, Template> templates = target.loadTemplates(trigger);
        assertThat(templates).hasSize(2);

        // assert
        Template template = templates.get("$_Component_$.java");
        assertThat(template).isNotNull();
        assertThat(template.getVariables().asMap()).isNotNull().containsEntry("foo", "root").containsEntry("bar",
            "barValue");

        template = templates.get("$_EntityName_$Eto.java");
        assertThat(template).isNotNull();
        assertThat(template.getVariables().asMap()).isNotNull().containsEntry("relocate", "../api2/${cwd}")
            .containsEntry("foo", "logic.api.to").containsEntry("bar", "barValue").containsEntry("local", "localValue");
    }

    /**
     * Test relocate while the template is defined with the template file ending, which should be removed on
     * destination path resolution.
     */
    @Test
    public void testRelocate_withTemplateFilenameEnding() {

        // given
        String templatesConfigurationRoot = testFileRootPath + "valid_relocate_template_fileending/";
        TemplatesConfigurationReader target = new TemplatesConfigurationReader(new File(testFileRootPath).toPath(),
            "valid_relocate_template_fileending/");

        Trigger trigger = new Trigger("id", "type", "valid_relocate", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // validation
        assertThat(templates).hasSize(1);

        String staticRelocationPrefix = "../server/";
        String templateName = "$_Component_$Impl.java";
        Template template = templates.get(templateName);
        assertThat(template).isNotNull();
        String pathWithName = "$_rootpackage_$/$_component_$/logic/impl/" + templateName;
        assertThat(template.getRelativeTemplatePath()).isEqualTo("templates/" + pathWithName + ".ftl");
        assertThat(template.getAbsoluteTemplatePath().toString().replace('\\', '/'))
            .isEqualTo(templatesConfigurationRoot + "templates/" + pathWithName + ".ftl");
        assertThat(template.getUnresolvedTemplatePath()).isEqualTo("src/main/java/" + pathWithName);
        assertThat(template.getUnresolvedTargetPath()).isEqualTo(staticRelocationPrefix + pathWithName);

    }

    /**
     * Test the basic valid configuration of
     * <a href="https://github.com/devonfw/cobigen/issues/157">issue 157</a> for relocation of templates
     * to support multi-module generation.
     */
    @Test
    public void testRelocate() {

        // given
        String noRelocation = "";
        String templateScanDestinationPath = "src/main/java/";
        String templatesConfigurationRoot = testFileRootPath + "valid_relocate/";
        TemplatesConfigurationReader target =
            new TemplatesConfigurationReader(new File(testFileRootPath).toPath(), "valid_relocate/");

        Trigger trigger = new Trigger("id", "type", "valid_relocate", Charset.forName("UTF-8"),
            new LinkedList<Matcher>(), new LinkedList<ContainerMatcher>());

        // when
        Map<String, Template> templates = target.loadTemplates(trigger);

        // validation
        assertThat(templates).hasSize(3);

        String staticRelocationPrefix = "../api/";
        verifyScannedTemplate(templates, "$_EntityName_$Entity.java", "$_rootpackage_$/$_component_$/dataaccess/api/",
            templatesConfigurationRoot, staticRelocationPrefix, templateScanDestinationPath);

        staticRelocationPrefix = "../api2/";
        verifyScannedTemplate(templates, "$_EntityName_$Eto.java", "$_rootpackage_$/$_component_$/logic/api/to/",
            templatesConfigurationRoot, staticRelocationPrefix, templateScanDestinationPath);

        verifyScannedTemplate(templates, "$_Component_$.java", "$_rootpackage_$/$_component_$/logic/api/",
            templatesConfigurationRoot, noRelocation, templateScanDestinationPath);
    }

    /**
     * Verifies a template's path properties
     * @param templates
     *            list of all templates mapping template name to template
     * @param templateName
     *            name of the template
     * @param templatePath
     *            template path
     * @param templatesConfigurationRoot
     *            configuration root folder of the templates configuration
     * @param staticRelocationPrefix
     *            static prefix of a relocation value excluding ${cwd}
     * @param templateScanDestinationPath
     *            destination path of the involved {@link TemplateScan}
     * @return the template with the given templateName
     */
    private Template verifyScannedTemplate(Map<String, Template> templates, String templateName, String templatePath,
        String templatesConfigurationRoot, String staticRelocationPrefix, String templateScanDestinationPath) {

        Template template = templates.get(templateName);
        assertThat(template).isNotNull();
        String pathWithName = templatePath + templateName;
        assertThat(template.getRelativeTemplatePath()).isEqualTo("templates/" + pathWithName);
        assertThat(template.getAbsoluteTemplatePath().toString().replace('\\', '/'))
            .isEqualTo(templatesConfigurationRoot + "templates/" + pathWithName);
        assertThat(template.getUnresolvedTemplatePath()).isEqualTo("src/main/java/" + pathWithName);
        if (StringUtils.isEmpty(staticRelocationPrefix)) {
            assertThat(template.getUnresolvedTargetPath()).isEqualTo(templateScanDestinationPath + pathWithName);
        } else {
            assertThat(template.getUnresolvedTargetPath()).isEqualTo(staticRelocationPrefix + pathWithName);
        }
        return template;
    }
}
