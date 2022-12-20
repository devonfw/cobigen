package com.devonfw.cobigen.impl.config.reader;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.google.common.collect.Lists;

/** The {@link ContextConfigurationReader} reads the context xml */
public abstract class AbstractContextConfigurationReader {

  /** Paths of the context configuration files */
  protected List<Path> contextFiles;

  /** Root of the context configuration file, used for passing to ContextConfiguration */
  protected Path contextRoot;

  /** Map with XML Nodes 'context' of the context.xml files */
  protected Map<Path, ContextConfiguration> contextConfigurations;

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given context file
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public AbstractContextConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    if (configRoot == null) {
      throw new IllegalArgumentException("Configuration path cannot be null.");
    }
  }

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given context file
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public AbstractContextConfigurationReader() throws InvalidConfigurationException {

  }

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  abstract public Map<String, Trigger> loadTriggers();

  /**
   * Loads all {@link Matcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  protected List<Matcher> loadMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<Matcher> matcher = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.Matcher m : trigger.getMatcher()) {
      matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m), m.getAccumulationType()));
    }
    return matcher;
  }

  /**
   * Loads all {@link ContainerMatcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param t {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  protected List<ContainerMatcher> loadContainerMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger t) {

    List<ContainerMatcher> containerMatchers = Lists.newLinkedList();
    for (com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher cm : t.getContainerMatcher()) {
      containerMatchers.add(new ContainerMatcher(cm.getType(), cm.getValue(), cm.isRetrieveObjectsRecursively()));
    }
    return containerMatchers;
  }

  /**
   * Loads all {@link VariableAssignment}s from a given {@link com.devonfw.cobigen.impl.config.entity.io.Matcher}
   *
   * @param m {@link com.devonfw.cobigen.impl.config.entity.io.Matcher} to retrieve the {@link VariableAssignment} from
   * @return the {@link List} of {@link Matcher}s
   */
  protected List<VariableAssignment> loadVariableAssignments(com.devonfw.cobigen.impl.config.entity.io.Matcher m) {

    List<VariableAssignment> variableAssignments = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.VariableAssignment va : m.getVariableAssignment()) {
      variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue(), va.isMandatory()));
    }
    return variableAssignments;
  }

  /**
   * @return the path of the context file
   */
  public Path getContextRoot() {

    return this.contextRoot;
  }

  /**
   * @return the list of the context files
   */
  public List<Path> getContextFiles() {

    return this.contextFiles;
  }

}