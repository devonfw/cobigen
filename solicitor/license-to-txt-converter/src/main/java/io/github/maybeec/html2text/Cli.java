package io.github.maybeec.html2text;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "html2txt", aliases = { "h2t" })
public class Cli implements Callable<Integer> {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(Cli.class);

    @Option(names = { "--html-escape", "-h" }, negatable = true,
        description = "Escape < and > characters for use of inner HTML")
    boolean escapeHtml = false;

    @Parameters(index = "1", description = "Output file. Will be re-created if exists.")
    Path txtFile;

    @Parameters(index = "0", description = "HTML input file to be converted to txt")
    Path htmlFile;

    @Override
    public Integer call() throws Exception {
        LOG.info("Convert {} to {}{}", htmlFile, txtFile,
            escapeHtml ? " with HTML escaping" : " without HTML escaping");

        String content = new HtmlProcessor().process(htmlFile, escapeHtml);

        content = content.replace((char) 12, ' '); // nodepad++ FF char to be removed

        if (Files.exists(txtFile)) {
            Files.delete(txtFile);
        }
        Files.write(txtFile, content.getBytes());
        return null;
    }
}
