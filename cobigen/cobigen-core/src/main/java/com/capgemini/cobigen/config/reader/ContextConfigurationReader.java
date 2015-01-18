package com.capgemini.cobigen.config.reader;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.capgemini.ContextConfiguration;
import com.capgemini.cobigen.config.entity.ContainerMatcher;
import com.capgemini.cobigen.config.entity.Matcher;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.entity.VariableAssignment;
import com.capgemini.cobigen.config.versioning.VersionValidator;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.util.ExceptionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The {@link ContextConfigurationReader} reads the context xml
 *
 * @author trippl (04.04.2013)
 */
public class ContextConfigurationReader {

    /**
     * XML Node 'context' of the context.xml
     */
    private ContextConfiguration contextNode;

    /**
     * Assigning logger to ContextConfigurationReader
     */
    private static final Logger LOG = LoggerFactory.getLogger(ContextConfigurationReader.class);

    /**
     * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given
     * context file
     *
     * @param file
     *            context file
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     * @author trippl (04.04.2013)
     */
    public ContextConfigurationReader(File file) throws InvalidConfigurationException {

        try {
            Unmarshaller unmarschaller =
                JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();

            // Unmarshal without schema checks for getting the version attribute of the root node.
            // This is necessary to provide an automatic upgrade client later on
            Object rootNode = unmarschaller.unmarshal(file);
            if (rootNode instanceof ContextConfiguration) {
                BigDecimal configVersion = ((ContextConfiguration) rootNode).getVersion();
                if (configVersion == null) {
                    throw new InvalidConfigurationException(file,
                        "The required 'version' attribute of node \"contextConfiguration\" has not been set");
                } else {
                    VersionValidator.validateContextConfig(configVersion);
                }
            } else {
                throw new InvalidConfigurationException(file,
                    "Unknown Root Node. Use \"contextConfiguration\" as root Node");
            }

            // If we reach this point, the configuration version and root node has been validated.
            // Unmarshal with schema checks for checking the correctness and give the user more hints to
            // correct his
            // failures
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema =
                schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream(
                    "/schema/contextConfiguration.xsd")));
            unmarschaller.setSchema(schema);
            rootNode = unmarschaller.unmarshal(file);
            contextNode = (ContextConfiguration) rootNode;
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
            LOG.error("Could not parse context configuration schema.", e);
            throw new IllegalStateException(
                "Could not parse context configuration schema. Please state this as a bug.");
        } catch (NumberFormatException e) {
            // The version number is currently the only xml value which will be parsed to a number data type
            // So provide help
            LOG.error("Invalid version number for context configuration defined.", e);
            throw new InvalidConfigurationException(
                "Invalid version number defined. The version of the context configuration should consist of 'major.minor' version.");
        }

    }

    /**
     * Loads all {@link Trigger}s of the static context into the local representation
     *
     * @return a {@link List} containing all the {@link Trigger}s
     * @author trippl (04.04.2013)
     */
    public Map<String, Trigger> loadTriggers() {

        Map<String, Trigger> triggers = Maps.newHashMap();
        for (com.capgemini.Trigger t : contextNode.getTriggers().getTrigger()) {
            triggers.put(
                t.getId(),
                new Trigger(t.getId(), t.getType(), t.getTemplateFolder(), Charset.forName(t
                    .getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
        }
        return triggers;
    }

    /**
     * Loads all {@link Matcher}s of a given {@link com.capgemini.Trigger}
     *
     * @param trigger
     *            {@link com.capgemini.Trigger} to retrieve the {@link Matcher}s from
     * @return the {@link List} of {@link Matcher}s
     * @author mbrunnli (08.04.2014)
     */
    private List<Matcher> loadMatchers(com.capgemini.Trigger trigger) {

        List<Matcher> matcher = new LinkedList<>();
        for (com.capgemini.Matcher m : trigger.getMatcher()) {
            matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m)));
        }
        return matcher;
    }

    /**
     * Loads all {@link ContainerMatcher}s of a given {@link com.capgemini.Trigger}
     *
     * @param trigger
     *            {@link com.capgemini.Trigger} to retrieve the {@link Matcher}s from
     * @return the {@link List} of {@link Matcher}s
     * @author mbrunnli (13.10.2014)
     */
    private List<ContainerMatcher> loadContainerMatchers(com.capgemini.Trigger trigger) {

        List<ContainerMatcher> containerMatchers = Lists.newLinkedList();
        for (com.capgemini.ContainerMatcher cm : trigger.getContainerMatcher()) {
            containerMatchers.add(new ContainerMatcher(cm.getType(), cm.getValue(), cm
                .isRetrieveObjectsRecursively()));
        }
        return containerMatchers;
    }

    /**
     * Loads all {@link VariableAssignment}s from a given {@link com.capgemini.Matcher}
     *
     * @param matcher
     *            {@link com.capgemini.Matcher} to retrieve the {@link VariableAssignment} from
     * @return the {@link List} of {@link Matcher}s
     * @author mbrunnli (08.04.2014)
     */
    private List<VariableAssignment> loadVariableAssignments(com.capgemini.Matcher matcher) {

        List<VariableAssignment> variableAssignments = new LinkedList<>();
        for (com.capgemini.VariableAssignment va : matcher.getVariableAssignment()) {
            variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue()));
        }
        return variableAssignments;
    }
}
