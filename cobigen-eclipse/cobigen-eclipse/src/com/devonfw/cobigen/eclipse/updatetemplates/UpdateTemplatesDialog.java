package com.devonfw.cobigen.eclipse.updatetemplates;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.common.constants.external.CobiGenDialogConstants.UpdateTemplateDialogs;
import com.devonfw.cobigen.eclipse.common.tools.PlatformUIUtil;
import com.devonfw.cobigen.eclipse.common.tools.ResourcesPluginUtil;

/**
 * Dialog to update templates from Maven central
 */
public class UpdateTemplatesDialog extends Dialog {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(UpdateTemplatesDialog.class);

    /**
     * Creates a new {@link UpdateTemplatesDialog}
     */
    public UpdateTemplatesDialog() {
        super(Display.getDefault().getActiveShell());

        // Create a new trust manager that trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            throw new Error("Failed to initialize SSLcontex", e);
        }

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

        getShell().setText(UpdateTemplateDialogs.DIALOG_TITLE);
        Composite contentParent = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        contentParent.setLayoutData(gridData);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 15;
        layout.marginHeight = 15;
        contentParent.setLayout(layout);

        Label introduction = new Label(contentParent, SWT.LEFT);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData.widthHint = 450;
        gridData.horizontalSpan = 2;
        introduction.setText("The following template folders will be downloaded from maven central repository:");
        introduction.setLayoutData(gridData);

        GridData leftGridData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        leftGridData.widthHint = 320;
        GridData rightGridData = new GridData(GridData.CENTER, GridData.CENTER, false, false);
        rightGridData.widthHint = 80;
        Label label = new Label(contentParent, SWT.NONE);
        label.setText("templates-devon4j");
        label.setLayoutData(leftGridData);
        MDC.remove(InfrastructureConstants.CORRELATION_ID);
        return contentParent;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        Button button = createButton(parent, IDialogConstants.OK_ID, "Download", false);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    ResourcesPluginUtil.setUserWantsToDownloadTemplates(true);
                    ResourcesPluginUtil.downloadJar(false);
                    MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Information", null,
                        "Downloaded succesfully!", MessageDialog.INFORMATION, new String[] { "Ok" }, 1);
                    dialog.setBlockOnOpen(true);
                    dialog.open();
                } catch (MalformedURLException malformedURLException) {
                    PlatformUIUtil.openErrorDialog(
                        "Templates were not downloaded because the maven central repo url or path doesn't exist. \n "
                            + "Please create a new issue on GitHub https://github.com/devonfw/cobigen/issues",
                        malformedURLException);
                    throw new CobiGenRuntimeException(
                        "Invalid maven central repo url or path doesn't exist " + malformedURLException);
                } catch (IOException exceptionIO) {
                    PlatformUIUtil.openErrorDialog("Templates were not downloaded because there is no connection."
                        + " Are you connected to the Internet? ", exceptionIO);
                    throw new CobiGenRuntimeException(
                        "Failed while reading or writing Jar at .metadata folder" + exceptionIO);
                }
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }

}
