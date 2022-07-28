package com.devonfw.cobigen.impl.config.upgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Link;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Links;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Tag;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Tags;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Upgrader for the templates from v2_1 to v3_0 that splits the monolithic template structure into version 3.0 with
 * template sets
 */
public class TemplateSetUpgrader {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetUpgrader.class);

  /** Mapper factory instance. */
  private MapperFactory mapperFactory;

  /** Mapper Facade instance. */
  private MapperFacade mapper;

  /**
   * Creates a new {@link TemplateSetUpgrader} instance to upgrade v2.1 ContextConfigurations to v3.0
   */
  public TemplateSetUpgrader() {

    this.mapperFactory = new DefaultMapperFactory.Builder().useAutoMapping(true).mapNulls(true).build();
    this.mapperFactory
        .classMap(com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher.class,
            com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class)
        .field(
            "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
            "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
        .byDefault().register();
    this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.Trigger.class,
        com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger.class).byDefault().register();
    this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.Matcher.class,
        com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher.class).byDefault().register();
    this.mapperFactory
        .classMap(com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher.class,
            com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class)
        .field(
            "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
            "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
        .byDefault().register();
    this.mapper = this.mapperFactory.getMapperFacade();
  }

  /**
   * Upgrades the ContextConfiguration from v2.1 to the new structure from v3.0. The monolithic pom and context files
   * will be split into multiple files corresponding to every template set that will be created.
   *
   * @param contextLocation the location of the context configuration file
   *
   * @param {@link Path} Path to the context.xml that will be upgraded
   * @return {@link Map} collection that contains the upgraded v3.0
   *         {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration} as key and a {@link Path} for
   *         the new location of the context.xml as value
   * @throws Exception if an issue occurred in directory copy operations
   */
  public Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> upgradeTemplatesToTemplateSets(
      Path contextLocation) throws Exception {

    Path cobigenHome = CobiGenPaths.checkCustomHomePath(contextLocation);
    if (cobigenHome == null)
      cobigenHome = CobiGenPaths.getCobiGenHomePath();

    List<Path> newContextLocation = FileSystemUtil.collectAllContextXML(contextLocation);
    Path folderOfContextLocation = newContextLocation.get(0).getParent();
    ContextConfiguration contextConfiguration = getContextConfiguration(folderOfContextLocation);
    Path templateSets = cobigenHome.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    if (!Files.exists(templateSets))
      Files.createDirectory(templateSets);
    Path adapted = Files.createDirectory(templateSets.resolve(ConfigurationConstants.ADAPTED_FOLDER));
    Path cobigenTemplates = folderOfContextLocation.getParent().getParent().getParent().getParent()
        .resolve(ConfigurationConstants.COBIGEN_TEMPLATES);

    List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration> contextFiles = splitContext(
        contextConfiguration);
    Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> contextMap = new HashMap<>();
    for (com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration cc : contextFiles) {
      for (com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger : cc.getTrigger()) {
        Path triggerFolder = folderOfContextLocation.resolve(trigger.getTemplateFolder());
        Path newTriggerFolder = adapted.resolve(trigger.getTemplateFolder());
        Path utilsPath = folderOfContextLocation.getParent().resolve("java");
        try {
          FileUtils.copyDirectory(triggerFolder.toFile(),
              newTriggerFolder.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER).toFile());
        } catch (Exception e) {
          LOG.error("An error occurred while copying the template Folder", e);
          throw new CobiGenRuntimeException(e.getMessage(), e);
        }
        try {
          FileUtils.copyDirectory(utilsPath.toFile(),
              newTriggerFolder.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER).toFile());
        } catch (Exception e) {
          LOG.error("An error occurred while copying the template utilities Folder", e);
          throw new CobiGenRuntimeException(e.getMessage(), e);
        }

        Path newContextPath = newTriggerFolder.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        contextMap.put(cc, newContextPath);
        // creates actual context configuration file
        try (OutputStream out = Files.newOutputStream(newContextPath)) {
          JAXB.marshal(cc, out);
        }
        writeNewPomFile(cobigenTemplates, newTriggerFolder, trigger);
      }
    }

    // backup of old files
    Path backupFolder = cobigenHome.resolve("backup");
    File backupTemplatesLocation = backupFolder.toFile();
    if (!backupTemplatesLocation.exists()) {
      backupTemplatesLocation.mkdir();
    }
    try {
      FileUtils.copyDirectoryToDirectory(cobigenTemplates.getParent().toFile(), backupTemplatesLocation);
    } catch (IOException e) {
      LOG.error("An error occured while backing up the old template folder", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    }
    return contextMap;

  }

  /**
   * Writes a pom.xml file for the split context and template folder
   *
   * @param {@link Path}cobigen_templates Path to the CobiGen_Templates folder
   * @param {@link Path}newTemplateFolder Path to the split template folder
   * @param {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger }trigger to the related template folder
   * @throws IOException
   * @throws XmlPullParserException
   * @throws ClassNotFoundException
   */
  private void writeNewPomFile(Path cobigen_templates, Path newTemplateFolder,
      com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger)
      throws IOException, XmlPullParserException, ClassNotFoundException {

    // Pom.xml creation
    /**
     * TODO Because Eclipse application cannot load these classes we need to look for other solutions:
     *
     * 1-copy files 2-replace by regex
     *
     */
    MavenXpp3Reader reader = new MavenXpp3Reader();
    MavenXpp3Writer writer = new MavenXpp3Writer();
    Model monolithicPomModel;
    try (FileInputStream pomInputStream = new FileInputStream(cobigen_templates.resolve("pom.xml").toFile());) {
      monolithicPomModel = reader.read(pomInputStream);
    } catch (FileNotFoundException e) {
      LOG.error("Monolitic pom file could not be found", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    } catch (IOException e) {
      LOG.error("IOError while reading the monolithic pom file", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    } catch (XmlPullParserException e) {
      LOG.error("XMLError while parsing the monolitic pom file", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    }

    Model splitPomModel = new Model();
    splitPomModel.setGroupId(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID);
    splitPomModel.setModelVersion(monolithicPomModel.getModelVersion());
    splitPomModel.setVersion("2021.12.007");
    splitPomModel.setDependencies(monolithicPomModel.getDependencies());
    splitPomModel.setPackaging(monolithicPomModel.getPackaging());
    splitPomModel.setArtifactId(trigger.getId().replace('_', '-'));
    splitPomModel.setName("PLACEHOLDER---Replace this text with a correct template name---PLACEHOLDER");
    splitPomModel.setProperties(monolithicPomModel.getProperties());
    splitPomModel.setBuild(monolithicPomModel.getBuild());
    try (FileOutputStream pomOutputStream = new FileOutputStream(newTemplateFolder.resolve("pom.xml").toFile());) {
      writer.write(new FileOutputStream(newTemplateFolder.resolve("pom.xml").toFile()), splitPomModel);
    } catch (FileNotFoundException e) {
      LOG.error("An error occured while creating the new v3_0 pom file", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    } catch (IOException e) {
      LOG.error("An IOError occured while writing the new v3_0 pom file", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    }

  }

  /**
   * Splits a contextConfiguration and converts a {@link Trigger} and his data to a v3_0 Trigger
   *
   * @param {@link ContextConfiguration}the monolithic context that will be split
   * @return {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration} List of the split
   *         contextConfiguration files
   */
  private List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration> splitContext(
      ContextConfiguration monolitic) {

    List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration> splitContexts = new ArrayList<>();
    List<Trigger> triggerList = monolitic.getTrigger();
    for (Trigger trigger : triggerList) {
      com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration contextConfiguration3_0 = new com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration();
      com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger3_0 = new com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger();
      trigger3_0.setId(trigger.getId());
      trigger3_0.setInputCharset(trigger.getInputCharset());
      trigger3_0.setType(trigger.getType());
      trigger3_0.setTemplateFolder(trigger.getTemplateFolder());

      List<com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher> v3MList = this.mapper.mapAsList(trigger.getMatcher(),
          com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher.class);
      List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher> v3CMList = this.mapper.mapAsList(
          trigger.getContainerMatcher(), com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class);
      trigger3_0.getContainerMatcher().addAll(v3CMList);
      trigger3_0.getMatcher().addAll(v3MList);
      contextConfiguration3_0.getTrigger().add(trigger3_0);
      Tags tags = new Tags();
      Tag tag = new Tag();
      tag.setName(
          "PLACEHOLDER---This tag was inserted through the upgrade process and has to be changed manually---PLACEHOLDER");
      tags.getTag().add(tag);
      contextConfiguration3_0.setTags(tags);
      Links links = new Links();
      Link link = new Link();
      link.setUrl(
          "PLACEHOLDER---This tag was inserted through the upgrade process and has to be changed manually---PLACEHOLDER");
      links.getLink().add(link);
      contextConfiguration3_0.setLinks(links);
      contextConfiguration3_0.setVersion(new BigDecimal("3.0"));
      splitContexts.add(contextConfiguration3_0);
    }
    return splitContexts;
  }

  /**
   * Locates and returns the correct context file
   *
   * @param {@link Path} to the contextFile
   * @return {@link ContextConfiguration}
   * @throws Exception
   */
  private ContextConfiguration getContextConfiguration(Path contextFile) throws Exception {

    if (contextFile == null) {
      throw new Exception("Templates location cannot be null!");
    }
    // check if context exits here
    Path context = contextFile.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    if (Files.exists(context)) {
      LOG.info("Found Context File");
    } else {
      if (contextFile.endsWith(ConfigurationConstants.COBIGEN_TEMPLATES)) {
        context = contextFile.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        LOG.info("Found Context File");
      } else if (contextFile.endsWith(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)) {
        context = contextFile.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
            .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        LOG.info("Found Context File");
      }
    }
    if (!Files.exists(context)) {
      throw new FileNotFoundException("Context.xml could not be found");
    }

    try (InputStream in = Files.newInputStream(context)) {
      Unmarshaller unmarschaller = JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();

      Object rootNode = unmarschaller.unmarshal(in);
      if (rootNode instanceof ContextConfiguration) {
        return (ContextConfiguration) rootNode;
      }
    } catch (IOException e) {
      throw new InvalidConfigurationException("Context file could not be found", e);
    } catch (JAXBException e) {
      throw new InvalidConfigurationException("Context file provided some XML errors", e);
    }
    return null;
  }

}
