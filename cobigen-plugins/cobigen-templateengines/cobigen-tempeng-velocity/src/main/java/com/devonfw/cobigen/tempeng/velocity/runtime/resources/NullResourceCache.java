package com.devonfw.cobigen.tempeng.velocity.runtime.resources;

import java.util.Iterator;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;

/**
 * A non-cache implementation of the ResourceCache interface.
 */
public class NullResourceCache implements ResourceCache {

  @Override
  public Iterator enumerateKeys() {

    return new Iterator<Object>() {

      @Override
      public boolean hasNext() {

        return false;
      }

      @Override
      public Object next() {

        return null;
      }

      @Override
      public void remove() {

      }

    };
  }

  @Override
  public Resource get(Object arg0) {

    return null;
  }

  @Override
  public void initialize(RuntimeServices arg0) {

  }

  @Override
  public Resource put(Object arg0, Resource arg1) {

    return null;
  }

  @Override
  public Resource remove(Object arg0) {

    return null;
  }

}
