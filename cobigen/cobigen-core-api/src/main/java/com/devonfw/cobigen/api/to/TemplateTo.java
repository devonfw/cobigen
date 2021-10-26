package com.devonfw.cobigen.api.to;

/** The transfer object for templates */
public class TemplateTo extends GenerableArtifact {

  /** Determines the required strategy to merge the template */
  private String mergeStrategy;

  /**
   * Forces conflicting contents (In case of {@code #mergeStrategy == null}) or the whole file to be overwritten.
   */
  private boolean forceOverride;

  /**
   * Creates a new templates transfer object with the given properties
   *
   * @param id of the template
   * @param mergeStrategy merge strategy the generated sources can be merged with
   * @param triggerId Trigger ID, the template has been resolved from.
   */
  public TemplateTo(String id, String mergeStrategy, String triggerId) {

    super(id, triggerId);
    this.mergeStrategy = mergeStrategy;
  }

  /**
   * Returns the merge strategy the generated sources can be merged with
   *
   * @return the merge strategy
   */
  public String getMergeStrategy() {

    return this.mergeStrategy;
  }

  /**
   * Check if the template has been marked to be overriding.
   *
   * @return if the template has been marked to be overriding
   */
  public boolean isForceOverride() {

    return this.forceOverride;
  }

  /**
   * Set the force override property. Forces conflicting contents (In case of {@code #mergeStrategy == null} ) or the
   * whole file to be overwritten.
   *
   * @param forceOverride value to be set
   */
  public void setForceOverride(boolean forceOverride) {

    this.forceOverride = forceOverride;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (getId() == null ? 0 : getId().hashCode());
    result = prime * result + (getTriggerId() == null ? 0 : getTriggerId().hashCode());
    result = prime * result + (getMergeStrategy() == null ? 0 : getMergeStrategy().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }

    if (obj instanceof TemplateTo) {
      boolean equal = true;
      TemplateTo otherTemplate = (TemplateTo) obj;
      if (getId() != null) {
        equal = equal && getId().equals(otherTemplate.getId());
      }
      if (!equal) {
        return false;
      }

      if (getTriggerId() != null) {
        equal = equal && getTriggerId().equals(otherTemplate.getTriggerId());
      }
      if (!equal) {
        return false;
      }

      if (getMergeStrategy() != null) {
        equal = equal && getMergeStrategy().equals(otherTemplate.getMergeStrategy());
      }
      return equal;
    }
    return false;
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[id='" + getId() + "'/triggerId='" + getTriggerId() + "'/mergeStrategy="
        + getMergeStrategy() + "']";
  }
}
