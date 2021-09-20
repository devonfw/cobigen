package io.github.maybeec.html2text;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;

/**
 *
 */
public class Converter {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(Converter.class);

    public static void main(String[] args) throws IOException {
        CommandLine commandLine = new CommandLine(new Cli());
        commandLine.registerConverter(Path.class, s -> Paths.get(s));
        int exitCode = commandLine.execute(args);
        if (exitCode == 0) {
            LOG.info("Success");
        } else {
            LOG.info("Failed");
        }
        System.exit(exitCode);
    }
}
