package com.capgemini.cobigen.jsonplugin.matcher;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.VariableAssignmentTo;
import com.capgemini.cobigen.impl.exceptions.InvalidConfigurationException;

/**
 *
 * @author rudiazma (Sep 22, 2016)
 */
public class JSONMatcher implements MatcherInterpreter {

    @Override
    public boolean matches(MatcherTo matcher) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
        throws InvalidConfigurationException {
        // TODO Auto-generated method stub
        return null;
    }

}
