package com.capgemini.cobigen.swaggerplugin.matcher;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.VariableAssignmentTo;

/**
 * 
 */
public class SwaggerMatcher implements MatcherInterpreter {

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
