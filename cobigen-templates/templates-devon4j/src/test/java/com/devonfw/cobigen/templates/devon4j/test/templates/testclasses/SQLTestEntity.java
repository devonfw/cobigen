package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SQLTEST")
public class SQLTestEntity {
    @Id
    @Column(name = "MY_ID_FIELD")
    private Long id;

    @Column(name = "VALUENAME")
    private Integer value;

    @OneToOne(mappedBy = "I_am_mapped!!_and_should_be_skipped!")
    private ReferenceEntity refEntity;

    @Enumerated(EnumType.STRING)
    @Column(length = 420, name = "YES_EXCACTLY")
    private EnumForTest enumForTest;

    private List<ReferenceEntity> referenceEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }


    public void setValue(Integer value) {
        this.value = value;
    }

    @OneToMany
    @JoinColumn(name = "reference_entity_id", unique = true, nullable = false)
    public List<ReferenceEntity> getReferenceEntities() {
        return referenceEntities;
    }

    public void setReferenceEntities(List<ReferenceEntity> referenceEntities) {
        this.referenceEntities = referenceEntities;
    }


    public ReferenceEntity getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(ReferenceEntity refEntity) {
        this.refEntity = refEntity;
    }

    public EnumForTest getEnumForTest() {
        return enumForTest;
    }

    public void setEnumForTest(EnumForTest enumForTest) {
        this.enumForTest = enumForTest;
    }
}
