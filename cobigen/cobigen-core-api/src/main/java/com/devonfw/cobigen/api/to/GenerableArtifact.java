package com.devonfw.cobigen.api.to;

/**
 * An generation artifact like a {@link TemplateTo template} or an {@link IncrementTo increment} to used for processing
 * the generation.
 */
public class GenerableArtifact implements Comparable<GenerableArtifact> {

  /** Id of the template */
  private String id;

  /** Trigger Id, this {@link IncrementTo} was retrieved from */
  private String triggerId;

  /**
   * Initializes the {@link GenerableArtifact} with the given ID
   *
   * @param id ID
   * @param triggerId the increment is assigned to
   */
  GenerableArtifact(String id, String triggerId) {

    this.id = id;
    this.triggerId = triggerId;
  }

  /**
   * Returns the id
   *
   * @return the id
   */
  public String getId() {

    return this.id;
  }

  /**
   * Returns the trigger's id the increment is assigned to
   *
   * @return the trigger's id the increment is assigned to
   */
  public String getTriggerId() {

    return this.triggerId;
  }

  @Override
  public int compareTo(GenerableArtifact o) {

    return this.id.compareTo(o.id);
  }
}
