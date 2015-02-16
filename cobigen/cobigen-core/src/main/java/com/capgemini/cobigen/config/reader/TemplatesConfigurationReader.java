package com.capgemini.cobigen.config.reader;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.capgemini.IncrementRef;
import com.capgemini.Increments;
import com.capgemini.TemplateExtension;
import com.capgemini.TemplateRef;
import com.capgemini.TemplateScan;
import com.capgemini.TemplateScans;
import com.capgemini.Templates;
import com.capgemini.TemplatesConfiguration;
import com.capgemini.cobigen.config.entity.Increment;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.versioning.VersionValidator;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.util.ExceptionUtil;
import com.google.common.collect.Sets;

/**
 * The {@link TemplatesConfigurationReader} reads the configuration xml, evaluates all key references and
 * converts the information to the working entities
 *
 * @author mbrunnli (11.03.2013)
 */
public class TemplatesConfigurationReader {

    /** The file extension of the template files. */
    private static final String TEMPLATE_EXTENSION = ".ftl";

    /**
     * Templates configuration file name
     */
    private static final String CONFIG_FILENAME = "templates.xml";

    /**
     * XML Node 'configuration' of the configuration.xml
     */
    private TemplatesConfiguration configNode;

    /**
     * Configuration file
     */
    private Path configFilePath;

    /**
     * {@link JXPathContext} for the configNode
     */
    private JXPathContext xPathContext;

    /**
     * Assigning logger to TemplatesConfigurationReader
     */
    private static final Logger LOG = LoggerFactory.getLogger(TemplatesConfigurationReader.class);

    /**
     * Creates a new instance of the {@link TemplatesConfigurationReader} which initially parses the given
     * configuration file
     *
     * @param templatesRoot
     *            root path for the template configuration and templates
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     * @author mbrunnli (11.03.2013)
     */
    public TemplatesConfigurationReader(Path templatesRoot) throws InvalidConfigurationException {

        configFilePath = templatesRoot.resolve(CONFIG_FILENAME);
        readConfiguration();
    }

    /**
     * Reads the templates configuration.
     * @author mbrunnli (12.02.2015)
     */
    private void readConfiguration() {
        try {
            Unmarshaller unmarschaller =
                JAXBContext.newInstance(TemplatesConfiguration.class).createUnmarshaller();

            // Unmarshal without schema checks for getting the version attribute of the root node.
            // This is necessary to provide an automatic upgrade client later on
            Object rootNode = unmarschaller.unmarshal(Files.newInputStream(configFilePath));
            if (rootNode instanceof TemplatesConfiguration) {
                BigDecimal configVersion = ((TemplatesConfiguration) rootNode).getVersion();
                if (configVersion == null) {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "The required 'version' attribute of node \"templatesConfiguration\" has not been set");
                } else {
                    VersionValidator.validateTemplatesConfig(configVersion);
                }
            } else {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "Unknown Root Node. Use \"templatesConfiguration\" as root Node");
            }

