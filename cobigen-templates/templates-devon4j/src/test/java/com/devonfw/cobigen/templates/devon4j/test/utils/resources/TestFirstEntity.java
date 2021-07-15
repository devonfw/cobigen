package com.devonfw.cobigen.templates.devon4j.test.utils.resources;

import javax.persistence.Table;

/**
 *
 */
@Table(name = "my_table")
public class TestFirstEntity extends TestParentClass {
    private String firstName;
}
