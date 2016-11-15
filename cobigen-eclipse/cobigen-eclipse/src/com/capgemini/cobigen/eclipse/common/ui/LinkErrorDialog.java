package com.capgemini.cobigen.eclipse.common.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

/**
 *
 */
public class LinkErrorDialog extends ErrorDialog {

    /**
     * @see ErrorDialog
     */
    @SuppressWarnings("javadoc")
    public LinkErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status,
        int displayMask) {
        super(parentShell, dialogTitle, message, status, displayMask);
    }

    /**
     * @see ErrorDialog#openError(Shell, String, String, IStatus, int)
     */
    @SuppressWarnings("javadoc")
    public static int openError(Shell parent, String dialogTitle, String message, IStatus status) {
        return openError(parent, dialogTitle, message, status,
            IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
    }

    /**
     * @see ErrorDialog#openError(Shell, String, String, IStatus)
     */
    @SuppressWarnings("javadoc")
    public static int openError(Shell parentShell, String title, String message, IStatus status,
        int displayMask) {
        ErrorDialog dialog = new LinkErrorDialog(parentShell, title, message, status, displayMask);
        return dialog.open();
    }

    /**
     * Create the area the message will be shown in.
     * <p>
     * The parent composite is assumed to use GridLayout as its layout manager, since the parent is typically
     * the composite created in {@link Dialog#createDialogArea}.
     * </p>
     * <p>
     * <strong>Note:</strong> Clients are expected to call this method, otherwise neither the icon nor the
     * message will appear.
     * </p>
     *
     * @param composite
     *            The composite to parent from.
     * @return Control
     */
    @Override
    protected Control createMessageArea(Composite composite) {
        Image image = getImage();
        if (image != null) {
            imageLabel = new Label(composite, SWT.NULL);
            image.setBackground(imageLabel.getBackground());
            imageLabel.setImage(image);
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
        }

        if (message != null) {

            Link link = new Link(composite, getMessageLabelStyle());
            link.setText(message);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
                .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                .applyTo(link);
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        Desktop.getDesktop().open(new File(e.text));
                    } catch (IOException ex) {
                        MessageDialog.openError(getShell(), "Error",
                            "Could not open path " + e.text + " in file explorer.");
                    }
                }
            });
        }
        return composite;
    }
}
