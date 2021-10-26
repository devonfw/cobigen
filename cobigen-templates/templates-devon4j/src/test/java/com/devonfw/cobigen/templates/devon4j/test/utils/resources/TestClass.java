package com.devonfw.cobigen.templates.devon4j.test.utils.resources;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;

import com.devonfw.cobigen.templates.devon4j.test.utils.resources.dataaccess.api.DeepEntity;

@SuppressWarnings("javadoc")
public class TestClass {
  private int primitive;

  private int[] primitiveArray;

  private Integer boxed;

  private String object;

  private String[] objectArray;

  private TestEntity entity;

  private List<TestEntity> entitys;

  private Set<TestEntity> setEntitys;

  private DeepEntity deepEntity;

  @GET
  public String methodWithReturnType(String one, @PathParam("id") int two) {

    return null;
  }

  public void methodWithVoidReturnType(boolean one) {

  }

  public void noParameters() {

  }

}