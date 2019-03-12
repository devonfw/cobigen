package com.devonfw.cobigen.impl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;

public class ProcessOutputUtil extends Thread {
    private final StringBuilder buf = new StringBuilder();

    private final BufferedReader in;

    public ProcessOutputUtil(InputStream in, String encoding) throws UnsupportedEncodingException {

        this.in = new BufferedReader(new InputStreamReader(in, encoding == null ? "UTF-8" : encoding));
        setDaemon(true);
        start();
    }

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