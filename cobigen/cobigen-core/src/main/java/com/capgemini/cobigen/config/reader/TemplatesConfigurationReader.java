/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.reader;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.capgemini.TemplateRef;
import com.capgemini.TemplateScan;
import com.capgemini.TemplateScans;
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
     * XML Node 'configuration' of the configuration.xml
     */
    private TemplatesConfiguration configNode;

    /**
     * Configuration file
     */
    private File configFile;

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
     * @param file
     *            configuration file
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     * @author mbrunnli (11.03.2013)
     */
    public TemplatesConfigurationReader(File file) throws InvalidConfigurationException {

        configFile = file;

        try {
            Unmarshaller unmarschaller =
                JAXBContext.newInstance(TemplatesConfiguration.class).createUnmarshaller();

            // Unmarshal without schema checks for getting the version attribute of the root node.
            // This is necessary to provide an automatic upgrade client later on
            Object rootNode = unmarschaller.unmarshal(file);
            if (rootNode instanceof TemplatesConfiguration) {
                BigDecimal configVersion = ((TemplatesConfiguration) rootNode).getVersion();
                if (configVersion == null) {
                    throw new InvalidConfigurationException(file,
                        "The required 'version' attribute of node \"templatesConfiguration\" has not been set");
                } else {
                    VersionValidator.validateTemplatesConfig(configVersion);
                }
            } else {
                throw new InvalidConfigurationException(file,
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
            rootNode = unmarschaller.unmarshal(file);
            configNode = (TemplatesConfiguration) rootNode;
        } catch (JAXBException e) {
            LOG.error("Could not parse configuration file {}", file.getPath(), e);
            // try getting SAXParseException for better error handling and user support
            SAXParseException parseCause = ExceptionUtil.getCause(e, SAXParseException.class);
            String message = null;
            if (parseCause != null) {
                message = parseCause.getMessage();
            }
            throw new InvalidConfigurationException(file, "Could not parse configuration file:\n" + message,
                e);
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
                "Invalid version number defined. The version of the templates configuration should consist of 'major.minor' version.");
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
        for (com.capgemini.Template t : configNode.getTemplates().getTemplate()) {
            if (templates.get(t.getId()) != null) {
                throw new InvalidConfigurationException(configFile,
                    "Multiple template definitions found for idRef='" + t.getId() + "'");
            }
            templates.put(
                t.getId(),
                new Template(t.getId(), t.getDestinationPath(), t.getTemplateFile(), t.getMergeStrategy(), t
                    .getTargetCharset(), trigger, triggerInterpreter));
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

        String templateFolder = scan.getTemplatePath();
        String path = configFile.getParent() + "/" + templateFolder;
        File folder = new File(path);
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Folder does not exist: " + path);
        }
        scanTemplates(folder, "", scan, templates, trigger, triggerInterpreter);
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
     */
    private void scanTemplates(File currentDirectory, String currentPath, TemplateScan scan,
        Map<String, Template> templates, Trigger trigger, ITriggerInterpreter triggerInterpreter) {

        String currentPathWithSlash = currentPath;
        if (!currentPathWithSlash.isEmpty()) {
            currentPathWithSlash = currentPathWithSlash + "/";
        }
        for (File child : currentDirectory.listFiles()) {
            if (child.isDirectory()) {
                scanTemplates(child, currentPathWithSlash + child.getName(), scan, templates, trigger,
                    triggerInterpreter);
            } else {
                String templateName = child.getName();
                if (templateName.endsWith(TEMPLATE_EXTENSION)) {
                    String templateNameWithoutExtension =
                        templateName.substring(0, templateName.length() - TEMPLATE_EXTENSION.length());
                    String templateId = scan.getTemplateIdPrefix() + templateNameWithoutExtension;
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
                    throw new InvalidConfigurationException(configFile, "No Template found for idRef='"
                        + tRef.getIdref() + "'");
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
            throw new InvalidConfigurationException(configFile, "No increment definition found for idRef='"
                + source.getIdref() + "'");
        case 1:
            return p;
        default:
            throw new InvalidConfigurationException(configFile,
                "Multiple increment definitions found for idRef='" + source.getIdref() + "'");
        }

        // XmlCursor cursor = source.newCursor();
        // cursor
        // .selectPath("declare namespace s='http://capgemini.com'; /s:templatesConfiguration/s:increments/s:increment[@id='"
        // + source.getIdref() + "']");
        //
        // if (cursor.getSelectionCount() == 0) {
        // throw new InvalidConfigurationException(configFile,
        // "No increment definition found for idRef='"
        // + source.getIdref() + "'");
        // } else if (cursor.getSelectionCount() > 1) {
        // throw new InvalidConfigurationException(configFile,
        // "Multiple increment definitions found for idRef='" +
        // source.getIdref() + "'");
        // }
        //
        // if (cursor.toNextSelection()) {
        // XmlObject node = cursor.getObject();
        // if (node instanceof com.capgemini.Increment) {
        // return (com.capgemini.Increment) node;
        // }
        // }
    }
}