            // If we reach this point, the configuration version and root node has been validated.
            // Unmarshal with schema checks for checking the correctness and give the user more hints to
            // correct his
            // failures
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema =
                schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream(
                    "/schema/templatesConfiguration.xsd")));
            unmarschaller.setSchema(schema);
            rootNode = unmarschaller.unmarshal(Files.newInputStream(configFilePath));
            configNode = (TemplatesConfiguration) rootNode;
        } catch (JAXBException e) {
            LOG.error("Could not parse configuration file {}", configFilePath.toUri().toString(), e);
            // try getting SAXParseException for better error handling and user support
            SAXParseException parseCause = ExceptionUtil.getCause(e, SAXParseException.class);
            String message = null;
            if (parseCause != null) {
                message = parseCause.getMessage();
            }
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Could not parse configuration file:\n" + message, e);
        } catch (SAXException e) {
            // Should never occur. Programming error.
            LOG.error("Could not parse templates configuration schema.", e);
            throw new IllegalStateException(
                "Could not parse templates configuration schema. Please state this as a bug.");
        } catch (NumberFormatException e) {
            // The version number is currently the only xml value which will be parsed to a number data type
            // So provide help
            LOG.error("Invalid version number for templates configuration defined.", e);
            throw new InvalidConfigurationException(
                configFilePath.toUri().toString(),
                "Invalid version number defined. The version of the templates configuration should consist of 'major.minor' version.",
                e);
        } catch (IOException e) {
            LOG.error("Could not read templates configuration file {}", configFilePath.toUri().toString(), e);
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Could not read templates configuration file.", e);
        }
    }

    /**
     * Loads all templates of the static configuration into the local representation
     *
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @param triggerInterpreter
     *            {@link ITriggerInterpreter} the trigger has been interpreted with
     * @return the mapping of template id's to the corresponding {@link Template}
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws InvalidConfigurationException
     *             if there are multiple templates with the same id
     * @author mbrunnli (06.02.2013) edited by trippl (07.03.2013)
     */
    public Map<String, Template> loadTemplates(Trigger trigger, ITriggerInterpreter triggerInterpreter)
        throws UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException {

        Map<String, Template> templates = new HashMap<>();
        Templates templatesNode = configNode.getTemplates();
        if (templatesNode != null) {
            for (com.capgemini.Template t : templatesNode.getTemplate()) {
                if (templates.get(t.getId()) != null) {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "Multiple template definitions found for idRef='" + t.getId() + "'");
                }
                templates.put(t.getId(), new Template(t.getId(), t.getDestinationPath(), t.getTemplateFile(),
                    t.getMergeStrategy(), t.getTargetCharset(), trigger, triggerInterpreter));
            }
        }
        TemplateScans templateScans = configNode.getTemplateScans();
        if (templateScans != null) {
            List<TemplateScan> scans = templateScans.getTemplateScan();
            if (scans != null) {
                for (TemplateScan scan : scans) {
                    scanTemplates(scan, templates, trigger, triggerInterpreter);
                }
            }
        }

        // override existing templates with extension definitions
        Set<String> observedExtensionIds = Sets.newHashSet();
        if (templatesNode != null && templatesNode.getTemplateExtension() != null) {
            for (TemplateExtension ext : configNode.getTemplates().getTemplateExtension()) {
                // detection of duplicate templateExtensions
                if (observedExtensionIds.contains(ext.getIdref())) {
                    LOG.error("Two templateExtensions declared for idref='{}'.", ext.getIdref());
                    throw new InvalidConfigurationException("Two templateExtensions declared for idref='"
                        + ext.getIdref() + "'. Don't know what to do.");
                }
                observedExtensionIds.add(ext.getIdref());

                // overriding properties if defined
                if (templates.containsKey(ext.getIdref())) {
                    Template template = templates.get(ext.getIdref());
                    if (ext.getDestinationPath() != null) {
                        template.setUnresolvedDestinationPath(ext.getDestinationPath());
                    }
                    if (ext.getMergeStrategy() != null) {
                        template.setMergeStrategy(ext.getMergeStrategy());
                    }
                    if (ext.getTargetCharset() != null) {
                        template.setTargetCharset(ext.getTargetCharset());
                    }
                } else {
                    LOG.error("The templateExtension with idref='{}' does not reference any template!",
                        ext.getIdref());
                    throw new InvalidConfigurationException("The templateExtension with idref='"
                        + ext.getIdref() + "' does not reference any template!");
                }
            }
        }
        return templates;
    }

    /**
     * Scans the templates specified by the given {@link TemplateScan} and adds them to the given
     * <code>templates</code> {@link Map}.
     *
     * @param scan
     *            is the {@link TemplateScan} configuration.
     * @param templates
     *            is the {@link Map} where to add the templates.
     * @param trigger
     *            the templates are from
     * @param triggerInterpreter
     *            of the {@link Trigger}
     */
    private void scanTemplates(TemplateScan scan, Map<String, Template> templates, Trigger trigger,
        ITriggerInterpreter triggerInterpreter) {

        Path templateFolderPath = configFilePath.getParent().resolve(scan.getTemplatePath());
        if (!Files.isDirectory(templateFolderPath)) {
            throw new IllegalArgumentException("The path '" + templateFolderPath
                + "' does not describe a directory.");
        }
        scanTemplates(templateFolderPath, "", scan, templates, trigger, triggerInterpreter,
            Sets.<String> newHashSet());
    }

    /**
     * Recursively scans the templates specified by the given {@link TemplateScan} and adds them to the given
     * <code>templates</code> {@link Map}.
     *
     * @param currentDirectory
     *            the {@link File} pointing to the current directory to scan.
     * @param currentPath
     *            the current path relative to the top-level directory where we started the scan.
     * @param scan
     *            is the {@link TemplateScan} configuration.
     * @param templates
     *            is the {@link Map} where to add the templates.
     * @param trigger
     *            the templates are from
     * @param triggerInterpreter
     *            of the {@link Trigger}
     * @param observedTemplateIds
     *            observed template ids during template scan. Needed for conflict detection
     */
    private void scanTemplates(Path currentDirectory, String currentPath, TemplateScan scan,
        Map<String, Template> templates, Trigger trigger, ITriggerInterpreter triggerInterpreter,
        HashSet<String> observedTemplateIds) {

        String currentPathWithSlash = currentPath;
        if (!currentPathWithSlash.isEmpty()) {
            currentPathWithSlash = currentPathWithSlash + "/";
        }

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(currentDirectory)) {
            Iterator<Path> it = dirStream.iterator();
            while (it.hasNext()) {
                Path next = it.next();
                if (Files.isDirectory(next)) {
                    scanTemplates(next, currentPathWithSlash + next.getFileName().toString(), scan,
                        templates, trigger, triggerInterpreter, observedTemplateIds);
                } else {
                    String templateName = next.getFileName().toString();
                    String templateNameWithoutExtension = templateName;
                    if (templateName.endsWith(TEMPLATE_EXTENSION)) {
                        templateNameWithoutExtension =
                            templateName.substring(0, templateName.length() - TEMPLATE_EXTENSION.length());
                    }
                    String templateId = scan.getTemplateIdPrefix() + templateNameWithoutExtension;
                    if (observedTemplateIds.contains(templateId)) {
                        throw new InvalidConfigurationException(
                            "Template-scan has detected two files with the same file name and thus with the same "
                                + "template id. Continuing would result in an indeterministic behavior.\n"
                                + "For now, multiple files with the same name are not supported to be automatically "
                                + "configured with template-scans.");
                    }
                    observedTemplateIds.add(templateId);
                    if (!templates.containsKey(templateId)) {
                        String destinationPath =
                            scan.getDestinationPath() + "/" + currentPathWithSlash
                                + templateNameWithoutExtension;
                        String templateFile =
                            scan.getTemplatePath() + "/" + currentPathWithSlash + templateName;
                        String mergeStratgey = scan.getMergeStrategy();
                        Template template =
                            new Template(templateId, destinationPath, templateFile, mergeStratgey,
                                scan.getTargetCharset(), trigger, triggerInterpreter);
                        templates.put(templateId, template);
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Could not create directory stream for path '" + currentDirectory.toUri().toString()
                + "'. Continuing template scanning...");
        }
    }

    /**
     * Loads all increments of the static configuration into the local representation
     *
     * @return the mapping of increment id's to the corresponding {@link Increment}
     * @param templates
     *            {@link Map} of all templates (see
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger, ITriggerInterpreter)}
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @throws InvalidConfigurationException
     *             if there is an invalid idref attribute
     * @author trippl (25.02.2013)
     */
    public Map<String, Increment> loadIncrements(Map<String, Template> templates, Trigger trigger)
        throws InvalidConfigurationException {

        Map<String, Increment> generationIncrements = new HashMap<>();
        Increments increments = configNode.getIncrements();
        if (increments != null) {
            for (com.capgemini.Increment source : increments.getIncrement()) {
                generationIncrements.put(source.getId(),
                    new Increment(source.getId(), source.getDescription(), trigger));
            }
            for (com.capgemini.Increment p : configNode.getIncrements().getIncrement()) {
                Increment target = generationIncrements.get(p.getId());
                addAllTemplatesRecursively(target, p, templates, generationIncrements);
            }
        }
        return generationIncrements;
    }

    /**
     * Adds all templates defined within the increment and sub increments recursively
     *
     * @param rootTarget
     *            the {@link Increment} on which the templates should be added
     * @param current
     *            the source {@link com.capgemini.Increment} from which to retrieve the data
     * @param templates
     *            {@link Map} of all templates (see
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger, ITriggerInterpreter)}
     * @param generationIncrements
     *            {@link Map} of all retrieved increments
     * @throws InvalidConfigurationException
     *             if there is an invalid idref attribute
     * @author mbrunnli (07.03.2013)
     */
    private void addAllTemplatesRecursively(Increment rootTarget, com.capgemini.Increment current,
        Map<String, Template> templates, Map<String, Increment> generationIncrements)
        throws InvalidConfigurationException {

        for (Object ref : current.getTemplateRefOrIncrementRef()) {
            if (ref instanceof TemplateRef) {
                TemplateRef tRef = (TemplateRef) ref;
                Template temp = templates.get(tRef.getIdref());
                if (temp == null) {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "No Template found for idRef='" + tRef.getIdref() + "'");
                }
                rootTarget.addTemplate(temp);
            }
        }
        for (Object ref : current.getTemplateRefOrIncrementRef()) {
            if (ref instanceof IncrementRef) {
                IncrementRef pRef = (IncrementRef) ref;
                Increment parentPkg = generationIncrements.get(current.getId());
                Increment childPkg = generationIncrements.get(pRef.getIdref());
                parentPkg.addIncrementDependency(childPkg);

                com.capgemini.Increment pkg = getIncrementDeclaration(pRef);
                addAllTemplatesRecursively(rootTarget, pkg, templates, generationIncrements);
            }
        }
    }

    /**
     * Returns the {@link com.capgemini.Increment} for the given {@link IncrementRef}
     *
     * @param source
     *            {@link IncrementRef}
     * @return the referenced {@link com.capgemini.Increment}
     * @throws InvalidConfigurationException
     *             if there is an invalid increment idref
     * @author mbrunnli (11.03.2013)
     */
    private com.capgemini.Increment getIncrementDeclaration(IncrementRef source)
        throws InvalidConfigurationException {

        if (xPathContext == null) {
            xPathContext = JXPathContext.newContext(configNode);
        }
        // declare namespace s='http://capgemini.com';
        Iterator<com.capgemini.Increment> it =
            xPathContext.iterate("increments/increment[@id='" + source.getIdref() + "']");

        int count = 0;
        com.capgemini.Increment p = null;
        while (it.hasNext()) {
            p = it.next();
            count++;
        }

        switch (count) {
        case 0:
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "No increment definition found for idRef='" + source.getIdref() + "'");
        case 1:
            return p;
        default:
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Multiple increment definitions found for idRef='" + source.getIdref() + "'");
        }

    }
}
