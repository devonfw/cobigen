package com.devonfw.cobigen.impl.config.reader;

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

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.UnknownExpressionException;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.TemplatesConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.TemplateFile;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.TemplatePath;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.IncrementRef;
import com.devonfw.cobigen.impl.config.entity.io.Increments;
import com.devonfw.cobigen.impl.config.entity.io.TemplateExtension;
import com.devonfw.cobigen.impl.config.entity.io.TemplateRef;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScan;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScanRef;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScans;
import com.devonfw.cobigen.impl.config.entity.io.Templates;
import com.devonfw.cobigen.impl.config.entity.io.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;
import com.devonfw.cobigen.impl.extension.TemplateEngineRegistry;
import com.devonfw.cobigen.impl.util.ExceptionUtil;
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

    /** The {@link ConfigurationHolder} used for reading templates folder **/
    private ConfigurationHolder configurationHolder;

    /**
     * Creates a new instance of the {@link TemplatesConfigurationReader} which initially parses the given
     * configuration file without a ConfigurationFolder
     *
     * @param projectRoot
     *            root path for the templates, has to be an absolute path
     * @param templateFolder
     *            name of the folder containing the configuration and templates, has to be a relative path
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     */
    public TemplatesConfigurationReader(Path projectRoot, String templateFolder) {
        this(projectRoot, templateFolder, null);
    }

    /**
     * Creates a new instance of the {@link TemplatesConfigurationReader} which initially parses the given
     * configuration file
     *
     * @param projectRoot
     *            root path for the templates, has to be an absolute path
     * @param templateFolder
     *            name of the folder containing the configuration and templates, has to be a relative path
     * @param configurationHolder
     *            The {@link ConfigurationHolder} used for reading templates folder
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     */
    public TemplatesConfigurationReader(Path projectRoot, String templateFolder,
        ConfigurationHolder configurationHolder) throws InvalidConfigurationException {
        Path templateLocation;

        Path rootTemplatePath = projectRoot.resolve(templateFolder);
        configFilePath = rootTemplatePath.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);

        if (!Files.exists(configFilePath)) {
            Path sourceTemplatePath = projectRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
            sourceTemplatePath = sourceTemplatePath.resolve(templateFolder);
            configFilePath = sourceTemplatePath.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
            templateLocation = sourceTemplatePath;

            if (!Files.exists(configFilePath)) {
                throw new InvalidConfigurationException(configFilePath, "Could not find templates configuration file.");
            }
        } else {
            templateLocation = rootTemplatePath;
        }
        rootTemplateFolder = TemplateFolder.create(templateLocation);

        readConfiguration();
        this.configurationHolder = configurationHolder;
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

        // workaround to make JAXB work in OSGi context by
        // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
        final ClassLoader orig = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());

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
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
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
            for (com.devonfw.cobigen.impl.config.entity.io.Template t : templatesNode.getTemplate()) {
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
                // The relative template path has to be specifically parsed to string and back to a path so
                // the templateFile and scanSourcePath are using the same file system. More info can be found
                // at https://github.com/devonfw/cobigen/issues/715
                String templateFilePath = templateFile.getRootRelativePath().toString();
                Path destinationPath = Paths.get(scanSourcePath).relativize(Paths.get(templateFilePath));
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
            for (com.devonfw.cobigen.impl.config.entity.io.Increment source : incrementsNode.getIncrement()) {
                if (!increments.containsKey(source.getName())) {
                    increments.put(source.getName(), new Increment(source.getName(), source.getDescription(), trigger));
                } else {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "Duplicate increment found with name='" + source.getName() + "'.");
                }
            }
            // Collect templates
            for (com.devonfw.cobigen.impl.config.entity.io.Increment p : configNode.getIncrements().getIncrement()) {
                Increment target = increments.get(p.getName());
                addAllTemplatesRecursively(target, p, templates, increments);
            }
        }
        return increments;
    }

    /**
     * Loads an specific increment of the static configuration into the local representation. The return
     * object must be a map because maybe this increment references other increments
     *
     * @return the mapping of increment names to the corresponding {@link Increment}
     * @param templates
     *            {@link Map} of all templates (see
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @param incrementName
     *            the increment to search
     * @throws InvalidConfigurationException
     *             if there is an invalid ref attribute
     */
    public Map<String, Increment> loadSpecificIncrement(Map<String, Template> templates, Trigger trigger,
        String incrementName) throws InvalidConfigurationException {

        Map<String, Increment> increments = new HashMap<>();
        Increments incrementsNode = configNode.getIncrements();
        if (incrementsNode != null) {
            // We only add the specific increment we want
            com.devonfw.cobigen.impl.config.entity.io.Increment source =
                getSpecificIncrement(incrementsNode.getIncrement(), incrementName);
            if (source == null) {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "No increment found with name='" + incrementName + "' on the external templates.xml folder.");
            }

            increments.put(source.getName(), new Increment(source.getName(), source.getDescription(), trigger));

            // Collect templates for our specific increment
            Increment target = increments.get(source.getName());
            addAllTemplatesRecursively(target, source, templates, increments);
        }
        return increments;
    }

    /**
     * Adds all templates defined within the increment and sub increments recursively.
     *
     * @param rootIncrement
     *            the {@link Increment} on which the templates should be added
     * @param current
     *            the source {@link com.devonfw.cobigen.impl.config.entity.io.Increment} from which to
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
        com.devonfw.cobigen.impl.config.entity.io.Increment current, Map<String, Template> templates,
        Map<String, Increment> increments) throws InvalidConfigurationException {
        for (TemplateRef ref : current.getTemplateRef()) {
            Template temp = templates.get(ref.getRef());
            if (temp == null) {
                if (isExternalRef(ref.getRef())) {
                    rootIncrement.addTemplate(loadExternalTemplate(ref));
                } else {
                    throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                        "No template found for ref='" + ref.getRef() + "'!");
                }
            } else {
                rootIncrement.addTemplate(temp);
            }
        }

        for (IncrementRef ref : current.getIncrementRef()) {
            Increment parentPkg = increments.get(current.getName());
            Increment childPkg = increments.get(ref.getRef());

            if (childPkg == null) {

                // We try to find the increment inside our templates.xml file
                Increments incrementsNode = configNode.getIncrements();
                com.devonfw.cobigen.impl.config.entity.io.Increment source = null;
                if (incrementsNode != null) {
                    // We only add the specific increment we want
                    source = getSpecificIncrement(incrementsNode.getIncrement(), ref.getRef());
                    if (source != null) {
                        addAllTemplatesRecursively(rootIncrement, source, templates, increments);
                    }
                    // We have not found the increment inside our templates.xml file, now let's see if this
                    // incrementRef contains "::". That would mean we have to search on another folder.
                    else if (isExternalRef(ref.getRef())) {
                        parentPkg.addIncrementDependency(loadExternalIncrement(ref));
                    } else {
                        throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                            "No increment found for ref='" + ref.getRef() + "'!");
                    }
                }

            } else {
                parentPkg.addIncrementDependency(childPkg);

                com.devonfw.cobigen.impl.config.entity.io.Increment pkg = getIncrementDeclaration(ref);
                addAllTemplatesRecursively(rootIncrement, pkg, templates, increments);
            }
        }

        for (TemplateScanRef ref : current.getTemplateScanRef()) {
            List<String> scannedTemplateNames = templateScanTemplates.get(ref.getRef());
            if (scannedTemplateNames == null) {
                throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                    "No templateScan found for ref='" + ref.getRef() + "'!");
            } else {
                for (String scannedTemplateName : scannedTemplateNames) {
                    rootIncrement.addTemplate(templates.get(scannedTemplateName));
                }
            }
        }
    }

    /**
     * Tries to load an external template, returning the reference template
     * @param ref
     *            The reference to the template
     * @return the referenced template
     */
    private Template loadExternalTemplate(TemplateRef ref) {
        String[] split = splitExternalRef(ref.getRef());
        String refTrigger = split[0];
        String refTemplate = split[1];

        com.devonfw.cobigen.impl.config.TemplatesConfiguration externalTemplatesConfiguration =
            loadExternalConfig(refTrigger);

        Template template = externalTemplatesConfiguration.getTemplate(refTemplate);

        if (template == null) {
            throw new InvalidConfigurationException("No Template found for ref=" + ref.getRef());
        }

        return template;
    }

    /**
     * Tries to load an external increment. It loads the trigger of the external increment and all its
     * increments for finding the needed one
     * @param ref
     *            incrementRef to load and store on the root increment
     * @return the referenced child increment
     */
    private Increment loadExternalIncrement(IncrementRef ref) {
        Increment childPkg;
        String[] split = splitExternalRef(ref.getRef());
        String refTrigger = split[0];
        String refIncrement = split[1];

        com.devonfw.cobigen.impl.config.TemplatesConfiguration externalTemplatesConfiguration =
            loadExternalConfig(refTrigger);

        Map<String, Increment> externalIncrements = externalTemplatesConfiguration.getIncrements();

        childPkg = externalIncrements.get(refIncrement);

        if (childPkg == null) {
            throw new InvalidConfigurationException("No Increment found for ref=" + ref.getRef());
        }

        return childPkg;
    }

    /**
     * Returns the TemplatesConfiguration file corresponding to the given trigger
     * @param refTrigger
     *            The trigger by which the TemplatesConfiguration shoul be searched
     * @return The TemplatesConfiguration corresponding to the trigger
     */
    private com.devonfw.cobigen.impl.config.TemplatesConfiguration loadExternalConfig(String refTrigger) {

        Trigger extTrigger = getExternalTrigger(refTrigger);
        return configurationHolder.readTemplatesConfiguration(extTrigger);
    }

    /**
     * Returns the {@link com.devonfw.cobigen.impl.config.entity.io.Increment} for the given
     * {@link IncrementRef}
     *
     * @param source
     *            {@link IncrementRef}
     * @return the referenced {@link com.devonfw.cobigen.impl.config.entity.io.Increment}
     * @throws InvalidConfigurationException
     *             if there is an invalid increment ref
     */
    private com.devonfw.cobigen.impl.config.entity.io.Increment getIncrementDeclaration(IncrementRef source)
        throws InvalidConfigurationException {

        if (xPathContext == null) {
            xPathContext = JXPathContext.newContext(configNode);
        }

        // does not work any longer as name is not a NCName type any more
        // xPathContext.iterate("//increment[@name='" + source.getRef() + "']");
        Iterator<com.devonfw.cobigen.impl.config.entity.io.Increment> allNamedIncrementsIt =
            xPathContext.iterate("//increment[@name]");

        String incrementToSearch = source.getRef();
        // Check whether we have an external incrementRef
        if (isExternalRef(incrementToSearch)) {
            String[] splitted = splitExternalRef(source.getRef());
            incrementToSearch = splitted[1];
        }

        com.devonfw.cobigen.impl.config.entity.io.Increment result = null;
        while (allNamedIncrementsIt.hasNext()) {
            com.devonfw.cobigen.impl.config.entity.io.Increment currentIncrement = allNamedIncrementsIt.next();
            if (incrementToSearch.equals(currentIncrement.getName())) {
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

    /**
     * External ref's syntax is "triggerName::incrementName" . Therefore, this method splits the ref using
     * "::" as the delimiter so that we can get both strings separately.
     * @param ref
     *            the increment ref to split
     * @return an string array that will contain 2 elements
     */
    private String[] splitExternalRef(String ref) {
        String[] split = ref.split(ConfigurationConstants.REFERENCE_DELIMITER);
        if (split.length != 2) {
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Invalid external ref for ref='" + ref + "', it should be 'nameOfFolder::nameOfIncrement'!");
        }
        return split;
    }

    /**
     * Checks whether this increment ref is an external increment ref
     * @param ref
     *            the increment ref to check
     * @return true if it is an external IncrementRef
     */
    private boolean isExternalRef(String ref) {
        return ref.contains(ConfigurationConstants.REFERENCE_DELIMITER);
    }

    /**
     * Tries to read the context.xml file for finding and returning an external trigger
     * @param triggerToSearch
     *            string containing the name of the trigger to search
     * @return the found external trigger
     */
    private Trigger getExternalTrigger(String triggerToSearch) {
        ContextConfigurationReader contextConfigurationReader =
            new ContextConfigurationReader(configurationHolder.readContextConfiguration().getConfigurationPath());
        Map<String, Trigger> triggers = contextConfigurationReader.loadTriggers();
        Trigger trig = triggers.get(triggerToSearch);
        if (trig == null) {
            throw new InvalidConfigurationException(configFilePath.toUri().toString(),
                "Invalid external ref, no trigger '" + triggerToSearch + "' was found on your context.xml!");
        }
        return trig;
    }

    /**
     * Tries to find an increment on a list of increments and return it
     * @param increment
     *            list of increments
     * @param ref
     *            name of the increment to get
     * @return Increment if it was found, null if no increment with that name was found
     */
    public com.devonfw.cobigen.impl.config.entity.io.Increment getSpecificIncrement(
        List<com.devonfw.cobigen.impl.config.entity.io.Increment> increment, String ref) {
        for (com.devonfw.cobigen.impl.config.entity.io.Increment inc : increment) {
            if (inc.getName().equals(ref)) {
                return inc;
            }
        }
        return null;
    }

}
