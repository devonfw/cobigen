package com.devonfw.cobigen.eclipse.common.widget;

import java.awt.Desktop;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;

/**
 * Selection adapter for {@link LinkErrorDialog}.
 */
public class LinkSelectionAdapter extends SelectionAdapter {

  @Override
  public void widgetSelected(SelectionEvent e) {

    try {
      File link = new File(e.text);
      if (link.exists()) {
        if (link.isFile()) {
          // open file in eclipse
          openFilesInEclipse(link);
          File[] patches = getCorrespondingPatches(link);
          openFilesInEclipse(patches);
        } else {
          // open directory in file explorer
          Desktop.getDesktop().open(link);
        }
      } else {
        PlatformUIUtil.openErrorDialog("Could not open path " + e.text + " in file explorer. Path does not exist.",
            null);
      }
    } catch (IOException ex) {
      PlatformUIUtil.openErrorDialog("Could not open path " + e.text + " in file explorer.", ex);
    }
  }

  /**
   * Searches for corresponding patches of the given file.
   *
   * @param referenceFile file to find patches for
   * @return the list of patches for the file
   */
  private File[] getCorrespondingPatches(File referenceFile) {

    final String fileextension = FilenameUtils.getExtension(referenceFile.getName());
    final String baseName = FilenameUtils.getBaseName(referenceFile.getName());
    File[] patches = referenceFile.getParentFile().listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {

        return name.matches(Pattern.quote(baseName) + "\\.patch\\.[0-9]+\\." + fileextension);
      }
    });
    return patches;
  }

  /**
   * Opens a given file in the eclipse default editor.
   *
   * @param filesToOpen File to open
   */
  private void openFilesInEclipse(File... filesToOpen) {

    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    for (File f : filesToOpen) {
      IFileStore fileStore = EFS.getLocalFileSystem().getStore(f.toURI());
      try {
        IDE.openEditorOnFileStore(page, fileStore);
      } catch (PartInitException ex) {
        PlatformUIUtil.openErrorDialog("Could not open file " + filesToOpen + " in eclipse.", ex);
      }
    }
  }
}
