package com.capgemini.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.exception.UnknownExpressionException;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;
import com.capgemini.cobigen.impl.config.constant.MavenMetadata;
import com.capgemini.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.capgemini.cobigen.impl.config.entity.Increment;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.capgemini.cobigen.impl.config.entity.TemplateFile;
import com.capgemini.cobigen.impl.config.entity.TemplateFolder;
import com.capgemini.cobigen.impl.config.entity.TemplatePath;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.io.IncrementRef;
import com.capgemini.cobigen.impl.config.entity.io.Increments;
import com.capgemini.cobigen.impl.config.entity.io.TemplateExtension;
import com.capgemini.cobigen.impl.config.entity.io.TemplateRef;
import com.capgemini.cobigen.impl.config.entity.io.TemplateScan;
import com.capgemini.cobigen.impl.config.entity.io.TemplateScanRef;
import com.capgemini.cobigen.impl.config.entity.io.TemplateScans;
import com.capgemini.cobigen.impl.config.entity.io.Templates;
import com.capgemini.cobigen.impl.config.entity.io.TemplatesConfiguration;
import com.capgemini.cobigen.impl.config.versioning.VersionValidator;
import com.capgemini.cobigen.impl.config.versioning.VersionValidator.Type;
import com.capgemini.cobigen.impl.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.impl.extension.TemplateEngineRegistry;
import com.capgemini.cobigen.impl.util.ExceptionUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The {@link TemplatesConfigurationReader} reads the configuration xml, evaluates all key references and
 * converts the information to the working entities
 */
public class TemplatesConfigurationReader {

    /**
     * The {@link Properties#getProperty(String) name of the property} to relocate a template target folder.
     */
    private static final String PROPERTY_RELOCATE = "relocate";

    /** The syntax for the variable pointing to the current working directory (CWD) of a template. */
    private static final String VARIABLE_CWD = "${cwd}";

    /** JAXB root node of the configuration */
    private TemplatesConfiguration configNode;

    /** Configuration file */
    private Path configFilePath;

    /** {@link JXPathContext} for the configNode */
    private JXPathContext xPathContext;

    /** Cache to find all templates by name for each template scan */
    private Map<String, List<String>> templateScanTemplates = Maps.newHashMap();

    /** The top-level folder where the templates are located. */
    private TemplateFolder rootTemplateFolder;

    /**
     * Creates a new instance of the {@link TemplatesConfigurationReader} which initially parses the given
     * configuration file
     *
     * @param templatesRoot
     *            root path for the template configuration and templates
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     */
    public TemplatesConfigurationReader(Path templatesRoot) throws InvalidConfigurationException {

        rootTemplateFolder = TemplateFolder.create(templatesRoot);
        configFilePath = templatesRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
        readConfiguration();
    }

    /**
     * Returns the configured template engine to be used
     * @return the configured template engine to be used
     */
    public String getTemplateEngine() {
        return configNode.getTemplateEngine();
    }

    /**
     * Reads the templates configuration.
     */
    private void readConfiguration() {
        try {
            Unmarshaller unmarschaller = JAXBContext.newInstance(TemplatesConfiguration.class).createUnmarshaller();

            // Unmarshal without schema checks for getting the version attribute of the root node.
            // This is necessary to provide an automatic upgrade client later on
            Object rootNode = unmarschaller.unmarshal(Files.newInputStream(configFilePath));
            if (rootNode instanceof TemplatesConfiguration) {
                BigDecimal configVersion = ((TemplatesConfiguration) rootNode).getVersion();
                if (configVersion == null) {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "The required 'version' attribute of node \"templatesConfiguration\" has not been set");
                } else {
                    VersionValidator validator =
                        new VersionValidator(Type.TEMPLATES_CONFIGURATION, MavenMetadata.VERSION);
                    validator.validate(configVersion.floatValue());
                }
            } else {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "Unknown Root Node. Use \"templatesConfiguration\" as root Node");
            }

