package com.capgemini.cobigen.xmlplugin.matcher;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.to.MatcherTo;
import com.capgemini.cobigen.extension.to.VariableAssignmentTo;

/**
 *
 * @author fkreis (18.11.2014)
 */
public class XmlMatcher implements IMatcher {

    /**
     * {@inheritDoc}
     * @author fkreis (18.11.2014)
     */
    @Override
    public boolean matches(MatcherTo matcher) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (18.11.2014)
     */
    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher,
        List<VariableAssignmentTo> variableAssignments) throws InvalidConfigurationException {
        // TODO Auto-generated method stub
        return null;
    }

}
