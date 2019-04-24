package com.devonfw.cobigen.impl.externalprocess;

import java.io.File;
import java.util.Properties;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;

/**
 * Contains the properties related to the external process like its name, file path or the URL to download it
 */
public class ExternalProcessProperties {

    /**
     * Path to the external process
     */
    private String filePath;

    /**
     * Name of the external process
     */
    private String fileName;

    /**
     * Version of the external process (server)
     */
    private String serverVersion;

    /**
     * URL to download the external process
     */
    private String downloadURL;

    /**
     * Current operative system, so that we can check which executable do we need
     */
    private String osName = System.getProperty("os.name").toLowerCase();

    /**
     * Creates a new instance containing the properties related to the external process like its name, file
     * path or the URL to download it
     * @param propertiesFile
     *            Properties file where the needed values are stored
     *
     */
    public ExternalProcessProperties(Properties propertiesFile) {
        filePath = "";
        fileName = "";
        serverVersion = "";
        downloadURL = "";

        settingProperties(propertiesFile);
        getExePath();
    }

    /**
     * Creates a new instance containing the properties related to the external process like its name, file
     * path or the URL to download it
     * @param name
     *            Name of the exe file
     *
     */
    public ExternalProcessProperties(String name) {
        filePath = "";
        fileName = name;
        serverVersion = "";
        downloadURL = "";

        getExePath();
    }

    /**
     * Setting all the properties from the properties file. Properties are different depending on the
     * Operative System
     * @param propertiesFile
     *            Properties file where the needed values are stored
     *
     */
    private void settingProperties(Properties propertiesFile) {
        serverVersion = propertiesFile.getProperty("server.version");
        fileName = propertiesFile.getProperty("server.name");

        if (osName.indexOf("win") >= 0) {
            downloadURL = propertiesFile.getProperty("server.url");
        } else if (osName.indexOf("mac") >= 0) {
            downloadURL = propertiesFile.getProperty("server.url.mac");
        } else {
            downloadURL = propertiesFile.getProperty("server.url.linux");
        }
    }

    /**
     * Trying to resolve the path of the executable server. We had to implement several cases for running
     * JUnit tests correctly, as calling this class from a test means that the exe is on the classpath.
     *
     */
    private void getExePath() {
        if (getClass().getResource(fileName) == null) {

            if (osName.indexOf("win") >= 0) {
                filePath = ExternalProcessConstants.EXTERNAL_PROCESS_FOLDER.toString() + File.separator + fileName + "-"
                    + serverVersion + ".exe";
            } else if (osName.indexOf("mac") >= 0) {
                filePath = ExternalProcessConstants.EXTERNAL_PROCESS_FOLDER.toString() + File.separator + fileName
                    + "-macos-" + serverVersion;
            } else {
                filePath = ExternalProcessConstants.EXTERNAL_PROCESS_FOLDER.toString() + File.separator + fileName
                    + "-linux-" + serverVersion;
            }

        } else {
            // When the exe is on the current class path
            filePath = getClass().getResource(fileName).getPath();
            if (System.getProperty("os.name").startsWith("Windows")) {
                filePath = filePath + ".exe";
            }
        }

    }

    /**
     * Get the path where the external process is located
     * @return String of the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Set the path where the external process is located
     * @param filePath
     *            path to the external process
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Get the name of the external process
     * @return String of the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the name of the external process
     * @param fileName
     *            String of the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the version of the external process (server)
     * @return String of the version of the external process (server)
     */
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * Set the version of the external process (server)
     * @param serverVersion
     *            the version of the external process (server)
     */
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    /**
     * Get the download URL of the server
     * @return String download URL of the server
     */
    public String getDownloadURL() {
        return downloadURL;
    }

    /**
     * Set the download URL of the server
     * @param downloadURL
     *            the download URL of the server
     */
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

}
