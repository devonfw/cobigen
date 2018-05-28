package com.devonfw.cobigen.impl.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The {@link AbstractInterceptor} provides the ability of holding the target object for any
 * {@link InvocationHandler} implementation implementing the {@link AbstractInterceptor}.
 */
public abstract class AbstractInterceptor implements InvocationHandler {

    /** The target object. */
    private Object targetObject;

    /**
     * Checks whether the declaringClass comes with an annotation of type annotationClass
     * @param target
     *            method to check for annotation activation
     * @param annotationClass
     *            type of annotation to be looked for
     * @return {@code true}, if annotation exists in any class or super class. {@code false}, otherwise.
     */
    public boolean isActive(Method target, Class<? extends Annotation> annotationClass) {
        return target.isAnnotationPresent(annotationClass)
            || target.getDeclaringClass().isAnnotationPresent(annotationClass);
    }

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