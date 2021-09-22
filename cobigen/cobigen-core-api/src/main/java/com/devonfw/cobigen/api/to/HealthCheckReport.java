package com.devonfw.cobigen.api.to;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.devonfw.cobigen.api.HealthCheck;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This is the report created while running the {@link HealthCheck}. It contains information such as thrown exceptions
 * or the current configuration and its version. All important information about the HealthCheck can be accessed from
 * here.
 */
public class HealthCheckReport {

  /**
   * A list of strings containing the names of the expected templates configuration files
   */
  private List<String> expectedTemplatesConfigurations = Lists.newArrayList();

  /**
   * A set of strings containing the names of the current templates configuration files
   */
  private Set<String> hasConfiguration = Sets.newHashSet();

  /**
   * A set of strings containing the names of the configuration files that are accessible
   */
  private Set<String> isAccessible = Sets.newHashSet();

  /**
   * A map mapping strings to paths where the strings are the names of the configuration files and the paths are their
   * corresponding path where they can be found.
   */
  private Map<String, Path> upgradeableConfigurations = Maps.newHashMap();

  /**
   * A set of strings containing the names of the configuration files that are already up to date
   */
  private Set<String> upToDateConfigurations = Sets.newHashSet();

  /**
   * A string containing information about the outcome of the HealthCheck or its current status
   */
  private String healthyCheckMessage = "";

  /**
   * A list of throwable containing the errors that were thrown during the execution of the HealthCheck
   */
  private List<RuntimeException> errors = Lists.newArrayList();

  /**
   * A list of strings containing the error messages that correspond to the thrown errors
   */
  private List<String> errorMessages = Lists.newArrayList();

  /**
   * @return the list of throwable that contains the thrown errors
   */
  public List<RuntimeException> getErrors() {

    return this.errors;
  }

  /**
   * @param c the Exception class to be checked for
   *
   * @return the first Exception of the given class if existent in this HealthCheckReport or {@code null} if no
   *         Exception of this class is existent
   */
  public Exception getFirstError(Class<?> c) {

    Iterator<RuntimeException> it = this.errors.iterator();
    while (it.hasNext()) {
      if (it.next().getClass().equals(c)) {
        return it.next();
      }
    }
    return null;
  }

  /**
   * @param errorClass the {@link Exception} to be checked for
   *
   * @return {@code true} if this HealthCheckReport contains the given {@link Exception} and {@code false} otherwise
   */
  public boolean containsError(Class<?> errorClass) {

    Iterator<RuntimeException> it = this.errors.iterator();
    while (it.hasNext()) {
      if (it.next().getClass().equals(errorClass)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the number of errors that occurred while performing the HealthCheck
   */
  public int getNumberOfErrors() {

    return getErrors().size();
  }

  /**
   * @param errors the list of throwable that contains the thrown errors
   */
  public void setErrors(List<RuntimeException> errors) {

    this.errors = errors;
  }

  /**
   * @return the list that contains the error messages
   */
  public List<String> getErrorMessages() {

    return this.errorMessages;
  }

  /**
   * @param errorMessage the new error message that should be added to the list of error messages
   */
  public void addErrorMessages(String errorMessage) {

    if (this.errorMessages == null) {
      this.errorMessages = new ArrayList<>();
    }
    this.errorMessages.add(errorMessage);
  }

  /**
   * @param e the Throwable that should be added to the list of errors
   */
  public void addError(RuntimeException e) {

    this.errors.add(e);
  }

  /**
   * @return the healthyCheckMessage of this {@link HealthCheckReport}
   */
  public String getHealthyCheckMessage() {

    return this.healthyCheckMessage;
  }

  /**
   * @param healthyCheckMessage the string that should be set as healthyCheckMessage
   */
  public void setHealthyCheckMessage(String healthyCheckMessage) {

    this.healthyCheckMessage = healthyCheckMessage;
  }

  /**
   * @param input the string that should be appended to the healthyCheckMessage
   */
  public void appendToHealthyCheckMessage(String input) {

    this.healthyCheckMessage += input;
  }

  /**
   * @return the expected templates configuration
   */
  public List<String> getExpectedTemplatesConfigurations() {

    return this.expectedTemplatesConfigurations;
  }

  /**
   * @param expectedTemplatesConfigurations a list of strings containing the expected templates configuration
   */
  public void setExpectedTemplatesConfigurations(List<String> expectedTemplatesConfigurations) {

    this.expectedTemplatesConfigurations = expectedTemplatesConfigurations;
  }

  /**
   * @return the current configuration folder
   */
  public Set<String> getHasConfiguration() {

    return this.hasConfiguration;
  }

  /**
   * @param hasConfiguration a set of strings containing the current configuration
   */
  public void setHasConfiguration(Set<String> hasConfiguration) {

    this.hasConfiguration = hasConfiguration;
  }

  /**
   * @return the set of strings containing the templates that are accessible
   */
  public Set<String> getIsAccessible() {

    return this.isAccessible;
  }

  /**
   * @param isAccessible a set of string containing the templates that are accessible
   */
  public void setIsAccessible(Set<String> isAccessible) {

    this.isAccessible = isAccessible;
  }

  /**
   * @return a map mapping strings to paths containing the upgradeable configuration files' name as string and their
   *         corresponding path
   */
  public Map<String, Path> getUpgradeableConfigurations() {

    return this.upgradeableConfigurations;
  }

  /**
   * @param upgradeableConfigurations a map mapping strings to paths containing the upgradeable configuration files'
   *        name as string and their corresponding path
   */
  public void setUpgradeableConfigurations(Map<String, Path> upgradeableConfigurations) {

    this.upgradeableConfigurations = upgradeableConfigurations;
  }

  /**
   * @return the configuration files that are already up to date as a set of strings containing the names of the files
   */
  public Set<String> getUpToDateConfigurations() {

    return this.upToDateConfigurations;
  }

  /**
   * @param upToDateConfigurations a set of strings containing the names of the files that are already up to date
   */
  public void setUpToDateConfigurations(Set<String> upToDateConfigurations) {

    this.upToDateConfigurations = upToDateConfigurations;
  }

}
