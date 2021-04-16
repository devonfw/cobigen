package com.devonfw.cobigen.impl.externalprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.UnmarshalException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.impl.exceptions.ConnectionExceptionHandler;
import com.devonfw.cobigen.impl.exceptions.ConnectionExceptionHandler.ConnectionException;
import com.devonfw.cobigen.impl.exceptions.HttpConnectionException;
import com.devonfw.cobigen.impl.util.ExceptionUtil;
import com.devonfw.cobigen.impl.util.ProcessOutputUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Class for handling the external process creation and the communication with it.
 *
 */
public class ExternalProcessHandler {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ExternalProcessHandler.class);

    /**
     * Port used for connecting to the server
     */
    private Integer port;

    /**
     * Host name of the server, by default is localhost
     */
    private String hostName;

    /**
     * HTTP connection instance for sending requests
     */
    private HttpURLConnection conn;

    /**
     * Native process instance
     */
    private Process process;

    /**
     * Used for handling all the errors our external process throws
     */
    private ProcessOutputUtil errorHandler = null;

    /**
     * URL for connecting with the different services of our external process
     */
    private URL url;

    /**
     * Path of the executable file (the server)
     */
    private String exeName = "";

    /**
     * Singleton instance of {@link ExternalProcessHandler}.
     */
    public static ExternalProcessHandler externalProcessHandler = null;

    /**
     * Exception handler related to connectivity to the server
     */
    private ConnectionExceptionHandler connectionExc = new ConnectionExceptionHandler();

    /**
     * Holds properties of the external process like file name, path, URL to download...
     */
    private ExternalProcessProperties processProperties;

    /**
     * Using singleton pattern, we will only have one instance of {@link ExternalProcessHandler}.
     *
     * @param exeName
     *            of the executable file to execute
     * @param hostName
     *            name of the server, normally localhost
     * @param port
     *            port to be used for connecting to the server
     * @return external process instance
     */
    public static ExternalProcessHandler getExternalProcessHandler(String exeName, String hostName, Integer port) {

        if (externalProcessHandler == null) {
            externalProcessHandler = new ExternalProcessHandler(exeName, hostName, port);
        }

        return externalProcessHandler;
    }

    /**
     * Using singleton pattern, we will only have one instance of {@link ExternalProcessHandler}.
     *
     * @param activatorClass
     *            plug-in class that activated the execution of the server. We need it to find the jar of the
     *            activator class, which should define the executable server in a properties file
     * @param hostName
     *            name of the server, normally localhost
     * @param port
     *            port to be used for connecting to the server
     * @return external process instance
     */
    public static ExternalProcessHandler getExternalProcessHandler(Class<?> activatorClass, String hostName,
        Integer port) {

        if (externalProcessHandler == null) {
            externalProcessHandler = new ExternalProcessHandler(activatorClass, hostName, port);
        }

        return externalProcessHandler;
    }

    /**
     * Constructor of {@link ExternalProcessHandler}.
     *
     * @param name
     *            of the executable file to execute
     * @param hostName
     *            name of the server, normally localhost
     * @param port
     *            port to be used for connecting to the server
     */
    protected ExternalProcessHandler(String name, String hostName, int port) {

        LOG.info("Instantiating server: " + name);
        exeName = name;
        processProperties = new ExternalProcessProperties(exeName);
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Constructor of {@link ExternalProcessHandler}.
     *
     * @param activatorClass
     *            plug-in class that activated the execution of the server. We need it to find the jar of the
     *            activator class, which should define the executable server in a properties file
     * @param hostName
     *            name of the server, normally localhost
     * @param port
     *            port to be used for connecting to the server
     */
    public ExternalProcessHandler(Class<?> activatorClass, String hostName, Integer port) {
        setServerProperties(activatorClass);
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Tries to execute, as a new process, the executable file defined inside the plugin calling this method.
     * Also initializes the error handler.
     *
     * @param activatorClass
     *            plug-in class that activated the execution of the server. We need it to find the jar of the
     *            activator class, which should define the executable server in a properties file
     */
    public void setServerProperties(Class<?> activatorClass) {

        try {
            // We load the properties file from the input stream
            if (activatorClass.getResourceAsStream("/META-INF/externalservers/server.properties") != null) {

                Properties serverProperties = new Properties();
                serverProperties
                    .load(activatorClass.getResourceAsStream("/META-INF/externalservers/server.properties"));

                processProperties = new ExternalProcessProperties(serverProperties);
                exeName = processProperties.getFileName();

            } else {
                throw new CobiGenRuntimeException(
                    "Error starting the external process. The server.properties file has not been found");
            }

        } catch (IOException e) {
            throw new CobiGenRuntimeException(
                "Error starting the external process. Unable to load server.properties file.", e);
        }
    }

    /**
     * Executes the exe file
     * @return true only if the exe has been correctly executed
     */
    public boolean startServer() {

        try {
            String filePath = processProperties.getFilePath();
            if (exeIsNotValid(filePath)) {
                filePath = downloadExe(processProperties.getDownloadURL(), filePath, processProperties.getFileName());
            }

            setPermissions(filePath);

            process = new ProcessBuilder(filePath, String.valueOf(port)).start();

            // We try to get the error output
            errorHandler = new ProcessOutputUtil(process.getErrorStream(), "UTF-8");

            int retry = 0;
            while (!process.isAlive() && retry <= 10) {
                if (processHasErrors()) {
                    terminateProcessConnection();
                    // If the process outputs errors, it means that we were able to start it successfully,
                    // that's why we return true
                    return true;
                }
                retry++;
                // This means the executable has already finished
                if (process.exitValue() == 0) {
                    return true;
                }
                LOG.info("Waiting process to be alive");
            }
            if (retry > 10) {
                // We were not able to start the process
                return false;
            }
        } catch (IOException e) {
            LOG.error("Unable to start the exe/server", e);
        }

        LOG.info("Try to start server at port " + port);

        return true;
    }

    /**
     * Returns true if the current exe server is not valid and we need to force a download.
     * @param filePath
     *            path to the exe of the server
     * @return true if the exe file needs to be downloaded again
     */
    private boolean exeIsNotValid(String filePath) {
        File exeFile = new File(filePath);
        if (!exeFile.isFile()) {
            return true;
        }

        return false;
    }

    /**
     * Sets permissions to the executable file, so that it can be executed
     * @param filePath
     *            path to the file we want to change its permissions
     * @throws IOException
     *             throws {@link IOException}
     */
    private void setPermissions(String filePath) throws IOException {
        // Depending on the operative system, we need to set permissions in a different way
        if (processProperties.getOsName().indexOf("win") >= 0) {
            File exeFile = new File(filePath);
            try {
                exeFile.setExecutable(true, false);
            } catch (SecurityException e) {
                LOG.error("Not able to set executable permissions on the file", e);
            }
        } else {
            Files.setPosixFilePermissions(Paths.get(filePath),
                Sets.newHashSet(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ,
                    PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ,
                    PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ));
        }
    }

    /**
     * Downloads the external server on the specified folder (which will normally be .cobigen folder)
     * @param downloadURL
     *            the URL that points to the file to download
     * @param filePath
     *            path where the external server should be downloaded to
     * @param fileName
     *            name of the external server
     * @return path of the external server
     * @throws IOException
     *             {@link IOException} occurred while downloading the file
     */
    private String downloadExe(String downloadURL, String filePath, String fileName) throws IOException {

        String tarFileName = "";
        String currentFileName = "";
        File exeFile = new File(filePath);
        String parentDirectory = exeFile.getParent();

        URL url = new URL(downloadURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Downloading tar file
        File tarFile;
        Path tarPath;
        LOG.info("Downloading server from {} to {}", downloadURL, filePath);
        try (InputStream inputStream = conn.getInputStream()) {

            tarFileName = conn.getURL().getFile().substring(conn.getURL().getFile().lastIndexOf("/") + 1);
            tarFile = new File(parentDirectory + File.separator + tarFileName);
            tarPath = tarFile.toPath();
            if (!tarFile.exists()) {
                Files.copy(inputStream, tarPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        conn.disconnect();

        // We need to extract the file to our current directory
        // Do we have write access?
        if (Files.isWritable(Paths.get(parentDirectory))) {

            LOG.info("Extracting server to users folder...");
            try (InputStream is = new GZIPInputStream(new FileInputStream(tarPath.toString()));
                TarArchiveInputStream tarInputStream =
                    (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);) {

                TarArchiveEntry entry;

                while ((entry = tarInputStream.getNextTarEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    if (entry.getName().contains(fileName)) {
                        // We don't want the directories (src/main/server.exe), we just want to create the
                        // file (server.exe)
                        currentFileName = getLastPartOfTarPath(entry.getName());
                        File curfile = new File(parentDirectory, currentFileName);

                        try (FileOutputStream fos = new FileOutputStream(curfile)) {
                            IOUtils.copy(tarInputStream, fos);

                            fos.flush();
                            // We need to wait until it has finished writing the file
                            fos.getFD().sync();
                            break;
                        }
                    }
                }

            } catch (ArchiveException e) {
                throw new CobiGenRuntimeException("Error while extracting the external server.", e);
            }
        } else {
            // We are not able to extract the server
            Files.deleteIfExists(tarPath);
            return "";
        }

        // Remove tar file
        Files.deleteIfExists(tarPath);
        return filePath;

    }

    /**
     * Returns the last part of a tar path (path inside a tar file). So if we have "src/test/java" it will
     * return "java"
     * @param path
     *            to perform the operation
     * @return string with the result
     */
    private String getLastPartOfTarPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * Tries several times to start a new connection to the server. If the port is already in use, tries to
     * connect again to another port
     *
     * @return true if the ExternalProcess was able to connect to the server
     */
    public boolean initializeConnection() {

        for (int retry = 0; retry < 10; retry++) {
            try {
                startConnection();

                // Just check correct port acquisition
                if (acquirePort()) {
                    return true;
                } else {
                    continue;
                }
            } catch (Exception e) {
                LOG.error("Connection to server failed, attempt number " + retry + ".");
                try {
                    LOG.info("Sleeping...");
                    Thread.sleep(100);
                } catch (InterruptedException interrupted) {
                    LOG.error("Error while trying to sleep the execution.");
                }
                if (connectionExc.handle(e).equals(ConnectionException.MALFORMED_URL)) {
                    LOG.error("MalformedURLException: Connection to server failed, MalformedURL.", e);
                    return false;
                }

            } finally {
                conn.disconnect();
            }
        }
        return false;
    }

    /**
     * Tries to start a new connection to the server
     *
     * @throws MalformedURLException
     *             thrown when the URL is malformed
     * @throws IOException
     *             throws {@link IOException}
     */
    public void startConnection() throws IOException {

        url = new URL(
            "http://" + hostName + ":" + port + "/processmanagement/" + processProperties.getPluginName() + "/");
        conn = (HttpURLConnection) url.openConnection();
        conn.connect();
    }

    /**
     * Tries to acquire a port. If the port is already in use, tries to execute again the external process
     * with another port
     *
     * @return true if a port has been acquired
     */
    private boolean acquirePort() {

        if (isNotConnected()) {
            restartServer();
        } else {
            return true;
        }
        return false;
    }

    /**
     * Checks whether the external process error handler has any content. If so, it means there is an error
     *
     * @return true if external process contains errors
     */
    private boolean processHasErrors() {

        if (errorHandler != null) {
            // External process has not printed any error
            if (errorHandler.getText().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sends a dummy request to the server in order to check if it is not connected
     *
     * @return true if it is not connected
     */
    public boolean isNotConnected() {

        try {
            getConnection("GET", "Content-Type", "text/plain", ExternalProcessConstants.IS_CONNECTION_READY);
            if (conn.getResponseCode() < 300) {

                // Check if it is the correct server version
                try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(isr)) {
                    String response = br.readLine();

                    if (response.equals(processProperties.getServerVersion())) {
                        return false;
                    }

                    if (response.equals("true")) {
                        LOG.warn(
                            "The old version {} of {} is currently deployed. Please consider deploying the newest version to get the current bug fixes/features.",
                            processProperties.getServerVersion(), exeName);
                        return false;
                    }
                } catch (Exception e) {
                    LOG.info("Reading server version failed");
                }

                return true;
            }
        } catch (IOException e) {
            LOG.error("Connection to server failed, maybe the server is not yet deployed...");
        }

        return true;
    }

    /**
     * Gets an HTTP request using the specified parameters
     *
     * @param httpMethod
     *            HTTP method to use (POST, GET, PUT...)
     * @param headerProperty
     *            Header property to use (content-type, content-length
     * @param mediaType
     *            type of media (application/json, text/plain...)
     * @param endpointURL
     *            The endpoint URL of the service
     * @return the {@link HttpURLConnection} to the endpoint
     */
    public HttpURLConnection getConnection(String httpMethod, String headerProperty, String mediaType,
        String endpointURL) {

        try {
            URL currentURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + endpointURL);

            conn = (HttpURLConnection) currentURL.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod(httpMethod);
            conn.setRequestProperty(headerProperty, mediaType);
            conn.setConnectTimeout(ExternalProcessConstants.CONNECTION_TIMEOUT);
            conn.setReadTimeout(ExternalProcessConstants.CONNECTION_TIMEOUT);

        } catch (Exception e) {
            Throwable parseCause = ExceptionUtil.getCause(e, Exception.class, UnmarshalException.class);
            LOG.info(parseCause.toString(), e);
            ConnectionExceptionHandler connectionExc = new ConnectionExceptionHandler();
            connectionExc.handle(e);
        }
        return conn;
    }

    /**
     * Sends a transfer object to the server, by using http connection
     * @param dataTo
     *            Data to transfer, it should be a serializable class
     * @param conn
     *            {@link HttpURLConnection} to the server, containing also the URL
     * @param charsetName
     *            the name of a supported {@link Charset}
     * @return returns true when the status code of the request was 200 or 201
     */
    public boolean sendRequest(Object dataTo, HttpURLConnection conn, String charsetName) {
        ObjectWriter objWriter;
        objWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonMergerTo;
        try (OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, Charset.forName(charsetName).newEncoder());) {

            jsonMergerTo = objWriter.writeValueAsString(dataTo);
            // We need to escape new lines because otherwise our JSON gets corrupted
            jsonMergerTo = jsonMergerTo.replace("\\n", "\\\\n");

            osw.write(jsonMergerTo);
            osw.flush();

            for (int i = 0; i < ExternalProcessConstants.NUMBER_OF_RETRIES; i++) {
                conn.connect();
                int statusCode = conn.getResponseCode();

                if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
                    // Successful request
                    return true;
                }
                if (shouldNotRetry(conn.getResponseCode())) {
                    LOG.error("Sending the request failed. The response message is the following: "
                        + conn.getResponseMessage());
                    return false;
                }
            }
            return false;
        } catch (IOException e) {
            Throwable parseCause = ExceptionUtil.getCause(e, Exception.class, UnmarshalException.class);
            LOG.info(parseCause.toString(), e);
        }

        return false;

    }

    /**
     * Checks whether the HTTP request should NOT be retried
     * @param statusCode
     *            code of the HTTP request
     * @return true when we should not retry because the status code describes a unavoidable case
     * @throws IOException
     *             throws {@link IOException}
     */
    private boolean shouldNotRetry(int statusCode) throws IOException {

        switch (statusCode) {

        case HttpURLConnection.HTTP_MOVED_TEMP:
            return false;

        case HttpURLConnection.HTTP_UNAVAILABLE:
            return false;

        default:
            try {
                throw new HttpConnectionException("Failed : HTTP error code : " + conn.getResponseCode());
            } catch (HttpConnectionException e) {
                return true;
            }
        }

    }

    /**
     * Terminates the current connection to the server, the process and the error handler. Then tries to load
     * a new server on a different port number
     */
    public void restartServer() {
        terminateProcessConnection();
        port++;
        startServer();
    }

    /**
     * Terminates the connection to the server (if was still connected), the process (if it was still alive)
     * and the error output handler (if it was still alive)
     * @return true if it was able to terminate the process
     */
    public Boolean terminateProcessConnection() {

        if (conn != null) {
            conn.disconnect();
        }
        if (process == null) {
            return true;
        }
        if (process.isAlive()) {
            process.destroyForcibly();
            LOG.info("Closing process instance");
        }
        // Let's kill the process error handler
        if (errorHandler.isAlive()) {
            try {
                errorHandler.join();
            } catch (InterruptedException e) {
                LOG.info("Error while trying to close the process error handler", e);
                return false;
            }
        }
        return true;
    }

}
