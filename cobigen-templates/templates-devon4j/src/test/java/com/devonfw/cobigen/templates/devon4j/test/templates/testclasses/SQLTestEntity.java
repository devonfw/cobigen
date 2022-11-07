package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import javax.persistence.*;

@Entity
@Table(name = "SQLTEST")
public class SQLTestEntity {
    @Id
    private Long id;

    @Column
    private Integer value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
