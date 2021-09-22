package com.devonfw.cobigen.templates.devon4j.utils;

import java.util.Random;

/**
 * Generates random ID's necessary for Sencha Architect project files
 */
public class IDGenerator {

  /**
   * The ID for the model file
   */
  private Object modelId;

  /**
   * The ID for the view file
   */
  private Object viewId;

  /**
   * The ID for the controller file
   */
  private Object controllerId;

  /**
   * The ID for the VC file
   */
  private Object viewControllerId;

  /**
   * The ID for the VM file
   */
  private Object viewModelId;

  /**
   * The constructor
   */
  public IDGenerator() {

    setModelId(generateId());
    setViewId(generateId());
    setControllerId(generateId());
    setViewControllerId(generateId());
    setViewModelId(generateId());
  }

  @SuppressWarnings("javadoc")
  public Object getModelId() {

    return this.modelId;
  }

  @SuppressWarnings("javadoc")
  public void setModelId(Object modelId) {

    this.modelId = modelId;
  }

  @SuppressWarnings("javadoc")
  public Object getViewId() {

    return this.viewId;
  }

  @SuppressWarnings("javadoc")
  public void setViewId(Object viewId) {

    this.viewId = viewId;
  }

  @SuppressWarnings("javadoc")
  public Object getControllerId() {

    return this.controllerId;
  }

  @SuppressWarnings("javadoc")
  public void setControllerId(Object controllerId) {

    this.controllerId = controllerId;
  }

  /**
   * Generates random strings following the 8-4-4-4-12 pattern
   *
   * @return the generated string
   */
  private Object generateId() {

    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    while (sb.length() < 32) {
      sb.append(Integer.toHexString(random.nextInt()));
    }
    String id = sb.toString();
    String resultId = id.substring(0, 8) + '-' + id.substring(8, 12) + '-' + id.substring(12, 16) + '-'
        + id.substring(16, 20) + '-' + id.substring(20, 32);
    return resultId;
  }

  @SuppressWarnings("javadoc")
  public Object getViewControllerId() {

    return this.viewControllerId;
  }

  @SuppressWarnings("javadoc")
  public void setViewControllerId(Object viewControllerId) {

    this.viewControllerId = viewControllerId;
  }

  @SuppressWarnings("javadoc")
  public Object getViewModelId() {

    return this.viewModelId;
  }

  @SuppressWarnings("javadoc")
  public void setViewModelId(Object viewModelId) {

    this.viewModelId = viewModelId;
  }
}
