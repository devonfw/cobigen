package com.capgemini.cobigen.util;

/**
 * This class provides commonly interesting exception handling methods
 * 
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class ExceptionUtil {

    /**
     * Tries to find the exception with the given type in the causing stacktrace of exceptions of the given
     * exception <code>e</code>
     * 
     * @param e
     *            super exception to retrieve the causes stack from
     * @param cause
     *            {@link Class} which should be found and retrieved
     * @return the cause with the given type if found<br>
     *         otherwise <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T getCause(Exception e, Class<T> cause) {

        Throwable curr = e;
        while (curr != null && !cause.equals(curr.getClass())) {
            curr = curr.getCause();
        }
        if (curr != null) {
            return (T) curr;
        } else {
            return null;
        }
    }
}
