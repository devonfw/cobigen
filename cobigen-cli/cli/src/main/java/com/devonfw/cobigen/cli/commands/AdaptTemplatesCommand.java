package com.devonfw.cobigen.cli.commands;

import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.ConfigurationUtil;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.cli.logger.CLILogger;
import com.devonfw.cobigen.impl.CobiGenFactory;

import ch.qos.logback.classic.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * This class handles the user defined template directory e.g. determining and obtaining the latest templates
 * jar, unpacking the sources and compiled classes and storing the custom location path in a configuration
 * file
 */
@Command(description = MessagesConstants.ADAPT_TEMPLATES_DESCRIPTION, name = "adapt-templates", aliases = { "a" },
    mixinStandardHelpOptions = true)
public class AdaptTemplatesCommand implements Callable<Integer> {

    /**
     * If this options is enabled, we will print also debug messages
     */
    @Option(names = { "--verbose", "-v" }, negatable = true, description = MessagesConstants.VERBOSE_OPTION_DESCRIPTION)
    private boolean verbose;

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            CLILogger.setLevel(Level.DEBUG);
        }

        URI templatesLocationUri = ConfigurationUtil.findTemplatesLocation();
        if (templatesLocationUri == null) {
            LOG.info(
                "CobiGen is attempting to download the latest CobiGen_Templates.jar and will extract it to cobigen home directory {}. please wait...",
                ConfigurationConstants.DEFAULT_HOME);
            Path extractTemplates = CobiGenFactory.extractTemplates();
            LOG.info("Successfully downloaded and extracted templates to @ {}", extractTemplates);
        } else {
            LOG.info("Templates already found at {}. You can edit them in place to adapt your generation results.",
                templatesLocationUri);
        }
        return 0;
    }
}
