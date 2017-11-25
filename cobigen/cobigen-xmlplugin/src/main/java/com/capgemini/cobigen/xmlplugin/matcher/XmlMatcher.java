package com.capgemini.cobigen.xmlplugin.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.VariableAssignmentTo;
import com.capgemini.cobigen.xmlplugin.inputreader.XmlInputReader;

/** {@link MatcherInterpreter} for XML matcher configurations. */
public class XmlMatcher implements MatcherInterpreter {

    /** Assigning logger to XmlClassMatcher */
    private static final Logger LOG = LoggerFactory.getLogger(XmlMatcher.class);

    /** Currently supported matcher types */
    private enum MatcherType {
        /** Document's root name */
        NODENAME,
        /** Should match if input is a Document */
        XPATH
    }

    /** Available variable types for the matcher */
    private enum VariableType {
        /** Constant variable assignment */
        CONSTANT,
        /** Xpath expression group assignment */
        XPATH
    }

    @Override
    public boolean matches(MatcherTo matcher) {
        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            Object target = matcher.getTarget();
            switch (matcherType) {
            case NODENAME:
                if (target instanceof Document) {
                    String documentRootName = ((Document) target).getDocumentElement().getNodeName();
                    // return documentRootName.equals(matcher.getValue());
                    return documentRootName != null && !documentRootName.equals("")
                        && matcher.getValue().matches(documentRootName);
                }
                break;
            case XPATH:
                Document targetDoc = getDoc(target, 1);
                XPath xPath = createXpathObject(target);
                String xpathExpression = matcher.getValue();
                try {
                    return ((NodeList) xPath.evaluate(xpathExpression, targetDoc, XPathConstants.NODESET))
                        .getLength() > 0;
                } catch (XPathExpressionException e) {
                    throw new CobiGenRuntimeException("Invalid XPath expression: " + xpathExpression, e);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new CobiGenRuntimeException("Matcher type " + matcher.getType() + " not registered!", e);
        }
        return false;
    }

    /**
     * Creates a new namespace aware XPath object for xpath evaluation
     * @param target
     *            the matcher target
     * @return the created {@link XPath} object
     */
    private XPath createXpathObject(Object target) {
        Document fullDoc = getDoc(target, 0);
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new NamespaceResolver(fullDoc));
        return xPath;
    }

    /**
     * Returns the document provided in the input object. Either the object is an {@link Document}, or an
     * {@link Document} array. In the latter case, the document at the preferred index is returned. As of
     * {@link XmlInputReader#getInputObjects(Object, java.nio.charset.Charset)}, the preferred index 0 points
     * at the full {@link Document} and the preferred index 1 points at the partial {@link Document} in case
     * of an array as input.
     * @param target
     *            generator input, which has to be a {@link Document} or {@link Document} array dependent on
     *            the return types of {@link XmlInputReader#getInputObjects(Object, java.nio.charset.Charset)}
     * @param preferredIndex
     *            index pointing at the document to be returned in case of {@link Document} array as target
     * @return the preferred {@link Document}
     */
    private Document getDoc(Object target, int preferredIndex) {
        Document targetDoc;
        if (target instanceof Document) {
            targetDoc = (Document) target;
        } else if (target instanceof Document[]) {
            targetDoc = ((Document[]) target)[preferredIndex];
        } else {
            throw new IllegalArgumentException(
                "Unknown input object of type " + target.getClass() + " in matcher execution.");
        }
        return targetDoc;
    }

    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
        throws InvalidConfigurationException {

        Map<String, String> resolvedVariables = new HashMap<>();
        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            switch (matcherType) {
            case NODENAME:
                for (VariableAssignmentTo va : variableAssignments) {
                    VariableType variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
                    switch (variableType) {
                    case CONSTANT:
                        resolvedVariables.put(va.getVarName(), va.getValue());
                        break;
                    case XPATH:
                        resolvedVariables.put(va.getVarName(),
                            resolveVariablesXPath(getDoc(matcher.getTarget(), 1), va.getValue()));
                        break;
                    }
                }
                return resolvedVariables;
            default:
                break;
            }
            // In case of being an XMI document
            for (VariableAssignmentTo va : variableAssignments) {
                VariableType variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
                switch (variableType) {
                case XPATH:
                    resolvedVariables.put(va.getVarName(),
                        resolveVariablesXPath(getDoc(matcher.getTarget(), 1), va.getValue()));
                    break;
                case CONSTANT:
                    resolvedVariables.put(va.getVarName(), va.getValue());
                    break;
                }

            }
            return resolvedVariables;
        } catch (IllegalArgumentException e) {
            throw new CobiGenRuntimeException(
                "Matcher or VariableAssignment type " + matcher.getType() + " not registered!", e);
        }
    }

    /**
     * Resolves the given xpath expression on the given doc.
     * @param doc
     *            target document
     * @param xpathExpression
     *            xpath expression
     * @return the text content of the first node resulting from the xpath or the empty string if the xpath
     *         results in an empty list
     */
    private String resolveVariablesXPath(Document doc, String xpathExpression) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        LOG.debug("Evaluating xpath {}", xpathExpression);
        try {
            NodeList list = (NodeList) xPath.evaluate(xpathExpression, doc, XPathConstants.NODESET);
            if (list.getLength() > 0) {
                // currently, we just allow strings as variable assignment values
                LOG.debug("... found {} nodes.", list.getLength());
                return list.item(0).getTextContent();
            } else {
                LOG.debug("... nothing found.", list.getLength());
                return ""; // we have to return empty string as of Variables restrictions of cobigen-core
            }
        } catch (XPathExpressionException e) {
            throw new CobiGenRuntimeException("Could not evaluate xpath expression " + xpathExpression, e);
        }
    }
}
