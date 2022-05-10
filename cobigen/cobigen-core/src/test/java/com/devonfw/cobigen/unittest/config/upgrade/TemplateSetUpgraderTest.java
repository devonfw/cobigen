package com.devonfw.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.upgrade.TemplateSetUpgrader;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;
import com.google.common.collect.Lists;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Test suite for {@link TemplateSetUpgrader}
 */
public class TemplateSetUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplateSetUpgraderTest/";

  private Path templateLocation;

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void prepare() throws IOException {

    Path playground = this.tempFolder.newFolder("playground").toPath();
    FileUtils.copyDirectory(new File(testFileRootPath), playground.toFile());
    this.templateLocation = playground.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
  }

  @Test
  public void testTemplateSetUpgrade() throws Exception {

    TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader(this.templateLocation);
    templateSetUpgrader.upradeTemplatesToTemplateSets();

    Path templateSetsPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path templateSetsAdapted = templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    assertThat(templateSetsPath).exists();
    assertThat(templateSetsAdapted).exists();
    // hier noch besser testen

  }

  @Test
  public void testTemplateSetUpgradeCopyOfTemplates() throws Exception {

	  TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader(this.templateLocation);
	  templateSetUpgrader.upradeTemplatesToTemplateSets();

	  Path oldTemplatesPath = this.templateLocation.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
	  oldTemplatesPath = oldTemplatesPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
	  Path newTemplatesPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
	  newTemplatesPath = newTemplatesPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
	  assertEquals(oldTemplatesPath.toFile().list().length, newTemplatesPath.toFile().list().length +1);
	  Set<String> OldPathFilesSet= new HashSet<>(Arrays.asList(oldTemplatesPath.toFile().list()));
	  Set<String> NewPathFilesSet= new HashSet<>(Arrays.asList(newTemplatesPath.toFile().list()));
	  OldPathFilesSet.remove("context.xml");
	  for(String s: OldPathFilesSet) {
		  assertTrue(NewPathFilesSet.contains(s));
		  NewPathFilesSet.remove(s);
	  }
	  assertEquals(NewPathFilesSet.size(), 0);
  }


  public void testTemplateSetUpgradeContextLocation() throws Exception{
	  TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader(this.templateLocation);
	  templateSetUpgrader.upradeTemplatesToTemplateSets();

	  Path newTemplatesPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
	  newTemplatesPath = newTemplatesPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
	  for(String s: newTemplatesPath.toFile().list()) {
		  Path newContextPath = newTemplatesPath.resolve(s+"/"+ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
		  Set<String> NewPathFilesSet= new HashSet<>(Arrays.asList(newContextPath.toFile().list()));
		  assertTrue(NewPathFilesSet.contains(s));
	}
  }

  @Test
  public void testTemplateSetUpgradeContextSplit() throws Exception{
	  TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader(this.templateLocation);
	  templateSetUpgrader.upradeTemplatesToTemplateSets();

	  Path newTemplatesPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
	  newTemplatesPath = newTemplatesPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);

	  SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	  //ContextConfigurationVersion latestConfigurationVersion = ContextConfigurationVersion.getLatest(); es wird ja immer version 3.0 gecheckt
	  InputStream schemaStream = getClass().getResourceAsStream("/schema/v3.0/contextConfiguration.xsd");
	  Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
	  Validator validator = schema.newValidator();

	  for(String s: newTemplatesPath.toFile().list()) {
		  Path newContextPath = newTemplatesPath.resolve(s+"/"+ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
		  newContextPath = newContextPath.resolve("context.xml");
		  assertThat(newContextPath.toFile().exists());
		  validator.validate(new StreamSource(newContextPath.toFile()));
	  }
  }
}
