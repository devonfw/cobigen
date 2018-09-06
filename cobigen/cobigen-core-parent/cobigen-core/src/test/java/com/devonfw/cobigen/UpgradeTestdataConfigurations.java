package com.devonfw.cobigen;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.BackupPolicy;
import com.devonfw.cobigen.impl.config.upgrade.ContextConfigurationUpgrader;
import com.devonfw.cobigen.impl.config.upgrade.TemplateConfigurationUpgrader;
import com.google.common.collect.Lists;

/**
 * Utility class to automatically migrate test configurations for CobiGen.
 */
public class UpgradeTestdataConfigurations {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UpgradeTestdataConfigurations.class);

    /**
     * Utility test to automatically migrate test configurations for CobiGen.
     */
    @Test
    @Ignore("Just a script to maintain test data after upgrading configurations. Should be run with caution!")
    public void upgradeTestdataConfigurations() {
        File root = new File("src/test/resources/testdata/");
        LinkedList<File> workingset = Lists.newLinkedList();
        List<File> rootChildren = Arrays.asList(root.listFiles());
        workingset.addAll(rootChildren);
        LOG.debug("Adding {} files to worklist.", rootChildren.size());

        while (!workingset.isEmpty()) {
            File current = workingset.pop();
            if (current.isDirectory() && !current.getName().equals("upgrade")) {
                List<File> children = Arrays.asList(current.listFiles());
                workingset.addAll(children);
                LOG.debug("Adding {} files to worklist.", children.size());
            } else if (current.getName().equals("context.xml")) {
                if (!current.toPath().getParent().getFileName().toString().startsWith("faulty")) {
                    LOG.debug("Upgrading ContextConfiguration: {}", current.toPath());
                    new ContextConfigurationUpgrader().upgradeConfigurationToLatestVersion(current.toPath().getParent(),
                        BackupPolicy.NO_BACKUP);
                    current.toPath().resolveSibling("context.bak.xml").toFile().delete();
                }
            } else if (current.getName().equals("templates.xml")) {
                if (!current.toPath().getParent().getFileName().toString().startsWith("faulty")) {
                    LOG.debug("Upgrading TemplateConfiguration: {}", current.toPath());
                    new TemplateConfigurationUpgrader()
                        .upgradeConfigurationToLatestVersion(current.toPath().getParent(), BackupPolicy.NO_BACKUP);
                    current.toPath().resolveSibling("templates.bak.xml").toFile().delete();
                }
            }
        }

        LOG.debug("DONE.");
    }
}
