package com.capgemini.cobigen.impl.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * The {@link AbstractHandler} provides the ability of holding the target object for any
 * {@link InvocationHandler} implementation implementing the {@link AbstractHandler}.
 */
public abstract class AbstractHandler implements InvocationHandler {

    /** The target object. */
    private Object targetObject;

    /**
     * Sets the target object.
     *
     * @param targetObject
     *            the new target object
     */
    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    /**
     * Gets the target object.
     *
     * @return the target object
     */
    public Object getTargetObject() {
        return targetObject;
    }
}