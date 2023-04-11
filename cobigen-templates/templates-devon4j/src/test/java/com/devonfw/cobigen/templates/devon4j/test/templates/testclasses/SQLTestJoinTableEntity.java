package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import javax.persistence.*;
import java.util.Set;

@Entity
public class SQLTestJoinTableEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "MY_AWESOME_JOINTABLE",
            joinColumns = @JoinColumn(name = "REF_ENTITY_ID", table = "REFERENCE", unique = true, referencedColumnName = "OVERRIDE_ID"),
            inverseJoinColumns = @JoinColumn
    )
    private Set<ReferenceEntity> referenceEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ReferenceEntity> getReferenceEntities() {
        return referenceEntities;
    }

    public void setReferenceEntities(Set<ReferenceEntity> referenceEntities) {
        this.referenceEntities = referenceEntities;
    }
}
