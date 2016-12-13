package utils;

import java.util.Random;

public class IDGenerator {

  private Object modelId;

  private Object viewId;

  private Object controllerId;

  private Object viewControllerId;

  private Object viewModelId;

  public IDGenerator() {
    setModelId(generateId());
    setViewId(generateId());
    setControllerId(generateId());
    setViewControllerId(generateId());
    setViewModelId(generateId());
  }

  public Object getModelId() {

    return this.modelId;
  }

  public void setModelId(Object modelId) {

    this.modelId = modelId;
  }

  public Object getViewId() {

    return this.viewId;
  }

  public void setViewId(Object viewId) {

    this.viewId = viewId;
  }

  public Object getControllerId() {

    return this.controllerId;
  }

  public void setControllerId(Object controllerId) {

    this.controllerId = controllerId;
  }

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

  public Object getViewControllerId() {

    return this.viewControllerId;
  }

  public void setViewControllerId(Object viewControllerId) {

    this.viewControllerId = viewControllerId;
  }

  public Object getViewModelId() {

    return this.viewModelId;
  }

  public void setViewModelId(Object viewModelId) {

    this.viewModelId = viewModelId;
  }
}
