package com.devonfw.cobigen.impl.config.upgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Link;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Links;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Tag;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.Tags;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Upgrader for the templates from v2_1 to v3_0 that splits the monolithic
 * template structure into version 3.0 with template sets
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
				.field("retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
						"retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
				.byDefault().register();
		this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.Trigger.class,
				com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger.class).byDefault().register();
		this.mapperFactory.classMap(com.devonfw.cobigen.impl.config.entity.io.Matcher.class,
				com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher.class).byDefault().register();
		this.mapperFactory
				.classMap(com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher.class,
						com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class)
				.field("retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}",
						"retrieveObjectsRecursively:{isRetrieveObjectsRecursively|setRetrieveObjectsRecursively(new Boolean(%s))|type=java.lang.Boolean}")
				.byDefault().register();
		this.mapper = mapperFactory.getMapperFacade();
	}


	/**
	 * Upgrades the ContextConfiguration from v2.1 to the new structure from v3.0. The
	 * monolithic pom and context files will be split into multiple files
	 * corresponding to every template set that will be created.
	 *
	 * @param {@link Path} Path to the context.xml that will be upgraded
	 * @return {@link Map} collection that contains the upgraded v3.0 {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration}
	 * 			as key and a {@link Path} for the new location of the context.xml as value
	 * @throws Exception
	 */
	public Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> upgradeTemplatesToTemplateSets(
			Path contextLocation) throws Exception {
		Path context = analyseStructure(contextLocation);
		ContextConfiguration contextConfiguration = getContextConfiguration(context);

		Path cobigenDir = context;
		while (!cobigenDir.endsWith(ConfigurationConstants.COBIGEN_CONFIG_FILE)) {
			cobigenDir = cobigenDir.getParent();
		}

		Path templateSets = Files.createDirectory(cobigenDir.resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER));
		Path adapted = Files.createDirectory(templateSets.resolve(ConfigurationConstants.ADAPTED_FOLDER));
		Path cobigenTemplates = cobigenDir.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
				.resolve(ConfigurationConstants.COBIGEN_TEMPLATES);
		Path templates = cobigenTemplates.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);

		List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration> contextFiles = splitContext(
				contextConfiguration);
		Map<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration, Path> contextMap = new HashMap<>();
		for (com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration cc : contextFiles) {
			for (com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger : cc.getTrigger()) {
				Path triggerFolder = templates.resolve(trigger.getTemplateFolder());
				Path newTriggerFolder = adapted.resolve(trigger.getTemplateFolder());
				Path utilsPath = cobigenTemplates.resolve("src/main/java");
				try {
					FileUtils.copyDirectory(triggerFolder.toFile(),
							newTriggerFolder.resolve("src/main/resources").toFile());
				} catch (Exception e) {
					LOG.error("Could not copy template Folder with the Error" + e);
					throw e;
				}
				try {
					FileUtils.copyDirectory(utilsPath.toFile(), newTriggerFolder.resolve("src/main/java").toFile());
				} catch (Exception e) {
					LOG.error("Could not copy utlis Folder with the Error" + e);
					throw e;
				}

				Path newContextPath = newTriggerFolder.resolve("src/main/resources")
						.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
				contextMap.put(cc, newContextPath);

				this.writeNewPomFile(cobigenTemplates, newTriggerFolder, trigger);
			}
		}

		// backup of old files
		Path backupFolder = cobigenDir.resolve("backup");
		File f = backupFolder.toFile();
		if (!f.exists()) {
			f.mkdir();
		}
		try {
			FileUtils.moveDirectoryToDirectory(cobigenTemplates.getParent().toFile(), f, false);
		} catch (IOException e) {
			LOG.error("Error copying and deleting the old template files", e);
			throw e;
		}
		return contextMap;

	}

	/**
	 * Writes a pom.xml file for the splitted context and template folder
	 *
	 * @param {@link Path}cobigen_templates Path to the CobiGen_Templates folder
	 * @param {@link Path}newTemplateFolder Path to the splitted template folder
	 * @param {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger }trigger
	 *               to the related template folder
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void writeNewPomFile(Path cobigen_templates, Path newTemplateFolder,
			com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger) throws IOException, XmlPullParserException {
		// Pom.xml creation
		MavenXpp3Reader reader = new MavenXpp3Reader();
		MavenXpp3Writer writer = new MavenXpp3Writer();
		Model mMonolithicPom;
		try(FileInputStream pomInputStream = new FileInputStream(cobigen_templates.resolve("pom.xml").toFile());) {
			mMonolithicPom = reader.read(pomInputStream);
		} catch (FileNotFoundException e) {
			LOG.error("Monolitic pom file could not be found", e);
			throw e;
		} catch (IOException e) {
			LOG.error("IOError while reading the monolithic pom file", e);
			throw e;
		} catch (XmlPullParserException e) {
			LOG.error("XMLError while parsing the monolitic pom file", e);
			throw e;
		}
		Model m = new Model();
		Parent p = new Parent();
		p.setArtifactId(mMonolithicPom.getArtifactId());
		p.setGroupId(mMonolithicPom.getGroupId());
		p.setVersion(mMonolithicPom.getVersion());
		m.setParent(p);
		m.setDependencies(mMonolithicPom.getDependencies());
		m.setArtifactId(trigger.getId().replace('_', '-'));
		m.setName("PLACEHOLDER---Replace this text with a correct template name---PLACEHOLDER");
		try(FileOutputStream pomOutputStream = new FileOutputStream(newTemplateFolder.resolve("pom.xml").toFile());) {
			writer.write(new FileOutputStream(newTemplateFolder.resolve("pom.xml").toFile()), m);
		} catch (FileNotFoundException e) {
			LOG.error("Error while creating the new v3_0 pom file", e);
			throw e;
		} catch (IOException e) {
			LOG.error("IOError while writing the new v3_0 pom file", e);
			throw e;
		}

	}

	/**
	 * Splits a contextConfiguration and converts a {@link Trigger} and his data to
	 * a v3_0 Trigger
	 *
	 * @param {@link ContextConfiguration}the monolithic context that will be
	 *               splitted
	 * @return {@link com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration}
	 *         List of the splitted contextConfiguration files
	 */
	private List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration> splitContext(
			ContextConfiguration monolitic) {
		List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration> splittedContexts = new ArrayList<>();
		List<Trigger> triggerList = monolitic.getTrigger();
		for (Trigger trigger : triggerList) {
			com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration contextConfiguration3_0 = new com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration();
			com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger trigger3_0 = new com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger();
			trigger3_0.setId(trigger.getId());
			trigger3_0.setInputCharset(trigger.getInputCharset());
			trigger3_0.setType(trigger.getType());
			trigger3_0.setTemplateFolder(trigger.getTemplateFolder());

			List<com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher> v3MList = mapper
					.mapAsList(trigger.getMatcher(), com.devonfw.cobigen.impl.config.entity.io.v3_0.Matcher.class);
			List<com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher> v3CMList = mapper.mapAsList(
					trigger.getContainerMatcher(),
					com.devonfw.cobigen.impl.config.entity.io.v3_0.ContainerMatcher.class);
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
			splittedContexts.add(contextConfiguration3_0);
		}
		return splittedContexts;
	}

	/**
	 * Locates the context file
	 *
	 * @param {@link Path} to the contextFile
	 * @return {@link File} of the contextFile
	 */
	private Path analyseStructure(Path contextP) throws Exception {

		if (contextP == null) {
			throw new Exception("Templates location cannot be null!");
		}
		// check if context exits here
		File context = contextP.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME).toFile();
		if (context.exists()) {
			LOG.info("Found Context File");
		} else {
			if (contextP.endsWith(ConfigurationConstants.COBIGEN_TEMPLATES)) {
				context = contextP.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
						.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME).toFile();
				LOG.info("Found Context File");
			} else if (contextP.endsWith(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)) {
				context = contextP.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
						.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
						.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME).toFile();
				LOG.info("Found Context File");
			} else {
				context = ConfigurationConstants.DEFAULT_HOME.resolve(ConfigurationConstants.TEMPLATES_FOLDER)
						.resolve(ConfigurationConstants.COBIGEN_TEMPLATES)
						.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
						.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME).toFile();
				LOG.info("Found Context File");
			}
		}
		if (!context.exists()) {
			throw new FileNotFoundException("Context.xml could not be found");
		}
		return context.toPath();
	}

	/**
	 * Returns the correct context file
	 *
	 * @param {@link Path} to the contextFile
	 * @return {@link ContextConfiguration}
	 */
	private ContextConfiguration getContextConfiguration(Path contextFile) {

		try (InputStream in = Files.newInputStream(contextFile)) {
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
