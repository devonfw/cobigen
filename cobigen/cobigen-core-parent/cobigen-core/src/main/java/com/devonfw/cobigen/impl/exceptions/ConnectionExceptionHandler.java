package com.devonfw.cobigen.impl.exceptions;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception handler for all the connectivity issues with the external process
 */
public class ConnectionExceptionHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionExceptionHandler.class);

    /**
     * Exception message to show to the user when a connection exception is thrown
     */
    private String connectExceptionMessage;

    /**
     * Exception message to show to the user when a malformed URL exception is thrown
     */
    private String malformedURLExceptionMessage;

    /**
     * Exception message to show to the user when a IO exception is thrown
     */
    private String ioExceptionMessage;

    /**
     * Exception message to show to the user when a protocol exception is thrown
     */
    private String protocolExceptionMessage;

    /**
     * Exception message to show to the user when an illegal state exception is thrown
     */
    private String illegalStateExceptionMessage;

    /**
     * Constructor for connection exception handling
     */
    public ConnectionExceptionHandler() {
        connectExceptionMessage = "";
        malformedURLExceptionMessage = "";
        ioExceptionMessage = "";
        protocolExceptionMessage = "";

    }

    /**
     * @return connectExceptionMessage
     */
    public String getConnectExceptionMessage() {
        return connectExceptionMessage;
    }

    /**
     * @param connectExceptionMessage
     *            message to throw when a new connection exception raised
     */
    public void setConnectExceptionMessage(String connectExceptionMessage) {
        this.connectExceptionMessage = connectExceptionMessage;
    }

    /**
     * @return Malformed URL exception
     */
    public String getMalformedURLExceptionMessage() {
        return malformedURLExceptionMessage;
    }

    /**
     * @param malformedURLExceptionMessage
     *            message to throw when a new malformed URL exception raised
     */
    public void setMalformedURLExceptionMessage(String malformedURLExceptionMessage) {
        this.malformedURLExceptionMessage = malformedURLExceptionMessage;
    }

    /**
     * @return IoExceptionMessage
     */
    public String getIOExceptionMessage() {
        return ioExceptionMessage;
    }

    /**
     * @param ioExceptionMessage
     *            message to throw when a new IO exception raised
     */
    public void setIOExceptionMessage(String ioExceptionMessage) {
        this.ioExceptionMessage = ioExceptionMessage;
    }

    /**
     * @return ProtocolExceptionMessage
     */
    public String getProtocolExceptionMessage() {
        return protocolExceptionMessage;
    }

    /**
     * @param protocolExceptionMessage
     *            message to throw when a new protocol exception raised
     */
    public void setProtocolExceptionMessage(String protocolExceptionMessage) {
        this.protocolExceptionMessage = protocolExceptionMessage;
    }

    /**
     * @return IllegalStateExceptionMessage
     */
    public String getIllegalStateExceptionMessage() {
        return illegalStateExceptionMessage;
    }

    /**
     * @param illegalStateExceptionMessage
     *            message to throw when a new Illegal State exception raised
     */
    public void setIllegalStateExceptionMessage(String illegalStateExceptionMessage) {
        this.illegalStateExceptionMessage = illegalStateExceptionMessage;
    }

    /**
     * Method for handling the different exceptions thrown when connecting to the external process
     * @param e
     *            generic exception that was thrown
     * @return the specific exception that was thrown
     */
    public ConnectionException handle(Exception e) {

        boolean isConnectException = e instanceof ConnectException;
        boolean isIOException = e instanceof IOException;

        if (isConnectException) {
            LOG.error(connectExceptionMessage);
        }

        if (isIOException) {
            LOG.error(ioExceptionMessage);
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

        if (e instanceof MalformedURLException) {
            LOG.error(malformedURLExceptionMessage);
            e.printStackTrace();

            return ConnectionException.MALFORMED_URL;
        }

        if (e instanceof ProtocolException) {

            return ConnectionException.PROTOCOL;
        }

        if (e instanceof IllegalStateException) {

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
