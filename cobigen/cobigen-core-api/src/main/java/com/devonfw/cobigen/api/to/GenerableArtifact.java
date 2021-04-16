package com.devonfw.cobigen.api.to;

/**
 * An generation artifact like a {@link TemplateTo template} or an {@link IncrementTo increment} to used for
 * processing the generation.
 */
public class GenerableArtifact implements Comparable<GenerableArtifact> {

    /** Id of the template */
    private String id;

    /**
     * Initializes the {@link GenerableArtifact} with the given ID
     * @param id
     *            ID
     */
    GenerableArtifact(String id) {
        this.id = id;
    }

    /**
     * Returns the id
     * @return the id
     */
    public String getId() {
        return id;
    }

    @Override
    public int compareTo(GenerableArtifact o) {
        return id.compareTo(o.id);
    }

}
