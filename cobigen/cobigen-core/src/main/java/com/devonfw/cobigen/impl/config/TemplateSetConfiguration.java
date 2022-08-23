package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;

/**
 * mdukhan This Class is used to set specific properties if not found, or save them if correctly found. These properties
 * are groupIds, allowSnapshots and hideTemplates.
 */
public class TemplateSetConfiguration {

  /** variable for template-set artifacts */
  private List<String> groupIds;

  /** allow snapshots of template-sets */
  private boolean allowSnapshots;

  /** variable to hide very specific template sets or versions of template sets */
  private List<String> hideTemplates;

  /**
   * All available {@link Trigger}s
   */
  private Map<String, Trigger> triggers;

  /**
   * All available {@Link Template}s
   */
  private Map<String, Template> templates;

  /**
   * All available {@Link Increment}
   */
  private Map<String, Increment> increments;

  /**
   * Path of the configuration. Might point to a folder or a jar or maybe even something different in future.
   */
  private Path configurationPath;

  /**
   * The reader to read the template-set.xml files
   */
  private TemplateSetConfigurationReader templateSetConfigurationReader;

  /**
   * The constructor. load properties from a given source
   *
   * @param groupIds
   * @param allowSnapshots
   * @param hideTemplates
   */
  public TemplateSetConfiguration(List<String> groupIds, boolean allowSnapshots, List<String> hideTemplates) {

    super();
    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = hideTemplates;
  }

  /**
   * Reads the configuration from the given path
   *
   * @param configRoot CobiGen configuration root path
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */
  private void readConfiguration(Path configRoot) throws InvalidConfigurationException {

    // TODO: Let the ConfigurationFactory take care of this
    if (this.templateSetConfigurationReader == null) {
      this.templateSetConfigurationReader = new TemplateSetConfigurationReader(configRoot);
    }

    // TODO: How do we get the template?
    // Fix this this.configurationPath = this.templateSetConfigurationReader.getContextRoot();
    this.triggers = this.templateSetConfigurationReader.loadTriggers();
    this.templates = this.templateSetConfigurationReader.loadTemplates(null);
    this.increments = this.templateSetConfigurationReader.loadIncrements(this.templates, null);
  }

  /**
   * return a list of the saved groupIds
   *
   * @return groupIds
   */
  public List<String> getGroupIds() {

    return this.groupIds;
  }

  /**
   * set a list of the groupIds from a source
   *
   * @param groupIds new value of {@link #getgroupIds}.
   */
  public void setGroupIds(List<String> groupIds) {

    this.groupIds = groupIds;
  }

  /**
   * return a boolean which states if specific Snapshots should be allowed.
   *
   * @return allowSnapshots
   */
  public boolean isAllowSnapshots() {

    return this.allowSnapshots;
  }

  /**
   * set a value on the snapshot
   *
   * @param allowSnapshots new value of {@link #getallowSnapshots}.
   */
  public void setAllowSnapshots(boolean allowSnapshots) {

    this.allowSnapshots = allowSnapshots;
  }

  /**
   * return a list of the saved templates to be hidden
   *
   * @return hideTemplates
   */
  public List<String> getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * set a list of the HideTemplate from a source
   *
   * @param hideTemplates new value of {@link #gethideTemplates}.
   */
  public void setHideTemplates(List<String> hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

}