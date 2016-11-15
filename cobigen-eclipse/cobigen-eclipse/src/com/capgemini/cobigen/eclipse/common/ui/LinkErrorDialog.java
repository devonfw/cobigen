package com.capgemini.cobigen.eclipse.common.ui;

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
 * Implementation of an {@link ErrorDialog} whereas the normal error label is replaced by a {@link Link} label
 * to allow links in the error dialog.
 */
public class LinkErrorDialog extends ErrorDialog {

    /**
     * @see ErrorDialog
     */
    @SuppressWarnings("javadoc")
    public LinkErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status,
        int displayMask) {
        super(parentShell, dialogTitle, message, status, displayMask);

        // mark any file path as link
        this.message = this.message.replaceAll(
            "(?:[a-zA-Z]\\:)\\\\([\\w-\\.\\$\\{\\}]+\\\\)*[\\w\\$]([\\w-\\.\\$\\{\\}])+", "<a>$0</a>");
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

            // BAD HACK: let SWT calculate the height of wrapped text using a label to just set it as a hint
            // to the Link label, which is not feasible to correctly calculate the final height of itself.
            messageLabel = new Label(composite, getMessageLabelStyle());
            messageLabel.setText(message);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
                .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                .applyTo(messageLabel);
            Point size = messageLabel.computeSize(
                convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT);
            messageLabel.dispose();

            Link link = new Link(composite, getMessageLabelStyle());
            link.setText(message);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).hint(size)
                .applyTo(link);

            link.addSelectionListener(new LinkSelectionAdapter());
        }
        return composite;
    }
}
