package com.devonfw.cobigen.impl.exceptions;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.xml.bind.UnmarshalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.impl.util.ExceptionUtil;

/**
 * Exception handler for all the connectivity issues with the external process
 */
public class ConnectionExceptionHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionExceptionHandler.class);

    /**
     * Constructor for connection exception handling
     */
    public ConnectionExceptionHandler() {

    }

    /**
     * Method for handling the different exceptions thrown when connecting to the external process
     * @param e
     *            generic exception that was thrown
     * @return the specific exception that was thrown
     */
    public ConnectionException handle(Exception e) {

        Throwable parseCause = ExceptionUtil.getCause(e, Exception.class, UnmarshalException.class);

        String errorMessage = parseCause.getMessage();

        boolean isConnectException = parseCause.getClass().isInstance(new ConnectException());
        boolean isIOException = parseCause.getClass().isInstance(new IOException());
        boolean isMalformedURLException = parseCause.getClass().isInstance(new MalformedURLException());
        boolean isProtocolException = parseCause.getClass().isInstance(new ProtocolException());
        boolean isIllegalStateException = parseCause.getClass().isInstance(new IllegalStateException());

        if (isConnectException) {
            LOG.error(errorMessage);
        }

        if (isIOException) {
            LOG.error(errorMessage);
        }

        if (isConnectException || isIOException) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException eS) {
                eS.printStackTrace();
            }

            if (isConnectException) {
                return ConnectionException.CONNECT;
            }

            return ConnectionException.IO;
        }

        if (isMalformedURLException) {
            LOG.error(errorMessage);
            e.printStackTrace();

            return ConnectionException.MALFORMED_URL;
        }

        if (isProtocolException) {

            return ConnectionException.PROTOCOL;
        }

        if (isIllegalStateException) {

            return ConnectionException.ILLEGAL_STATE;
        }

        return ConnectionException.EXCEPTION;
    }

    /**
     * All the possible exceptions that can raise when connecting to the external process
     */
    public enum ConnectionException {
        /**
         * {@link ConnectException}
         */
        CONNECT,
        /**
         * {@link MalformedURLException}
         */
        MALFORMED_URL,
        /**
         * {@link IOException}
         */
        IO,
        /**
         * {@link ProtocolException}
         */
        PROTOCOL,
        /**
         * {@link IllegalStateException}
         */
        ILLEGAL_STATE,
        /**
         * {@link Exception}
         */
        EXCEPTION;
    }
}
