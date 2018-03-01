package com.capgemini.cobigen.systemtest.util;

import static com.capgemini.cobigen.test.matchers.CustomHamcrestMatchers.hasItemsInList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.Any.ANY;

import java.util.HashMap;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.extension.PluginRegistry;
import com.capgemini.cobigen.test.matchers.MatcherToMatcher;
import com.capgemini.cobigen.test.matchers.VariableAssignmentToMatcher;

/** A mock factory to simply setup a mocked java plug-in to enable system tests. */
public class PluginMockFactory {
    /**
     * Creates simple to debug test data, which includes only one object as input. A {@link TriggerInterpreter
     * TriggerInterpreter} will be mocked with all necessary supplier classes to mock a simple java trigger
     * interpreter. Furthermore, the mocked trigger interpreter will be directly registered in the
     * {@link PluginRegistry}.
     * @return the input for generation
     */
    @SuppressWarnings("unchecked")
    public static Object createSimpleJavaConfigurationMock() {
        // we only need any objects for inputs to have a unique object reference to affect the mocked method
        // calls as intended
        Object input = new Object() {
            @Override
            public String toString() {
                return "input object";
            }
        };

        // Pre-processing: Mocking
        TriggerInterpreter triggerInterpreter = mock(TriggerInterpreter.class);
        MatcherInterpreter matcher = mock(MatcherInterpreter.class);
        InputReader inputReader = mock(InputReader.class);

        when(triggerInterpreter.getType()).thenReturn("mockplugin");
        when(triggerInterpreter.getMatcher()).thenReturn(matcher);
        when(triggerInterpreter.getInputReader()).thenReturn(inputReader);

        when(inputReader.isValidInput(any())).thenReturn(true);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input)))))
            .thenReturn(false);
        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("package"), ANY, sameInstance(input)))))
            .thenReturn(true);

        when(matcher.matches(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input))))).thenReturn(true);

        // Simulate variable resolving of any plug-in
        HashMap<String, String> variables = new HashMap<>(3);
        variables.put("rootPackage", "com.capgemini");
        variables.put("component", "comp1");
        variables.put("detail", "");

        when(matcher.resolveVariables(argThat(new MatcherToMatcher(equalTo("fqn"), ANY, sameInstance(input))),
            argThat(hasItemsInList(
                //
                new VariableAssignmentToMatcher(equalTo("regex"), equalTo("rootPackage"), equalTo("1")),
                new VariableAssignmentToMatcher(equalTo("regex"), equalTo("entityName"), equalTo("3"))))))
                    .thenReturn(variables);

        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);

        return input;
    }

}
