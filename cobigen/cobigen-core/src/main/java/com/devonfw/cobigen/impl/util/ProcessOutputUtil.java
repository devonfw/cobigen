package com.devonfw.cobigen.impl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;

/**
 * Util class for handling the output errors of the external process
 */
public class ProcessOutputUtil extends Thread {

    /**
     * Needed for handling the output
     */
    private final StringBuilder buf = new StringBuilder();

    /**
     * Reader of the output
     */
    private final BufferedReader in;

    /**
     * Creates a new instance that handles the errors output by the external process
     * @param in
     *            input stream of the external process that will be handled for getting the errors of the
     *            external process
     * @param encoding
     *            encoding of the output
     * @throws UnsupportedEncodingException
     *             when the encoding passed does not exist
     */
    public ProcessOutputUtil(InputStream in, String encoding) throws UnsupportedEncodingException {

        this.in = new BufferedReader(new InputStreamReader(in, encoding == null ? "UTF-8" : encoding));
        setDaemon(true);
        start();
    }

    /**
     * Get the output text from the server
     * @return text output from the server
     */
    public String getText() {

        synchronized (buf) {
            return buf.toString();
        }
    }

    @Override
    public void run() {

        // Reading process output
        try {
            String s = in.readLine();
            while (s != null) {
                synchronized (buf) {
                    buf.append(s);
                    buf.append('\n');
                }
                s = in.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(ExternalProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}