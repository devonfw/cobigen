package com.capgemini.cobigen.eclipse.test.common;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import com.capgemini.cobigen.eclipse.test.common.swtbot.AllJobsAreFinished;

/**
 * Eclipse specific operations to make test setup easier.
 */
public class EclipseUtils {

    /**
     * @see #importExistingGeneralProject(SWTWorkbenchBot, String, boolean)
     *      importExistingGeneralProject(SWTWorkbenchBot, String, boolean) with
     *      {@code copyIntoWorkspace=false}
     */
    @SuppressWarnings("javadoc")
    public static void importExistingGeneralProject(SWTWorkbenchBot bot, String projectPath) {
        importExistingGeneralProject(bot, projectPath, true);
    }

    /**
     * Imports the an existing general (non-java/non-maven) project to the workspace.
     * @param bot
     *            the {@link SWTWorkbenchBot} of the test
     * @param projectPath
     *            absolute path of the project on file system
     * @param copyIntoWorkspace
     *            state if the project sources should be copied into workspace while importing
     */
    public static void importExistingGeneralProject(SWTWorkbenchBot bot, String projectPath,
        boolean copyIntoWorkspace) {
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        view.show();
        view.setFocus();
        SWTBotTree packageExplorerTree = view.bot().tree();
        packageExplorerTree.contextMenu("Import...").click();

        bot.tree().expandNode("General").select("Existing Projects into Workspace");
        bot.button(IDialogConstants.NEXT_LABEL).click();
        bot.radio("Select root directory:").click();
        SWTBotCombo comboBox = bot.comboBox();
        comboBox.setText(projectPath);

        SWTBotCheckBox cbCopyProjects = bot.checkBox("Copy projects into workspace");
        SWTBotButton selectAll = bot.button("Select All");
        selectAll.setFocus();
        bot.waitUntil(widgetIsEnabled(selectAll));
        selectAll.click(); // just to trigger project scanning, correct logic will be set below

        if (copyIntoWorkspace && !cbCopyProjects.isChecked()
            || !copyIntoWorkspace && cbCopyProjects.isChecked()) {
            cbCopyProjects.click();
        }

        SWTBotButton finishButton = bot.button("Finish");
        bot.waitUntil(widgetIsEnabled(finishButton));
        finishButton.click();
        bot.waitUntil(new AllJobsAreFinished(), 20000);
    }

    /**
     * Updates a maven project.
     * @param bot
     *            the {@link SWTWorkbenchBot} of the test
     * @param projectName
     *            name of the project
     */
    public static void updateMavenProject(SWTWorkbenchBot bot, String projectName) {
        SWTBotView view = bot.viewById(JavaUI.ID_PACKAGES);
        SWTBotTreeItem configurationProject = view.bot().tree().expandNode(projectName);
        configurationProject.contextMenu("Maven").menu("Update Project...").click();
        bot.waitUntil(shellIsActive("Update Maven Project"));
        bot.button(IDialogConstants.OK_LABEL).click();
        bot.waitUntil(new AllJobsAreFinished(), 20000);
    }

    /**
     * Create a new file in the given project relative path with the given contents.
     * @param project
     *            to add the file to
     * @param relativePath
     *            the project relative path
     * @param contents
     *            the contents to be written to the file
     * @return the {@link IFile file}
     * @throws CoreException
     *             if anything fails
     */
    public static IFile createFile(IJavaProject project, String relativePath, String contents)
        throws CoreException {

        IContainer lastContainer = project.getProject();
        String[] pathArr = relativePath.split("/");
        // create folders
        for (int i = 0; i < pathArr.length - 1; i++) {
            IFolder folder = lastContainer.getFolder(new Path(pathArr[i]));
            if (!folder.exists()) {
                folder.create(true, true, new NullProgressMonitor());
            }
            lastContainer = folder;
        }

        IFile file = lastContainer.getFile(new Path(pathArr[pathArr.length - 1]));
        if (!file.exists()) {
            file.create(new ByteArrayInputStream(contents.getBytes()), true, new NullProgressMonitor());
        }
        return file;
    }
}
