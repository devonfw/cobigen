package com.devonfw.cobigen.unittest.config.upgrade;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Link;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Links;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Matcher;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Tag;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Tags;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Template;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.config.upgrade.TemplateSetConfigurationUpgrader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * Test suite for {@link ContextConfigurationUpgrader}
 */
public class ContextConfigurationUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String contextTestFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/ContextConfigurationUpgraderTest";

  private static String templateTestFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplateSetUpgraderTest";

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /**
   * Tests the valid upgrade of a context configuration from version v2.0 to v2.1. Please make sure that
   * .../ContextConfigurationUpgraderTest/valid-v2.1 exists
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectUpgrade_v2_0_TO_v_2_1() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_0;
    ContextConfigurationVersion targetVersion = ContextConfigurationVersion.v2_1;
    String currentVersionPath = "valid-v2.0";
    String targetVersionPath = "valid-v2.1";

    Path cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE).toPath();
    Path context = cobigen.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    File sourceTestdata = new File(contextTestFileRootPath + File.separator + currentVersionPath + File.separator
        + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

    FileUtils.copyDirectory(new File(contextTestFileRootPath + File.separator + currentVersionPath), cobigen.toFile());

    ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

    ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(context, currentVersion);
    assertThat(version).as("Source Version").isEqualTo(currentVersion);

    sut.upgradeConfigurationToLatestVersion(cobigen, BackupPolicy.ENFORCE_BACKUP, targetVersion);
    assertThat(cobigen.resolve("context.bak.xml").toFile()).exists().hasSameContentAs(sourceTestdata);

    version = sut.resolveLatestCompatibleSchemaVersion(cobigen, targetVersion);
    assertThat(version).as("Target version").isEqualTo(targetVersion);

    XMLUnit.setIgnoreWhitespace(true);
    try (
        Reader firstReader = new FileReader(contextTestFileRootPath + File.separator + targetVersionPath
            + File.separator + ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        Reader secondReader = new FileReader(context.toFile())) {
      new XMLTestCase() {
      }.assertXMLEqual(firstReader, secondReader);
    }
  }

  /**
   * Tests the valid upgrade of a context configuration from version v2.1 to v6.0. This also includes a merge of a
   * templateConfiguration v5.0 into a templateSetConfigration
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectUpgrade_v2_1_TO_v6_0() throws Exception {

    File cobigen = this.tempFolder.newFolder(ConfigurationConstants.COBIGEN_CONFIG_FILE);

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, cobigen.toPath().toString()).execute(() -> {
      // preparation
      ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_1;
      ContextConfigurationVersion targetVersion = ContextConfigurationVersion.v6_0;

      String currentVersionPath = "valid-2.1";
      String targetVersionPath = "valid-v3.0";
      String targetVersionPathTS = "valid-v6.0";

      Path templates = cobigen.toPath().resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
          .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
      Path context = templates.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

      FileUtils.copyDirectory(new File(templateTestFileRootPath + File.separator + currentVersionPath), cobigen);

      ContextConfigurationUpgrader sut = new ContextConfigurationUpgrader();

      ContextConfigurationVersion version = sut.resolveLatestCompatibleSchemaVersion(context, currentVersion);
      assertThat(version).as("Source Version").isEqualTo(currentVersion);

      sut.upgradeConfigurationToLatestVersion(templates, BackupPolicy.ENFORCE_BACKUP, targetVersion);
      // copy resources again to check if backup was successful
      String pom = "templates/CobiGen_Templates/pom.xml";
      FileUtils.copyDirectory(new File(templateTestFileRootPath + File.separator + currentVersionPath), cobigen);

      Path newTemplatesLocation = cobigen.toPath().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER)
          .resolve(ConfigurationConstants.ADAPTED_FOLDER);
      Path backupContextPath = cobigen.toPath().resolve("backup")
          .resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)
          .resolve(ConfigurationConstants.COBIGEN_TEMPLATES).resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

      for (String s : newTemplatesLocation.toFile().list()) {
        Path newContextPath = newTemplatesLocation.resolve(s).resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);

        version = sut.resolveLatestCompatibleSchemaVersion(newContextPath, targetVersion);
        assertThat(version).as("Target version").isEqualTo(targetVersion);

        newContextPath = newContextPath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
        XMLUnit.setIgnoreWhitespace(true);
        try (
            Reader firstReader = new FileReader(contextTestFileRootPath + File.separator + targetVersionPathTS
                + File.separator + s + File.separator + ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
            Reader secondReader = new FileReader(newContextPath.toFile())) {
          new XMLTestCase() {
          }.assertXMLEqual(firstReader, secondReader);
        }
      }
    });
  }

  /**
   * Tests if v2.0 context configuration is compatible to v2.0 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV2_0SchemaDetection() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_0;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v6.0 templateSet configuration is compatible to v6.0 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV6_0SchemaDetection() throws Exception {

    // preparation
    TemplateSetConfigurationVersion currentVersion = TemplateSetConfigurationVersion.v6_0;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      TemplateSetConfigurationVersion version = new TemplateSetConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  @Test
  public void TestContextV6unmarshall() {

    String input = "Input";
    com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration ccv6 = new ContextConfiguration();
    ccv6.setVersion(new BigDecimal(6));
    List<com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger> tiggerList = ccv6.getTrigger();
    Trigger trigger = new Trigger();
    com.devonfw.cobigen.impl.config.entity.io.v6_0.ContainerMatcher cm = new ContainerMatcher();
    cm.setRetrieveObjectsRecursively(true);
    cm.setType(input);
    cm.setValue(input);
    trigger.getContainerMatcher().add(cm);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Matcher m = new Matcher();
    m.setType(input);
    m.setValue(input);
    m.setAccumulationType(com.devonfw.cobigen.impl.config.entity.io.v6_0.AccumulationType.OR);
    trigger.getMatcher();
    trigger.setId(input);
    trigger.setInputCharset(input);
    trigger.setTemplateFolder(input);
    trigger.setType(input);
    tiggerList.add(trigger);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Tags tagList = new Tags();
    Tag tag = new Tag();
    tag.setName(input);
    tagList.getTag().add(tag);
    ccv6.setTags(tagList);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Links LinkList = new Links();
    Link link = new Link();
    link.setDescription(input);
    link.setUrl(input);
    LinkList.getLink().add(link);
    ccv6.setLinks(LinkList);

    try {
      Marshaller marschaller = JAXBContext
          .newInstance(com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration.class).createMarshaller();
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory
          .newSchema(new StreamSource(getClass().getResourceAsStream("/schema/v6.0/contextConfiguration.xsd")));
      marschaller.setSchema(schema);
      marschaller.marshal(ccv6, this.tempFolder.newFile("testcontext.xml"));
    } catch (Exception e) {
      assertTrue(false);
    }

    com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplatesConfiguration tC = new com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplatesConfiguration();
    tC.setTemplateEngine(input);
    tC.setVersion(new BigDecimal("6.0"));
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Template template = new Template();
    template.setDestinationPath(input);
    template.setMergeStrategy(input);
    template.setName(input);
    template.setTargetCharset(input);
    template.setTemplateFile(input);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Templates templates = new com.devonfw.cobigen.impl.config.entity.io.v6_0.Templates();
    templates.getTemplate().add(template);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateExtension te = new com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateExtension();
    te.setDestinationPath(input);
    te.setMergeStrategy(input);
    te.setRef(input);
    te.setTargetCharset(input);
    templates.getTemplateExtension().add(te);
    tC.setTemplates(templates);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Increment inc = new com.devonfw.cobigen.impl.config.entity.io.v6_0.Increment();
    inc.setDescription(input);
    inc.setExplanation(input);
    inc.setName(input);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.IncrementRef incref = new com.devonfw.cobigen.impl.config.entity.io.v6_0.IncrementRef();
    incref.setRef(input);
    inc.getTemplateRefOrIncrementRefOrTemplateScanRef().add(incref);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.Increments incs = new com.devonfw.cobigen.impl.config.entity.io.v6_0.Increments();
    incs.getIncrement().add(inc);
    tC.setIncrements(incs);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateScan tempscan = new com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateScan();
    tempscan.setDestinationPath(input);
    tempscan.setMergeStrategy(input);
    tempscan.setName(input);
    tempscan.setTargetCharset(input);
    tempscan.setTemplateNamePrefix(input);
    tempscan.setTemplatePath(input);
    com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateScans tempscans = new com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateScans();
    tempscans.getTemplateScan().add(tempscan);
    tC.setTemplateScans(tempscans);

    try {
      Marshaller marschaller = JAXBContext
          .newInstance(com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplatesConfiguration.class).createMarshaller();
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory
          .newSchema(new StreamSource(getClass().getResourceAsStream("/schema/v6.0/templatesConfiguration.xsd")));
      marschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marschaller.setSchema(schema);
      marschaller.marshal(tC, this.tempFolder.newFile("testTemplate.xml"));
    } catch (Exception e) {
      assertTrue(false);
    }

    com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateSetConfiguration tsc = new TemplateSetConfiguration();
    tsc.setContextConfiguration(ccv6);
    tsc.setTemplatesConfiguration(tC);
    tsc.setVersion(new BigDecimal("6.0"));
    try {
      Marshaller marschaller = JAXBContext
          .newInstance(com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateSetConfiguration.class)
          .createMarshaller();
      marschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(new StreamSource[] {
      new StreamSource(getClass().getResourceAsStream("/schema/v6.0/templatesConfiguration.xsd")),
      new StreamSource(getClass().getResourceAsStream("/schema/v6.0/contextConfiguration.xsd")),
      new StreamSource(getClass().getResourceAsStream("/schema/v6.0/templateSetConfiguration.xsd")), });
      marschaller.setSchema(schema);
      marschaller.marshal(tsc, this.tempFolder.newFile("testTemplateSet.xml"));
    } catch (Exception e) {
      assertTrue(false);
    }

  }

  /**
   * Tests if v2.1 context configuration is compatible to v2.1 schema.
   *
   * @throws Exception test fails
   */
  @Test
  public void testCorrectV2_1SchemaDetection() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_1;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v3.0 context configuration is compatible to v3.0 schema.
   *
   * @throws Exception test fails
   */
  @Ignore // not needed anymore v3 can be removed or v6 renamed to v3
  @Test
  public void testCorrectV3_0SchemaDetection() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v6_0;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath(), currentVersion);
      assertThat(version).isEqualTo(currentVersion);
    }
  }

  /**
   * Tests if v2.1 context configuration schema is not compatible to v6.0 configuration file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testV2_1IsIncompatibleToV3_0Schema() throws Exception {

    // preparation
    ContextConfigurationVersion currentVersion = ContextConfigurationVersion.v2_1;
    ContextConfigurationVersion targetVersion = ContextConfigurationVersion.v6_0;
    File targetConfig = new File(contextTestFileRootPath + "/valid-" + currentVersion);

    for (File context : targetConfig.listFiles()) {
      ContextConfigurationVersion version = new ContextConfigurationUpgrader()
          .resolveLatestCompatibleSchemaVersion(context.toPath());
      assertThat(version).isNotEqualTo(targetVersion);
    }
  }
}