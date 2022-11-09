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
    private Integer integerValue;

    @OneToOne
    private ReferenceEntity refEntity;

    @Enumerated(EnumType.STRING)
    @Column(length = 420, name = "ENUM_TEST_FIELD_NAME_OVERRIDE")
    private EnumForTest enumForTest;

    private List<ReferenceEntity> referenceEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }


    public void setIntegerValue(Integer value) {
        this.integerValue = value;
    }

    @OneToMany(mappedBy = "I_SHALL_BE_SKIPPED")
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
