package com.devonfw.cobigen.impl.config.reader;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@link ContextConfigurationReader} reads the context xml
 */
public class ContextConfigurationReader extends JaxbDeserializer {

  /**
   * Deserialized context.xml
   */
  private final com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfiguration;

  private final Path contextFile;

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given context file
   *
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  public ContextConfigurationReader(Path contextFile) throws InvalidConfigurationException {
    this.contextConfiguration = deserialize(contextFile, com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration.class, ContextConfigurationVersion.class, "contextConfiguration");
    this.contextFile = contextFile;
  }

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which already parsed the template-set.xml file
   *
   * @param contextConfiguration the {@link ContextConfiguration} provided by the template-set
   * @param templatesSetFile          root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  public ContextConfigurationReader(com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfiguration, Path templatesSetFile)
    throws InvalidConfigurationException {

    this.contextConfiguration = contextConfiguration;
    this.contextFile = templatesSetFile;
  }

  /**
   * Loads all {@link Matcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  private List<Matcher> loadMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<Matcher> matcher = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.Matcher m : trigger.getMatcher()) {
      matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m), m.getAccumulationType()));
    }
    return matcher;
  }

  /**
   * Loads all {@link ContainerMatcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  private List<ContainerMatcher> loadContainerMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<ContainerMatcher> containerMatchers = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher cm : trigger.getContainerMatcher()) {
      containerMatchers.add(new ContainerMatcher(cm.getType(), cm.getValue(), cm.isRetrieveObjectsRecursively()));
    }
    return containerMatchers;
  }

  /**
   * Loads all {@link VariableAssignment}s from a given {@link com.devonfw.cobigen.impl.config.entity.io.Matcher}
   * <p>
   * from
   *
   * @return the {@link List} of {@link Matcher}s
   */
  private List<VariableAssignment> loadVariableAssignments(com.devonfw.cobigen.impl.config.entity.io.Matcher matcher) {

    List<VariableAssignment> variableAssignments = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.VariableAssignment va : matcher.getVariableAssignment()) {
      variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue(), va.isMandatory()));
    }
    return variableAssignments;
  }

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  private Map<String, Trigger> loadTriggers() {

    Map<String, Trigger> triggers = new HashMap<>();
    for (com.devonfw.cobigen.impl.config.entity.io.Trigger t : contextConfiguration.getTrigger()) {
      triggers.put(t.getId(), new Trigger(t.getId(), t.getType(), t.getTemplateFolder(),
        Charset.forName(t.getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
    }
    return triggers;
  }

  public ContextConfiguration read() {
    return new ContextConfiguration(contextConfiguration.getVersion(), loadTriggers(), contextFile);
  }

}