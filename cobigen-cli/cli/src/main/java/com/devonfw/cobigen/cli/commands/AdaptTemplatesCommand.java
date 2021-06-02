package com.devonfw.cobigen.cli.commands;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MessagesConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;

import picocli.CommandLine.Command;

/**
 * This class handles the user defined template directory e.g. determining and obtaining the latest templates
 * jar, unpacking the sources and compiled classes and storing the custom location path in a configuration
 * file
 */
@Command(description = MessagesConstants.ADAPT_TEMPLATES_DESCRIPTION, name = "adapt-templates", aliases = { "a" },
    mixinStandardHelpOptions = true)
public class AdaptTemplatesCommand extends CommandCommons {

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    @Override
    public Integer doAction() throws Exception {

        if (!Files.isDirectory(templatesProject)) {
            LOG.info("Template jar found at {}.");
            Path extractTemplates = CobiGenFactory.extractTemplates();
            LOG.info("Successfully downloaded and extracted templates to @ {}", extractTemplates);
        } else {
            LOG.info("Templates already found at {}. You can edit them in place to adapt your generation results.",
                templatesProject);
        }
        return 0;
    }
}