            // If we reach this point, the configuration version and root node has been validated.
            // Unmarshal with schema checks for checking the correctness and give the user more hints to
            // correct his failures
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            TemplatesConfigurationVersion latestConfigurationVersion = TemplatesConfigurationVersion.getLatest();
            try (
                InputStream schemaStream = getClass()
                    .getResourceAsStream("/schema/" + latestConfigurationVersion + "/templatesConfiguration.xsd");
                InputStream configInputStream = Files.newInputStream(configFilePath)) {

                Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
                unmarschaller.setSchema(schema);
                rootNode = unmarschaller.unmarshal(configInputStream);
                configNode = (TemplatesConfiguration) rootNode;
            }
        } catch (JAXBException e) {
            // try getting SAXParseException for better error handling and user support
            Throwable parseCause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
            String message = "";
            if (parseCause != null && parseCause.getMessage() != null) {
                message = parseCause.getMessage();
            }
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Could not parse configuration file:\n" + message, e);
        } catch (SAXException e) {
            // Should never occur. Programming error.
            throw new IllegalStateException(
                "Could not parse templates configuration schema. Please state this as a bug.");
        } catch (NumberFormatException e) {
            // The version number is currently the only xml value which will be parsed to a number data type
            // So provide help
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Invalid version number defined. The version of the templates configuration should consist of 'major.minor' version.",
                e);
        } catch (IOException e) {
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Could not read templates configuration file.", e);
        }
    }

    /**
     * Loads all templates of the static configuration into the local representation
     *
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @return the mapping of template names to the corresponding {@link Template}
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws InvalidConfigurationException
     *             if there are multiple templates with the same name
     */
    public Map<String, Template> loadTemplates(Trigger trigger)
        throws UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException {

        Map<String, Template> templates = new HashMap<>();
        Templates templatesNode = configNode.getTemplates();
        if (templatesNode != null) {
            for (com.capgemini.cobigen.impl.config.entity.io.Template t : templatesNode.getTemplate()) {
                if (templates.get(t.getName()) != null) {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "Multiple template definitions found for ref='" + t.getName() + "'");
                }
                TemplatePath child = rootTemplateFolder.navigate(t.getTemplateFile());
                if ((child == null) || (child.isFolder())) {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "no template file found for '" + t.getTemplateFile() + "'");
                }
                Template template = createTemplate((TemplateFile) child, t.getName(), t.getDestinationPath(),
                    t.getMergeStrategy(), t.getTargetCharset(), null);
                templates.put(t.getName(), template);
            }
        }

        TemplateScans templateScans = configNode.getTemplateScans();
        if (templateScans != null) {
            List<TemplateScan> scans = templateScans.getTemplateScan();
            if (scans != null) {
                for (TemplateScan scan : scans) {
                    scanTemplates(scan, templates, trigger);
                }
            }
        }

        // override existing templates with extension definitions
        Set<String> observedExtensionNames = Sets.newHashSet();
        if (templatesNode != null && templatesNode.getTemplateExtension() != null) {
            for (TemplateExtension ext : configNode.getTemplates().getTemplateExtension()) {
                // detection of duplicate templateExtensions
                if (observedExtensionNames.contains(ext.getRef())) {
                    throw new InvalidConfigurationException(
                        "Two templateExtensions declared for ref='" + ext.getRef() + "'. Don't know what to do.");
                }
                observedExtensionNames.add(ext.getRef());

                // overriding properties if defined
                if (templates.containsKey(ext.getRef())) {
                    Template template = templates.get(ext.getRef());
                    if (ext.getDestinationPath() != null) {
                        template.setUnresolvedTemplatePath(ext.getDestinationPath());
                        template.setUnresolvedTargetPath(ext.getDestinationPath());
                    }
                    if (ext.getMergeStrategy() != null) {
                        template.setMergeStrategy(ext.getMergeStrategy());
                    }
                    if (ext.getTargetCharset() != null) {
                        template.setTargetCharset(ext.getTargetCharset());
                    }
                } else {
                    throw new InvalidConfigurationException(
                        "The templateExtension with ref='" + ext.getRef() + "' does not reference any template!");
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
     */
    private void scanTemplates(TemplateScan scan, Map<String, Template> templates, Trigger trigger) {

        String templatePath = scan.getTemplatePath();
        TemplatePath templateFolder = rootTemplateFolder.navigate(templatePath);
        if ((templateFolder == null) || templateFolder.isFile()) {
            throw new InvalidConfigurationException(configFilePath.toUri().toString(), "The templatePath '"
                + templatePath + "' of templateScan with name '" + scan.getName() + "' does not describe a directory.");
        }

        if (scan.getName() != null) {
            if (templateScanTemplates.containsKey(scan.getName())) {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "Two templateScan nodes have been defined with the same @name by mistake.");
            } else {
                templateScanTemplates.put(scan.getName(), new ArrayList<String>());
            }
        }

        scanTemplates((TemplateFolder) templateFolder, "", scan, templates, trigger, Sets.<String> newHashSet());
    }

    /**
     * Recursively scans the templates specified by the given {@link TemplateScan} and adds them to the given
     * <code>templates</code> {@link Map}.
     *
     * @param templateFolder
     *            the {@link TemplateFolder} pointing to the current directory to scan.
     * @param currentPath
     *            the current path relative to the top-level directory where we started the scan.
     * @param scan
     *            is the {@link TemplateScan} configuration.
     * @param templates
     *            is the {@link Map} where to add the templates.
     * @param trigger
     *            the templates are from
     * @param observedTemplateNames
     *            observed template name during template scan. Needed for conflict detection
     */
    private void scanTemplates(TemplateFolder templateFolder, String currentPath, TemplateScan scan,
        Map<String, Template> templates, Trigger trigger, HashSet<String> observedTemplateNames) {

        String currentPathWithSlash = currentPath;
        if (!currentPathWithSlash.isEmpty()) {
            currentPathWithSlash = currentPathWithSlash + "/";
        }

        for (TemplatePath child : templateFolder.getChildren()) {
            if (child.isFolder()) {
                scanTemplates((TemplateFolder) child, currentPathWithSlash + child.getFileName(), scan, templates,
                    trigger, observedTemplateNames);
            } else {
                String templateFileName = child.getFileName();
                String templateNameWithoutExtension = stripTemplateFileending(templateFileName);

                TextTemplateEngine templateEngine = TemplateEngineRegistry.getEngine(getTemplateEngine());
                if (!StringUtils.isEmpty(templateEngine.getTemplateFileEnding())
                    && templateFileName.endsWith(templateEngine.getTemplateFileEnding())) {
                    templateNameWithoutExtension = templateFileName.substring(0,
                        templateFileName.length() - templateEngine.getTemplateFileEnding().length());
                }
                String templateName = (scan.getTemplateNamePrefix() != null ? scan.getTemplateNamePrefix() : "")
                    + templateNameWithoutExtension;
                if (observedTemplateNames.contains(templateName)) {
                    throw new InvalidConfigurationException(
                        "TemplateScan has detected two files with the same file name (" + child
                            + ") and thus with the same "
                            + "template name. Continuing would result in an indeterministic behavior.\n"
                            + "For now, multiple files with the same name are not supported to be automatically "
                            + "configured with templateScans.");
                }
                observedTemplateNames.add(templateName);
                if (!templates.containsKey(templateName)) {
                    String destinationPath = "";
                    if (!StringUtils.isEmpty(scan.getDestinationPath())) {
                        destinationPath = scan.getDestinationPath() + "/";
                    }
                    destinationPath += currentPathWithSlash + templateNameWithoutExtension;

                    String mergeStratgey = scan.getMergeStrategy();
                    Template template = createTemplate((TemplateFile) child, templateName, destinationPath,
                        mergeStratgey, scan.getTargetCharset(), scan.getTemplatePath());
                    templates.put(templateName, template);

                    if (templateScanTemplates.get(scan.getName()) != null) {
                        templateScanTemplates.get(scan.getName()).add(templateName);
                    }
                }
            }
        }
    }

    /**
     * Strips the file ending provided by the template engine from the file name.
     * @param templateFileName
     *            file name of the template
     * @return the file name without the template file ending
     */
    private String stripTemplateFileending(String templateFileName) {
        String templateNameWithoutExtension = templateFileName;
        TextTemplateEngine templateEngine = TemplateEngineRegistry.getEngine(getTemplateEngine());
        if (!StringUtils.isEmpty(templateEngine.getTemplateFileEnding())
            && templateFileName.endsWith(templateEngine.getTemplateFileEnding())) {
            templateNameWithoutExtension = templateFileName.substring(0,
                templateFileName.length() - templateEngine.getTemplateFileEnding().length());
        }
        return templateNameWithoutExtension;
    }

    /**
     * @param templateFile
     *            the {@link TemplateFile}.
     * @param templateName
     *            the {@link Template#getName() template name} (ID).
     * @param unresolvedTemplatePath
     *            the {@link Template#getUnresolvedTemplatePath() unresolved template path}.
     * @param mergeStratgey
     *            the {@link Template#getMergeStrategy() merge strategy}.
     * @param outputCharset
     *            the {@link Template#getTargetCharset() target charset}.
     * @param scanSourcePath
     *            {@link TemplateScan#getTemplatePath() root path} of the {@link TemplateScan}
     * @return the new template instance.
     */
    private Template createTemplate(TemplateFile templateFile, String templateName, String unresolvedTemplatePath,
        String mergeStratgey, String outputCharset, String scanSourcePath) {

        String unresolvedDestinationPath = unresolvedTemplatePath;
        TemplateFolder templateFolder = templateFile.getParent();
        String relocate = templateFolder.getVariables().get(PROPERTY_RELOCATE);
        if (relocate != null) {
            if (scanSourcePath != null) {
                Path destinationPath = Paths.get(scanSourcePath).relativize(templateFile.getRootRelativePath());
                unresolvedDestinationPath =
                    relocate.replace(VARIABLE_CWD, destinationPath.toString().replace("\\", "/"));
            }
        }
        return new Template(templateFile, templateName, stripTemplateFileending(unresolvedDestinationPath),
            unresolvedTemplatePath, mergeStratgey, outputCharset);
    }

    /**
     * Loads all increments of the static configuration into the local representation.
     *
     * @return the mapping of increment names to the corresponding {@link Increment}
     * @param templates
     *            {@link Map} of all templates (see
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @throws InvalidConfigurationException
     *             if there is an invalid ref attribute
     */
    public Map<String, Increment> loadIncrements(Map<String, Template> templates, Trigger trigger)
        throws InvalidConfigurationException {

        Map<String, Increment> increments = new HashMap<>();
        Increments incrementsNode = configNode.getIncrements();
        if (incrementsNode != null) {
            // Add first all increments informally be able to resolve recursive increment references
            for (com.capgemini.cobigen.impl.config.entity.io.Increment source : incrementsNode.getIncrement()) {
                if (!increments.containsKey(source.getName())) {
                    increments.put(source.getName(), new Increment(source.getName(), source.getDescription(), trigger));
                } else {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "Duplicate increment found with name='" + source.getName() + "'.");
                }
            }
            // Collect templates
            for (com.capgemini.cobigen.impl.config.entity.io.Increment p : configNode.getIncrements().getIncrement()) {
                Increment target = increments.get(p.getName());
                addAllTemplatesRecursively(target, p, templates, increments);
            }
        }
        return increments;
    }

    /**
     * Adds all templates defined within the increment and sub increments recursively.
     *
     * @param rootIncrement
     *            the {@link Increment} on which the templates should be added
     * @param current
     *            the source {@link com.capgemini.cobigen.impl.config.entity.io.Increment} from which to
     *            retrieve the data
     * @param templates
     *            {@link Map} of all templates (see
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
     * @param increments
     *            {@link Map} of all retrieved increments
     * @throws InvalidConfigurationException
     *             if there is an invalid ref attribute
     */
    private void addAllTemplatesRecursively(Increment rootIncrement,
        com.capgemini.cobigen.impl.config.entity.io.Increment current, Map<String, Template> templates,
        Map<String, Increment> increments) throws InvalidConfigurationException {

        for (TemplateRef ref : current.getTemplateRef()) {
            Template temp = templates.get(ref.getRef());
            if (temp == null) {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "No template found for ref='" + ref.getRef() + "'!");
            }
            rootIncrement.addTemplate(temp);
        }

        for (IncrementRef ref : current.getIncrementRef()) {
            Increment parentPkg = increments.get(current.getName());
            Increment childPkg = increments.get(ref.getRef());
            if (childPkg == null) {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "No increment found for ref='" + ref.getRef() + "'!");
            }
            parentPkg.addIncrementDependency(childPkg);

            com.capgemini.cobigen.impl.config.entity.io.Increment pkg = getIncrementDeclaration(ref);
            addAllTemplatesRecursively(rootIncrement, pkg, templates, increments);
        }

        for (TemplateScanRef ref : current.getTemplateScanRef()) {
            List<String> scannedTemplateNames = templateScanTemplates.get(ref.getRef());
            if (scannedTemplateNames == null) {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "No templateScan found for ref='" + ref.getRef() + "'!");
            }

            for (String scannedTemplateName : scannedTemplateNames) {
                rootIncrement.addTemplate(templates.get(scannedTemplateName));
            }
        }
    }

    /**
     * Returns the {@link com.capgemini.cobigen.impl.config.entity.io.Increment} for the given
     * {@link IncrementRef}
     *
     * @param source
     *            {@link IncrementRef}
     * @return the referenced {@link com.capgemini.cobigen.impl.config.entity.io.Increment}
     * @throws InvalidConfigurationException
     *             if there is an invalid increment ref
     */
    private com.capgemini.cobigen.impl.config.entity.io.Increment getIncrementDeclaration(IncrementRef source)
        throws InvalidConfigurationException {

        if (xPathContext == null) {
            xPathContext = JXPathContext.newContext(configNode);
        }

        // does not work any longer as name is not a NCName type any more
        // xPathContext.iterate("//increment[@name='" + source.getRef() + "']");
        Iterator<com.capgemini.cobigen.impl.config.entity.io.Increment> allNamedIncrementsIt =
            xPathContext.iterate("//increment[@name]");

        com.capgemini.cobigen.impl.config.entity.io.Increment result = null;
        while (allNamedIncrementsIt.hasNext()) {
            com.capgemini.cobigen.impl.config.entity.io.Increment currentIncrement = allNamedIncrementsIt.next();
            if (source.getRef().equals(currentIncrement.getName())) {
                if (result == null) {
                    result = currentIncrement;
                } else {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "Multiple increment definitions found for ref='" + source.getRef() + "'");
                }
            }
        }

        if (result != null) {
            return result;
        } else {
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "No increment definition found for ref='" + source.getRef() + "'");
        }
    }
}
