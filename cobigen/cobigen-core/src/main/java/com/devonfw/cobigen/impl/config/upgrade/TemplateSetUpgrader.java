package com.devonfw.cobigen.impl.config.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.Trigger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class TemplateSetUpgrader {

  private Path templatesLocation;

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetUpgrader.class);

  public TemplateSetUpgrader(Path templatesLocation) {

    this.templatesLocation = templatesLocation;
  }

  public void upradeTemplatesToTemplateSets() throws Exception {

    if (this.templatesLocation == null) {
      throw new Exception("Templates location cannot be null!");
    }

    if (this.templatesLocation.endsWith(ConfigurationConstants.TEMPLATES_FOLDER)) {
      Path cobigenTemplates = this.templatesLocation.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
      if (Files.exists(cobigenTemplates)) {
        Path contextFile = cobigenTemplates.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        if (Files.exists(contextFile)) {
          ContextConfiguration contextConfiguration = getContextConfiguration(contextFile);
          if (contextConfiguration != null) {
            // create new template set folder
            Path templateSetsPath = Files.createDirectory(
                this.templatesLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER));
            Path adaptedFolder = Files.createDirectory(templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER));

            List<Trigger> triggers = contextConfiguration.getTrigger();
            for (Trigger trigger : triggers) {
              processTrigger(trigger, cobigenTemplates, adaptedFolder);
            }
          } else {
            LOG.info("Unable to parse context.xml file {}.", contextFile);
          }
        } else {
          LOG.info("No context.xml file found. {}", contextFile);
        }
      } else {
        LOG.info("No CobiGen_Templates folder found. Upgrade needs an adapted templates folder.");
      }
    } else {
      LOG.info("The path {} is no valid templates location.", this.templatesLocation);
    }
  }

  /**
   * @param contextFile
   * @return
   */
  private ContextConfiguration getContextConfiguration(Path contextFile) {

    try (InputStream in = Files.newInputStream(contextFile)) {
      Unmarshaller unmarschaller = JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();

      Object rootNode = unmarschaller.unmarshal(in);
      if (rootNode instanceof ContextConfiguration) {
        return (ContextConfiguration) rootNode;
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private void processTrigger(Trigger trigger, Path cobigenTemplates, Path templateSetsAdapted) throws IOException {

    Path templatesPath = cobigenTemplates.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
        .resolve(trigger.getTemplateFolder());
    Path templateSetPath = Files.createDirectory(templateSetsAdapted.resolve(trigger.getTemplateFolder()));

    // copy template files
    FileUtils.copyDirectory(templatesPath.toFile(),
        templateSetPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER).toFile());

    // copy java utils
    Path utilsPath = cobigenTemplates.resolve("src/main/java");
    FileUtils.copyDirectory(utilsPath.toFile(), templateSetPath.resolve("src/main/java").toFile());

    // create context.xml
    com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration contextConfiguration = new com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration();
    contextConfiguration.setVersion(new BigDecimal(3.0));

    List<com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger> triggerList = contextConfiguration.getTrigger();
    com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger3_0 = new com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger();
    trigger3_0.setId(trigger.getId());
    trigger3_0.setInputCharset(trigger.getInputCharset());
    trigger3_0.setType(trigger.getType());
    // TODO write trigger in new context.xml

    // TODO create pom.xml
  }
}
