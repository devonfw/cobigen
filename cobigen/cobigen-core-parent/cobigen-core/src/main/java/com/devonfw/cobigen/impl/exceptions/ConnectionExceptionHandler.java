package com.devonfw.cobigen.impl.exceptions;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionExceptionHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionExceptionHandler.class);

    private String connectExceptionMessage;

    private String malformedURLExceptionMessage;

    private String ioExceptionMessage;

    private String protocolExceptionMessage;

    public ConnectionExceptionHandler() {
        connectExceptionMessage = "";
        malformedURLExceptionMessage = "";
        ioExceptionMessage = "";
        protocolExceptionMessage = "";

    }

    public String getConnectExceptionMessage() {
        return connectExceptionMessage;
    }

    public ConnectionExceptionHandler setConnectExceptionMessage(String connectExceptionMessage) {
        this.connectExceptionMessage = connectExceptionMessage;

        return this;
    }

    public String getMalformedURLExceptionMessage() {
        return malformedURLExceptionMessage;
    }

    public ConnectionExceptionHandler setMalformedURLExceptionMessage(String malformedURLExceptionMessage) {
        this.malformedURLExceptionMessage = malformedURLExceptionMessage;

        return this;
    }

    public String getIOExceptionMessage() {
        return ioExceptionMessage;
    }

    public ConnectionExceptionHandler setIOExceptionMessage(String ioExceptionMessage) {
        this.ioExceptionMessage = ioExceptionMessage;

        return this;
    }

    public String getProtocolExceptionMessage() {
        return protocolExceptionMessage;
    }

    public ConnectionExceptionHandler setProtocolExceptionMessage(String protocolExceptionMessage) {
        this.protocolExceptionMessage = protocolExceptionMessage;

        return this;
    }

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

        return ConnectionException.EXCEPTION;
    }

    public enum ConnectionException {
        CONNECT, MALFORMED_URL, IO, PROTOCOL, EXCEPTION;
    }
}
