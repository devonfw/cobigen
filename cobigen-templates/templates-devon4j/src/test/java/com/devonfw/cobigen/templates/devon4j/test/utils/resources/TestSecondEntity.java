package com.devonfw.cobigen.templates.devon4j.test.utils.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 */
@Entity
public class TestSecondEntity {
    @Id
    @Column(name = "self_assigned_name")
    private String id;
}
