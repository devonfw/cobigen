package com.capgemini.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.capgemini.cobigen.impl.config.constant.MavenMetadata;
import com.capgemini.cobigen.impl.config.entity.ContainerMatcher;
import com.capgemini.cobigen.impl.config.entity.Matcher;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.VariableAssignment;
import com.capgemini.cobigen.impl.config.entity.io.ContextConfiguration;
import com.capgemini.cobigen.impl.config.versioning.VersionValidator;
import com.capgemini.cobigen.impl.config.versioning.VersionValidator.Type;
import com.capgemini.cobigen.impl.util.ExceptionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** The {@link ContextConfigurationReader} reads the context xml */
public class ContextConfigurationReader {

    /** XML Node 'context' of the context.xml */
    private ContextConfiguration contextNode;

    /**
     * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given
     * context file
     *
     * @param configRoot
     *            root directory of the configuration
     * @throws InvalidConfigurationException
     *             if the configuration is not valid against its xsd specification
     */
    public ContextConfigurationReader(Path configRoot) throws InvalidConfigurationException {

        Path contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
        String filePath = contextFile.toAbsolutePath().toString();

        try {
            Unmarshaller unmarschaller = JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();

            // Unmarshal without schema checks for getting the version attribute of the root node.
            // This is necessary to provide an automatic upgrade client later on
            Object rootNode = unmarschaller.unmarshal(Files.newInputStream(contextFile));
            if (rootNode instanceof ContextConfiguration) {
                BigDecimal configVersion = ((ContextConfiguration) rootNode).getVersion();
                if (configVersion == null) {
                    throw new InvalidConfigurationException(filePath,
                        "The required 'version' attribute of node \"contextConfiguration\" has not been set");
                } else {
                    VersionValidator validator =
                        new VersionValidator(Type.CONTEXT_CONFIGURATION, MavenMetadata.VERSION);
                    validator.validate(configVersion.floatValue());
                }
            } else {
                throw new InvalidConfigurationException(filePath,
                    "Unknown Root Node. Use \"contextConfiguration\" as root Node");
            }

            // If we reach this point, the configuration version and root node has been validated.
            // Unmarshal with schema checks for checking the correctness and give the user more hints to
            // correct his failures
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ContextConfigurationVersion latestConfigurationVersion = ContextConfigurationVersion.getLatest();
            try (
                InputStream schemaStream = getClass()
                    .getResourceAsStream("/schema/" + latestConfigurationVersion + "/contextConfiguration.xsd");
                InputStream configInputStream = Files.newInputStream(contextFile)) {

                Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
                unmarschaller.setSchema(schema);
                rootNode = unmarschaller.unmarshal(configInputStream);
                contextNode = (ContextConfiguration) rootNode;
            }
        } catch (JAXBException e) {
            // try getting SAXParseException for better error handling and user support
            Throwable parseCause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
            String message = "";
            if (parseCause != null && parseCause.getMessage() != null) {
                message = parseCause.getMessage();
            }

            throw new InvalidConfigurationException(filePath, "Could not parse configuration file:\n" + message, e);
        } catch (SAXException e) {
            // Should never occur. Programming error.
            throw new IllegalStateException(
                "Could not parse context configuration schema. Please state this as a bug.");
        } catch (NumberFormatException e) {
            // The version number is currently the only xml value which will be parsed to a number data type
            // So provide help
            throw new InvalidConfigurationException(
                "Invalid version number defined. The version of the context configuration should consist of 'major.minor' version.");
        } catch (IOException e) {
            throw new InvalidConfigurationException(contextFile.toUri().toString(),
                "Could not read context configuration file.", e);
        }

    }

    /**
     * Loads all {@link Trigger}s of the static context into the local representation
     *
     * @return a {@link List} containing all the {@link Trigger}s
     */
    public Map<String, Trigger> loadTriggers() {

        Map<String, Trigger> triggers = Maps.newHashMap();
        for (com.capgemini.cobigen.impl.config.entity.io.Trigger t : contextNode.getTrigger()) {
            triggers.put(t.getId(), new Trigger(t.getId(), t.getType(), t.getTemplateFolder(),
                Charset.forName(t.getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
        }
        return triggers;
    }

    /**
     * Loads all {@link Matcher}s of a given {@link com.capgemini.cobigen.impl.config.entity.io.Trigger}
     *
     * @param trigger
     *            {@link com.capgemini.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s
     *            from
     * @return the {@link List} of {@link Matcher}s
     */
    private List<Matcher> loadMatchers(com.capgemini.cobigen.impl.config.entity.io.Trigger trigger) {

        List<Matcher> matcher = new LinkedList<>();
        for (com.capgemini.cobigen.impl.config.entity.io.Matcher m : trigger.getMatcher()) {
            matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m), m.getAccumulationType()));
        }
        return matcher;
    }

    /**
     * Loads all {@link ContainerMatcher}s of a given
     * {@link com.capgemini.cobigen.impl.config.entity.io.Trigger}
     *
     * @param trigger
     *            {@link com.capgemini.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s
     *            from
     * @return the {@link List} of {@link Matcher}s
     */
    private List<ContainerMatcher> loadContainerMatchers(com.capgemini.cobigen.impl.config.entity.io.Trigger trigger) {

        List<ContainerMatcher> containerMatchers = Lists.newLinkedList();
        for (com.capgemini.cobigen.impl.config.entity.io.ContainerMatcher cm : trigger.getContainerMatcher()) {
            containerMatchers.add(new ContainerMatcher(cm.getType(), cm.getValue(), cm.isRetrieveObjectsRecursively()));
        }
        return containerMatchers;
    }

    /**
     * Loads all {@link VariableAssignment}s from a given
     * {@link com.capgemini.cobigen.impl.config.entity.io.Matcher}
     *
     * @param matcher
     *            {@link com.capgemini.cobigen.impl.config.entity.io.Matcher} to retrieve the
     *            {@link VariableAssignment} from
     * @return the {@link List} of {@link Matcher}s
     */
    private List<VariableAssignment> loadVariableAssignments(
        com.capgemini.cobigen.impl.config.entity.io.Matcher matcher) {

        List<VariableAssignment> variableAssignments = new LinkedList<>();
        for (com.capgemini.cobigen.impl.config.entity.io.VariableAssignment va : matcher.getVariableAssignment()) {
            variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue()));
        }
        return variableAssignments;
    }
}
