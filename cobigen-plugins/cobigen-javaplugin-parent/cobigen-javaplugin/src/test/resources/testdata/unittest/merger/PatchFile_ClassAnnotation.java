package com.devonfw.application.jtqj.general.dataaccess.api;

import javax.persistence.Entity;

import com.devonfw.application.jtqj.general.common.api.Visitor;

/**
 * Data access object for Visitor entities
 */
@Entity
@javax.persistence.Table(name = "Visitor")
public class VisitorEntity extends ApplicationPersistenceEntity implements Visitor {

    private static final long serialVersionUID = 1L;

}
