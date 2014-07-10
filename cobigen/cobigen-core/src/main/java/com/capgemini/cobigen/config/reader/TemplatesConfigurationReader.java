/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.reader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.IncrementRef;
import com.capgemini.Increments;
import com.capgemini.TemplateRef;
import com.capgemini.TemplatesConfiguration;
import com.capgemini.cobigen.config.entity.Increment;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.resolver.PathExpressionResolver;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;

/**
 * The {@link TemplatesConfigurationReader} reads the configuration xml, evaluates all key references and
 * converts the information to the working entities
 * 
 * @author mbrunnli (11.03.2013)
 */
public class TemplatesConfigurationReader {

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
            JAXBContext context = JAXBContext.newInstance(TemplatesConfiguration.class);
            Unmarshaller unmarschaller = context.createUnmarshaller();
            configNode = (TemplatesConfiguration) unmarschaller.unmarshal(file);
        } catch (JAXBException e) { // TODO there should be a failure thrown
                                    // when parsing is not valid... e.g.
                                    // false naming of <increments> node
            LOG.error("Could not parse the templates.xml configuration.", e);
            throw new InvalidConfigurationException(file, e.getMessage(), e);
        }

    }

    /**
     * Loads all templates of the static configuration into the local representation
     * 
     * @param variables
     *            Map of settings reference
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @return the mapping of template id's to the corresponding {@link Template}
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws InvalidConfigurationException
     *             if there are multiple templates with the same id
     * @author mbrunnli (06.02.2013) edited by trippl (07.03.2013)
     */
    public Map<String, Template> loadTemplates(Trigger trigger, Map<String, String> variables)
        throws UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException {

        Map<String, Template> templates = new HashMap<String, Template>();
        PathExpressionResolver expressionResolver = new PathExpressionResolver(variables);

        for (com.capgemini.Template t : configNode.getTemplates().getTemplate()) {
            expressionResolver.checkExpressions(t.getDestinationPath());
            if (templates.get(t.getId()) != null) {
                throw new InvalidConfigurationException(configFile,
                    "Multiple template definitions found for idRef='" + t.getId() + "'");
            }
            templates.put(
                t.getId(),
                new Template(t.getId(), t.getDestinationPath(), t.getTemplateFile(), t.getMergeStrategy(), t
                    .getTargetCharset(), expressionResolver, trigger));
        }
        return templates;
    }

    /**
     * Loads all increments of the static configuration into the local representation
     * 
     * @return the mapping of increment id's to the corresponding {@link Increment}
     * @param templates
     *            {@link Map} of all templates (see
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger, Map)}
     * @param trigger
     *            {@link Trigger} for which the templates should be loaded
     * @throws InvalidConfigurationException
     *             if there is an invalid idref attribute
     * @author trippl (25.02.2013)
     */
    public Map<String, Increment> loadIncrements(Map<String, Template> templates, Trigger trigger)
        throws InvalidConfigurationException {
        Map<String, Increment> generationIncrements = new HashMap<String, Increment>();
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
     *            {@link TemplatesConfigurationReader#loadTemplates(Trigger, Map)}
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
        @SuppressWarnings("unchecked")
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
