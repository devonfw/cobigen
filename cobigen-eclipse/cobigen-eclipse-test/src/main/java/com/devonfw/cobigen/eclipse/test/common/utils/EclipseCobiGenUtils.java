package com.devonfw.cobigen.eclipse.test.common.utils;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;
import com.devonfw.cobigen.eclipse.test.common.swtbot.AnyShellIsActive;

/**
 * Eclipse Utils to work with the CobiGen UI.
 */
public class EclipseCobiGenUtils {

    /** Default timeout for waiting on generation results or build results */
    public static final int DEFAULT_TIMEOUT = 15000;

    /**
     * Selects the the increment with the given name and generates it.
     * @param bot
     *            the {@link SWTWorkbenchBot} of the test
     * @param input
     *            input of CobiGen to be selected
     * @param increments
     *            increments to be generated.
     * @throws Exception
     *             test fails
     */
    public static void processCobiGen(SWTWorkbenchBot bot, SWTBotTreeItem input, String... increments)
        throws Exception {

        // Open generation wizard with new file as Input
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
        bot.waitUntil(new AllJobsAreFinished(), DEFAULT_TIMEOUT); // build might take some time
        input.contextMenu("CobiGen").menu("Generate...").click();
        bot.waitUntil(new AnyShellIsActive(CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE,
            CobiGenDialogConstants.GenerateWizard.DIALOG_TITLE_BATCH), DEFAULT_TIMEOUT);

        // select increment and generate
        for (String increment : increments) {
            SWTBotTreeItem treeItem = bot.tree().getTreeItem(increment);
            bot.waitUntil(widgetIsEnabled(treeItem));
            treeItem.check();
        }
        SWTBotButton finishButton = bot.button(IDialogConstants.FINISH_LABEL);
        bot.waitUntil(widgetIsEnabled(bot.button()));
        finishButton.click();
    }

    /**
     * Confirm successful generation dialog as well as organize import dialog if necessary.
     * @param bot
     *            the {@link SWTWorkbenchBot} of the test
     */
    public static void confirmSuccessfullGeneration(SWTWorkbenchBot bot) {
        try {
            bot.waitUntil(shellIsActive("Organize Imports"), DEFAULT_TIMEOUT);
            bot.shell("Organize Imports").bot().button(IDialogConstants.OK_LABEL).click();
        } catch (TimeoutException e) {
            // dialog just optional
        }
        bot.waitUntil(shellIsActive(CobiGenDialogConstants.DIALOG_TITLE_GEN_SUCCEEDED), DEFAULT_TIMEOUT);
        bot.shell(CobiGenDialogConstants.DIALOG_TITLE_GEN_SUCCEEDED).bot().button(IDialogConstants.OK_LABEL).click();
    }
}
