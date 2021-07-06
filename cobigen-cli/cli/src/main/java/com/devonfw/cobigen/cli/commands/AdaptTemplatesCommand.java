package com.devonfw.cobigen.cli.commands;

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

    @Override
    public Integer doAction() throws Exception {

        CobiGenFactory.extractTemplates();
        return 0;
    }
}
