package com.devonfw.cobigen.templates.devon4j.test.templates.testclasses;

import javax.persistence.*;

@Entity
public class ReferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
