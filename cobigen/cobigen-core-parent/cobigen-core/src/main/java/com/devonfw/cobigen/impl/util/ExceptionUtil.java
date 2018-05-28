package com.devonfw.cobigen.impl.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * This class provides commonly interesting exception handling methods
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class ExceptionUtil {

    /**
     * Tries to find the exception with the given type in the causing stack trace of exceptions of the given
     * exception <code>e</code>
     * @param <T>
     *            type of the throwable to be found in the exception's stack trace
     *
     * @param e
     *            super exception to retrieve the causes stack from
     * @param cause
     *            {@link Class} which should be found and retrieved
     * @return the cause with the given type if found<br>
     *         otherwise <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T getCause(Throwable e, Class<T> cause) {

        Throwable curr = e;
        while (curr != null && !cause.isAssignableFrom(curr.getClass())) {
            curr = curr.getCause();
        }
        if (curr != null) {
            return (T) curr;
        } else {
            return null;
        }
    }

    /**
     * Tries to find the exception with the given type in the causing stack trace of exceptions of the given
     * exception <code>e</code>
     *
     * @param e
     *            super exception to retrieve the causes stack from
     * @param causes
     *            {@link Class}es which should be found and retrieved in sorted with a decreasing priority to
     *            be found.
     * @return the cause with the given type if found<br>
     *         otherwise <code>null</code>
     */
    public static Throwable getCause(Throwable e, Class<?>... causes) {

        for (Class<?> cause : causes) {
            Throwable curr = e;
            while (curr != null && !cause.isAssignableFrom(curr.getClass())) {
                curr = curr.getCause();
            }
            if (curr != null) {
                // if found return as this exception has a higher priority as the upcoming
                return curr;
            }
        }
        return null;
    }

    /**
     * Invoking the target method while assuring proper exception handling.
     * @param targetObject
     *            target object to invoke the method on
     * @param method
     *            to be called.
     * @param args
     *            parameters to be passed.
     * @return the resulting object.
     */
    public static Object invokeTarget(Object targetObject, Method method, Object[] args) {
        try {
            return method.invoke(targetObject, args);
        } catch (IllegalAccessException e) {
            throw new CobiGenRuntimeException(
                "Could not access method " + method.toGenericString() + ". This is a bug!", e);
        } catch (InvocationTargetException e) {
            CobiGenRuntimeException cause = ExceptionUtil.getCause(e, CobiGenRuntimeException.class);
            if (cause != null) {
                throw cause;
            } else {
                throw new CobiGenRuntimeException("Unable to invoke " + method.toGenericString() + ".", e);
            }
        }
    }
}
