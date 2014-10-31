package com.capgemini.cobigen.exceptions;

/**
 * This exception states that the provided templates or context configuration is not compatible with the
 * current version of CobiGen
 * 
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class IncompatibleConfigurationException extends RuntimeException {

    /**
     * Default serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * Creates a new {@link IncompatibleConfigurationException} with the given error message
     * 
     * @param msg
     *            error message
     */
    public IncompatibleConfigurationException(String msg) {

        super(msg);
    }

}
