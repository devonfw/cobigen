package com.devonfw.application.jtqj.general.dataaccess.api;

import com.devonfw.application.jtqj.general.common.api.Visitor;

@javax.persistence.Table(name = "Visits")
public class VisitorEntity extends ApplicationPersistenceEntity implements Visitor {

    private String username;

    private String name;

    private static final long serialVersionUID = 1L;

}
