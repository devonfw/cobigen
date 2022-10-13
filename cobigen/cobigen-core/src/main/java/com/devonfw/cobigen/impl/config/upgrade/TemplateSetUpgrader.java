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
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Link;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Links;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Tag;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Tags;

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
   * @param templatesLocation the location of the templates
   *
   * @param {@link Path} Path to the context.xml that will be upgraded
   * @return {@link Map} collection that contains the upgraded v3.0
   *         {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration} as key and a {@link Path} for
   *         the new location of the context.xml as value
   * @throws Exception if an issue occurred in directory copy operations
   */
  public Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> upgradeTemplatesToTemplateSets(
      Path templatesLocation) throws Exception {

    Path cobigenTemplatesFolder = CobiGenPaths.getPomLocation(templatesLocation);
    Path parentOfCobigenTemplates = cobigenTemplatesFolder.getParent();
    Path template_sets = null;
    Path adapted = null;
    if (Files.exists(template_sets)) {
      throw new CobiGenRuntimeException("MACH WAS");
    }
    Path folderOfContextLocation = CobiGenPaths.getContextLocation(templatesLocation);
    if (parentOfCobigenTemplates.endsWith(ConfigurationConstants.TEMPLATES_FOLDER)) {
      template_sets = parentOfCobigenTemplates.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      adapted = cobigenTemplatesFolder.resolve(ConfigurationConstants.ADAPTED_FOLDER);
      parentOfCobigenTemplates.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER).toFile()
          .renameTo(template_sets.toFile());
      // rename cobigenTemplatesFolder to backup
    } else {
      template_sets = parentOfCobigenTemplates.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      parentOfCobigenTemplates.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER).toFile()
          .renameTo(template_sets.toFile());
      adapted = template_sets.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    }

    // if (renamedParentCobigenTemplatesFolder.exists()) {
    // folderOfContextLocation = CobiGenPaths.getContextLocation(renamedParentCobigenTemplatesFolder.toPath());
    // cobigenTemplatesFolder = CobiGenPaths.getPomLocation(renamedParentCobigenTemplatesFolder.toPath());
    // adapted = renamedParentCobigenTemplatesFolder.toPath().resolve(ConfigurationConstants.ADAPTED_FOLDER);
    // }
    ContextConfiguration contextConfiguration = getContextConfiguration(folderOfContextLocation);

    if (!Files.exists(adapted))
      Files.createDirectory(adapted);

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
          if (Files.exists(utilsPath)) {
            FileUtils.copyDirectory(utilsPath.toFile(),
                newTriggerFolder.resolve(ConfigurationConstants.UTIL_RESOURCE_FOLDER).toFile());
          }
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
        writeNewPomFile(cobigenTemplatesFolder, newTriggerFolder, trigger);
      }
    }

    File renamedCobigenTemplatesFolder = new File(
        renamedParentCobigenTemplatesFolder + "/" + ConfigurationConstants.BACKUP_FOLDER);
    cobigenTemplatesFolder.toFile().renameTo(renamedCobigenTemplatesFolder);

    return contextMap;

  }

  /**
   * Writes a pom.xml file for the split context and template folder
   *
   * @param {@link Path}cobigen_templates Path to the CobiGen_Templates folder
   * @param {@link Path}newTemplateFolder Path to the split template folder
   * @param {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger }trigger to the related template folder
   * @throws IOException
   * @throws FileNotFoundException
   */
  private void writeNewPomFile(Path cobigenTemplates, Path newTemplateFolder,
      com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger) throws IOException, FileNotFoundException {

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
