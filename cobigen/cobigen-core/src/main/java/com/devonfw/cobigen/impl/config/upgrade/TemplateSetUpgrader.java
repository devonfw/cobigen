package com.devonfw.cobigen.impl.config.upgrade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.TemplatesConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Link;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Links;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Tag;
import com.devonfw.cobigen.impl.config.entity.io.v6_0.Tags;

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
   * @param Path {@link Path} to the templatesLocation
   *
   * @return {@link Map} collection that contains the upgraded v3.0
   *         {@link com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration} as key and a {@link Path} for
   *         the new location of the context.xml as value
   * @throws Exception if an issue occurred in directory copy operations
   */
  public Map<com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration, Path> upgradeTemplatesToTemplateSets(
      Path templatesLocation) throws Exception {

    Path cobigenTemplatesFolder = CobiGenPaths.getPomLocation(templatesLocation);
    Path parentOfCobigenTemplates = cobigenTemplatesFolder.getParent();
    Path templateSets = null;
    File folderToRename = null;
    Path backupFolder = null;
    if (parentOfCobigenTemplates.endsWith(ConfigurationConstants.TEMPLATES_FOLDER)) {
      // #1 case Here we need to rename parentOfCobigenTemplates to template-sets
      templateSets = parentOfCobigenTemplates.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      folderToRename = parentOfCobigenTemplates.toFile();
      backupFolder = parentOfCobigenTemplates.getParent().resolve(ConfigurationConstants.BACKUP_FOLDER);
    } else {
      // #2 case we need to rename cobigenTemplatesFolder to template-sets, this is only the case if the
      // parentOfCobigenTemplates name is not "templates"
      templateSets = parentOfCobigenTemplates.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      folderToRename = cobigenTemplatesFolder.toFile();
      backupFolder = parentOfCobigenTemplates.resolve(ConfigurationConstants.BACKUP_FOLDER);
    }
    // backup of old files
    Files.createDirectory(backupFolder);
    try {
      FileUtils.copyDirectoryToDirectory(folderToRename, backupFolder.toFile());
    } catch (IOException e) {
      LOG.error("An error occured while backing up the old template folder", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    }

    Path adapted = folderToRename.toPath().resolve(ConfigurationConstants.ADAPTED_FOLDER);
    if (Files.exists(templateSets)) {
      throw new CobiGenRuntimeException("template-sets folder already exists!");
    }
    if (!Files.exists(adapted)) {
      Files.createDirectory(adapted);
    }
    Path folderOfContextLocation = CobiGenPaths.getContextLocation(templatesLocation);
    ContextConfiguration contextConfiguration = getContextConfiguration(folderOfContextLocation);

    List<com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration> contextFiles = splitContext(
        contextConfiguration);
    Map<com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration, Path> contextMap = new HashMap<>();
    for (com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration cc : contextFiles) {
      for (com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger trigger : cc.getTrigger()) {
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
          if (Files.exists(utilsPath)) {
            FileUtils.copyDirectory(utilsPath.toFile(),
                newTriggerFolder.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER).toFile());
          }
        } catch (Exception e) {
          LOG.error("An error occurred while copying the template utilities Folder", e);
          throw new CobiGenRuntimeException(e.getMessage(), e);
        }

        // Read templates.xml then delete it
        Path tcPath = newTriggerFolder.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve("templates.xml");
        TemplatesConfiguration tcV6 = readTemplatesConfigurationV6(tcPath);
        Files.delete(tcPath);

        TemplateSetConfiguration tsc = buildTemplateSetConfigurationV6(tcV6, cc);
        Path tscPath = newTriggerFolder.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
        try (OutputStream out = Files.newOutputStream(tscPath)) {
          JAXB.marshal(tsc, out);
        }

        Path newContextPath = newTriggerFolder.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        contextMap.put(cc, newContextPath);
        // creates actual context configuration file
        try (OutputStream out = Files.newOutputStream(newContextPath)) {
          JAXB.marshal(cc, out);
        }
        writeNewPomFile(cobigenTemplatesFolder, newTriggerFolder, trigger);
        newContextPath = templateSets.resolve(folderToRename.toPath().relativize(newTriggerFolder)
            .resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME));
        contextMap.put(cc, newContextPath);
      }
    }

    // cleanup
    for (File file : folderToRename.listFiles()) {
      if (!file.getName().equals(ConfigurationConstants.ADAPTED_FOLDER)) {
        FileUtils.forceDelete(file);
      }
    }
    folderToRename.renameTo(templateSets.toFile());

    return contextMap;

  }

  /**
   * Writes a pom.xml file for the split context and template folder
   *
   * @param cobigenTemplates {@link Path} to the CobiGen_Templates folder
   * @param cobigenTemplates {@link Path} to the split template folder
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger} to the related template folder
   * @throws IOException
   * @throws FileNotFoundException
   */
  private void writeNewPomFile(Path cobigenTemplates, Path newTemplateFolder,
      com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger trigger) throws IOException, FileNotFoundException {

    // Pom.xml creation
    try {
      Path oldPom = cobigenTemplates.resolve(MavenConstants.POM);
      Path newPom = newTemplateFolder.resolve(MavenConstants.POM);
      if (!Files.exists(newPom))
        Files.createFile(newPom);

      // read the content of the pom.xml then replace it
      Charset charset = StandardCharsets.UTF_8;
      String content = new String(Files.readAllBytes(oldPom), charset);
      content = content.replaceAll("</modelVersion>\n" + "  <groupId>([a-zA-Z]+(\\.[a-zA-Z]+)+)</groupId>",
          "</modelVersion>\n" + "  <groupId>" + ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID
              + "</groupId>");
      content = content.replaceAll("</groupId>\n" + "  <artifactId>.*</artifactId>", "</groupId>\n" + "  <artifactId>"
          + newTemplateFolder.getFileName().toString().replace('_', '-') + "</artifactId>");
      content = content.replaceAll("</artifactId>\n" + "  <version>([0-9]+(\\.[0-9]+)+)</version>", "</artifactId>\n"
          + "  <version>" + ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_VERSION + "</version>");
      content = content.replaceAll("</version>\n" + "  <name>.*</name>",
          "</version>\n" + "  <name>" + newTemplateFolder.getFileName().toString() + "</name>");
      Files.write(newPom, content.getBytes(charset));

    } catch (FileNotFoundException e) {
      LOG.error("Monolitic pom file could not be found", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    } catch (IOException e) {
      LOG.error("IOError while reading the monolithic pom file", e);
      throw new CobiGenRuntimeException(e.getMessage(), e);
    }

  }

  /**
   * Splits a contextConfiguration and converts a {@link Trigger} and his data to a v6_0 Trigger
   *
   * @param monolitic {@link ContextConfiguration} of the monolithic context that will be split
   * @return {@link com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration} List of the split
   *         contextConfiguration files
   */
  private List<com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration> splitContext(
      ContextConfiguration monolitic) {

    List<com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration> splitContexts = new ArrayList<>();
    List<Trigger> triggerList = monolitic.getTrigger();
    for (Trigger trigger : triggerList) {
      com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration contextConfiguration6_0 = new com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration();
      com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger trigger6_0 = new com.devonfw.cobigen.impl.config.entity.io.v6_0.Trigger();
      trigger6_0.setId(trigger.getId());
      trigger6_0.setInputCharset(trigger.getInputCharset());
      trigger6_0.setType(trigger.getType());
      trigger6_0.setTemplateFolder(trigger.getTemplateFolder());

      List<com.devonfw.cobigen.impl.config.entity.io.v6_0.Matcher> v3MList = this.mapper.mapAsList(trigger.getMatcher(),
          com.devonfw.cobigen.impl.config.entity.io.v6_0.Matcher.class);
      List<com.devonfw.cobigen.impl.config.entity.io.v6_0.ContainerMatcher> v3CMList = this.mapper.mapAsList(
          trigger.getContainerMatcher(), com.devonfw.cobigen.impl.config.entity.io.v6_0.ContainerMatcher.class);
      trigger6_0.getContainerMatcher().addAll(v3CMList);
      trigger6_0.getMatcher().addAll(v3MList);
      contextConfiguration6_0.getTrigger().add(trigger6_0);
      Tags tags = new Tags();
      Tag tag = new Tag();
      tag.setName(
          "PLACEHOLDER---This tag was inserted through the upgrade process and has to be changed manually---PLACEHOLDER");
      tags.getTag().add(tag);
      contextConfiguration6_0.setTags(tags);
      Links links = new Links();
      Link link = new Link();
      link.setUrl(
          "PLACEHOLDER---This tag was inserted through the upgrade process and has to be changed manually---PLACEHOLDER");
      links.getLink().add(link);
      contextConfiguration6_0.setLinks(links);
      contextConfiguration6_0.setVersion(new BigDecimal("3.0"));
      splitContexts.add(contextConfiguration6_0);
    }
    return splitContexts;
  }

  private TemplateSetConfiguration buildTemplateSetConfigurationV6(TemplatesConfiguration tcV6,
      com.devonfw.cobigen.impl.config.entity.io.v6_0.ContextConfiguration ccV6) {

    TemplateSetConfiguration tsc = new TemplateSetConfiguration();
    tsc.setTemplatesConfiguration(tcV6);
    tsc.setContextConfiguration(ccV6);
    return tsc;
  }

  /**
   * Reads templates.xml file
   * 
   * @param templatesFile {@link Path} to the templates.xml file
   * @return {@link TemplatesConfiguration} V6 templates configuration object
   */
  private TemplatesConfiguration readTemplatesConfigurationV6(Path templatesFile) throws IOException, JAXBException {

    try (InputStream in = Files.newInputStream(templatesFile)) {
      Unmarshaller um = JAXBContext.newInstance(TemplatesConfiguration.class).createUnmarshaller();

      Object rootNode = um.unmarshal(in);
      if (rootNode instanceof TemplatesConfiguration) {
        return (TemplatesConfiguration) rootNode;
      }
    } catch (IOException e) {
      throw new InvalidConfigurationException("Templates file could not be found", e);
    } catch (JAXBException e) {
      throw new InvalidConfigurationException("Templates file provided some XML errors", e);
    }
    return null;
  }

  /**
   * Locates and returns the correct context file
   *
   * @param contextFile {@link Path} to the contextFile
   * @return {@link ContextConfiguration}
   */
  private ContextConfiguration getContextConfiguration(Path contextFile) throws Exception {

    if (contextFile == null) {
      throw new CobiGenRuntimeException("Templates location cannot be null!");
    }
    // check if context exits here
    Path context = contextFile.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    if (Files.exists(context)) {
      LOG.info("Found Context File");
    } else {
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
