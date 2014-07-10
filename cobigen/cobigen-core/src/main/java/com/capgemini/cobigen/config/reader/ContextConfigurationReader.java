/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.reader;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.ContainerMatcher;
import com.capgemini.ContextConfiguration;
import com.capgemini.cobigen.config.entity.Matcher;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.entity.VariableAssignment;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
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
            JAXBContext context = JAXBContext.newInstance(ContextConfiguration.class);
            Unmarshaller unmarschaller = context.createUnmarshaller();
            Object rootNode = unmarschaller.unmarshal(file);
            if (rootNode instanceof ContextConfiguration) {
                contextNode = (ContextConfiguration) rootNode;
            } else {
                throw new InvalidConfigurationException(file,
                    "Unknown Root Node. Use \"contextConfiguration\" as root Node");
            }
        } catch (JAXBException e) {
            LOG.error("{}", "Invalid Configuration Exception", e);
            throw new InvalidConfigurationException(file, e.getMessage(), e);
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
                    .getInputCharset()), loadMatchers(t)));
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
        List<Matcher> matcher = new LinkedList<Matcher>();
        for (com.capgemini.Matcher m : trigger.getMatcher()) {
            matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m)));
        }
        for (ContainerMatcher cm : trigger.getContainerMatcher()) {
            matcher.add(new Matcher(cm.getType(), cm.getValue()));
        }
        return matcher;
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
        List<VariableAssignment> variableAssignments = new LinkedList<VariableAssignment>();
        for (com.capgemini.VariableAssignment va : matcher.getVariableAssignment()) {
            variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue()));
        }
        return variableAssignments;
    }
}
