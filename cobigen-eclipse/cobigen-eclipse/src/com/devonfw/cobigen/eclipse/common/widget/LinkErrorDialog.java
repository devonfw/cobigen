package com.devonfw.cobigen.eclipse.common.widget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

/**
 * Implementation of an {@link ErrorDialog} whereas the normal error label is replaced by a {@link Link} label to allow
 * links in the error dialog.
 */
public class LinkErrorDialog extends ErrorDialog {

  /**
   * Creates an error dialog. Note that the dialog will have no visual representation (no widgets) until it is told to
   * open.
   * <p>
   * Normally one should use <code>openError</code> to create and open one of these. This constructor is useful only if
   * the error object being displayed contains child items <i>and</i> you need to specify a mask which will be used to
   * filter the displaying of these children. The error dialog will only be displayed if there is at least one child
   * status matching the mask.
   * </p>
   *
   * @param parentShell the shell under which to create this dialog
   * @param dialogTitle the title to use for this dialog, or <code>null</code> to indicate that the default title should
   *        be used
   * @param message the message to show in this dialog, or <code>null</code> to indicate that the error's message should
   *        be shown as the primary message
   * @param status the error to show to the user
   * @param displayMask the mask to use to filter the displaying of child items, as per <code>IStatus.matches</code>
   * @see ErrorDialog#ErrorDialog(Shell, String, String, IStatus, int)
   */
  public LinkErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status, int displayMask) {

    super(parentShell, dialogTitle, message, status, displayMask);

    // mark any file path as link
    this.message = this.message
        .replaceAll("(?:[a-zA-Z]\\:)\\\\([\\w-\\.\\$\\{\\}#]+\\\\)*[\\w\\$]([\\w-\\.\\$\\{\\}#])+", "<a>$0</a>");
  }

  /**
   * Opens an error dialog to display the given error. Use this method if the error object being displayed does not
   * contain child items, or if you wish to display all such items without filtering.
   *
   * @param parent the parent shell of the dialog, or <code>null</code> if none
   * @param dialogTitle the title to use for this dialog, or <code>null</code> to indicate that the default title should
   *        be used
   * @param message the message to show in this dialog, or <code>null</code> to indicate that the error's message should
   *        be shown as the primary message
   * @param status the error to show to the user
   * @return the code of the button that was pressed that resulted in this dialog closing. This will be
   *         <code>Dialog.OK</code> if the OK button was pressed, or <code>Dialog.CANCEL</code> if this dialog's close
   *         window decoration or the ESC key was used.
   *
   * @see ErrorDialog#openError(Shell, String, String, IStatus)
   */
  public static int openError(Shell parent, String dialogTitle, String message, IStatus status) {

    return openError(parent, dialogTitle, message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
  }

  /**
   * Opens an error dialog to display the given error. Use this method if the error object being displayed contains
   * child items <i>and</i> you wish to specify a mask which will be used to filter the displaying of these children.
   * The error dialog will only be displayed if there is at least one child status matching the mask.
   *
   * @param parentShell the parent shell of the dialog, or <code>null</code> if none
   * @param title the title to use for this dialog, or <code>null</code> to indicate that the default title should be
   *        used
   * @param message the message to show in this dialog, or <code>null</code> to indicate that the error's message should
   *        be shown as the primary message
   * @param status the error to show to the user
   * @param displayMask the mask to use to filter the displaying of child items, as per <code>IStatus.matches</code>
   * @return the code of the button that was pressed that resulted in this dialog closing. This will be
   *         <code>Dialog.OK</code> if the OK button was pressed, or <code>Dialog.CANCEL</code> if this dialog's close
   *         window decoration or the ESC key was used.
   * @see ErrorDialog#openError(Shell, String, String, IStatus, int)
   */
  public static int openError(Shell parentShell, String title, String message, IStatus status, int displayMask) {

    ErrorDialog dialog = new LinkErrorDialog(parentShell, title, message, status, displayMask);
    return dialog.open();
  }

  /**
   * Create the area the message will be shown in.
   * <p>
   * The parent composite is assumed to use GridLayout as its layout manager, since the parent is typically the
   * composite created in {@link Dialog#createDialogArea}.
   * </p>
   * <p>
   * <strong>Note:</strong> Clients are expected to call this method, otherwise neither the icon nor the message will
   * appear.
   * </p>
   *
   * @param composite The composite to parent from.
   * @return Control
   */
  @Override
  protected Control createMessageArea(Composite composite) {

    Image image = getImage();
    if (image != null) {
      this.imageLabel = new Label(composite, SWT.NULL);
      image.setBackground(this.imageLabel.getBackground());
      this.imageLabel.setImage(image);
      GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(this.imageLabel);
    }

    if (this.message != null) {

      // BAD HACK: let SWT calculate the height of wrapped text using a label to just set it as a hint
      // to the Link label, which is not feasible to correctly calculate the final height of itself.
      this.messageLabel = new Label(composite, getMessageLabelStyle());
      this.messageLabel.setText(this.message);
      GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
          .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
          .applyTo(this.messageLabel);
      Point size = this.messageLabel
          .computeSize(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT);
      this.messageLabel.dispose();

      Link link = new Link(composite, getMessageLabelStyle());
      link.setText(this.message);
      GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).hint(size).applyTo(link);

      link.addSelectionListener(new LinkSelectionAdapter());
    }
    return composite;
  }
}
