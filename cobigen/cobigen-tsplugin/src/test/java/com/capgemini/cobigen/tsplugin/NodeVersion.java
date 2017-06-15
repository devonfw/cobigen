package com.capgemini.cobigen.tsplugin;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 *
 */
public class NodeVersion {

    @Test
    public void testNode() {
        String version = new String();
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "node --version");
        builder.redirectErrorStream(true);
        Process p;
        try {
            p = builder.start();
            try (InputStreamReader rdr = new InputStreamReader(p.getInputStream());
                BufferedReader r = new BufferedReader(rdr)) {
                String line;
                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    } else {
                        version = version.concat(line);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(version.startsWith("v6"));
    }

}
