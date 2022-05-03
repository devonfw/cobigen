package com.devonfw.cobigen.impl.config.upgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.XMLFormatter;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;
import org.glassfish.jaxb.core.marshaller.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.Trigger;
//import com.google.j2objc.annotations.Property;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.OrikaSystemProperties;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class TemplateSetUpgrader {

  private Path templatesLocation;

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetUpgrader.class);
  private MapperFactory mapperFactory;
  private MapperFacade mapper;

  private String POM = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
  		+ "  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
  		+ "  <modelVersion>4.0.0</modelVersion>\n"
  		+ "  <artifactId>crud-openapi-angular-client-app</artifactId>\n"
  		+ "\n"
  		+ "  <parent>\n"
  		+ "    <groupId>com.devonfw.cobigen.templates</groupId>\n"
  		+ "    <artifactId>templates-parent</artifactId>\n"
  		+ "    <version>${revision}</version>\n"
  		+ "  </parent>\n"
  		+ "\n"
  		+ "</project>";

  private String POMdependencies = "	<dependencies>\n	</dependencies>\n";
  private String POMdependency ="    <dependency>\n"
	  		  		+ "      <groupId>org.apache.commons</groupId>\n"
	  		  		+ "      <artifactId>commons-lang3</artifactId>\n"
	  		  		+ "    </dependency>\n";


  public TemplateSetUpgrader(Path templatesLocation) {
    this.templatesLocation = templatesLocation;
    this.mapperFactory = new DefaultMapperFactory.Builder().useAutoMapping(true).mapNulls(true).build();
    this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher.class, com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class)
    .field("retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
    "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
    .byDefault().register();
    this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.Trigger.class, com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger.class)
    .byDefault().register();
    this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.Matcher.class, com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher.class)
    .byDefault().register();
    this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.v2_1.ContainerMatcher.class, com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class)
    .field("retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
    "retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
    .byDefault().register();
    this.mapper = mapperFactory.getMapperFacade();
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

    // create new trigger
    List<com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger> triggerList = contextConfiguration.getTrigger();
    com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger3_0 = new com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger();
    trigger3_0.setId(trigger.getId());
    trigger3_0.setInputCharset(trigger.getInputCharset());
    trigger3_0.setType(trigger.getType());
    trigger3_0.setTemplateFolder(trigger.getTemplateFolder());

    // map containerMatcher and matcher to v.3_0
    List<com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher> v3ListM = mapper.mapAsList(trigger.getMatcher(),
    		com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher.class);
    List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher> v3ListCM = mapper.mapAsList(trigger.getContainerMatcher(),
    		com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class);
    trigger3_0.getContainerMatcher().addAll(v3ListCM);
    trigger3_0.getMatcher().addAll(v3ListM);

    // add trigger to context
    triggerList.add(trigger3_0);

    // write context.xml
    Path newContextPath = templateSetPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
    newContextPath = newContextPath.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    try{
    	Marshaller marshaller = JAXBContext.newInstance("com.devonfw.cobigen.impl.config.entity.io.v3_0").createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	marshaller.marshal(contextConfiguration, new File(newContextPath.toString()));
    }catch(JAXBException e) {
    	e.printStackTrace();
    }
    String pomArtifactID = trigger.getId();
    MavenXpp3Reader reader = new MavenXpp3Reader();
    MavenXpp3Writer writer = new MavenXpp3Writer();
    File f = new File(cobigenTemplates.toString()+"/pom.xml");
    try {
		Model mParent = reader.read(new FileInputStream(f));
		Model m = new Model();
		System.out.println(m.getArtifactId());
		Parent p = new Parent();
		p.setRelativePath(f.getPath());
		m.setParent(p); // relative path am besten diff bilden
		m.setArtifactId(pomArtifactID);
		m.setGroupId(p.getGroupId());
		m.setName("Hier sollt ein geeigneter Name stehen"); // TODO User Info geben oder Namen bestimmen lassen.
		m.setPomFile(new File(cobigenTemplates.toString()+"/pom2.xml"));
		writer.write(new FileOutputStream(new File(cobigenTemplates.toString()+"/pom2.xml")), m);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (XmlPullParserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    // stumpf dependencies aus der alten pom kopieren
    // stumpf alte template sets kopieren
    // alten .cobigen folder generieren lassen
    // testprojekt mit cobigen und julian fragen wie man nochmal auf die alte version kommt.

    System.out.println("Hallo");
    //TODO eine description Meldung das eine neues Description ausgewählt gefundne soll wenn es keine Description gibt.
  }
}
