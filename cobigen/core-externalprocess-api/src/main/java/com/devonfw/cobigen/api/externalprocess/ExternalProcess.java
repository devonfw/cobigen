package com.devonfw.cobigen.api.externalprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class for handling the external process creation and the communication with it.
 *
 */
public class ExternalProcess {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ExternalProcess.class);

    /** Property holding the current OS name */
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /** Download URL to get the server from */
    private final String serverDownloadUrl;

    /** Filename of the server executable */
    private final String serverFileName;

    /** Version of the server executable */
    private final String serverVersion;

    /** Port used for connecting to the server */
    public int port = 5000;

    /** Host name of the server, by default is localhost */
    private String hostName = "localhost";

    /** Context path for all service requests */
    private String contextPath = "";

    /** Native process instance */
    private StartedProcess process;

    /** Path of the executable file (the server) */
    private String exeName = "";

    /** HTTP Client to execute any server call */
    private OkHttpClient httpClient;

    /** HTTP Method enum as OkHttp does not provide an enum */
    public enum HttpMethod {
        GET, POST
    }

    /**
     * Constructor of {@link ExternalProcess}.
     */
    public ExternalProcess(String serverDownloadUrl, String serverFileName, String serverVersion) {
        this(serverDownloadUrl, serverFileName, serverVersion, null, null, null);
    }

    /**
     * Constructor of {@link ExternalProcess}.
     * @param contextPath
     *            context path of the API
     */
    public ExternalProcess(String serverDownloadUrl, String serverFileName, String serverVersion, String contextPath) {
        this(serverDownloadUrl, serverFileName, serverVersion, contextPath, null, null);
    }

    /**
     * Constructor of {@link ExternalProcess}.
     *
     * @param contextPath
     *            context path of the API
     * @param hostName
     *            name of the server, normally localhost
     * @param port
     *            port to be used for connecting to the server
     */
    public ExternalProcess(String serverDownloadUrl, String serverFileName, String serverVersion, String contextPath,
        String hostName, Integer port) {
        if (contextPath != null) {
            this.contextPath = contextPath;
        }
        if (hostName != null) {
            this.hostName = hostName;
        }
        if (port != null) {
            this.port = port;
        }
        this.serverDownloadUrl = serverDownloadUrl;
        this.serverVersion = serverVersion;
        this.serverFileName = serverFileName;

        httpClient = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();
    }

    /**
     * General request sent to the external server making sure, that the server has been started beforehand.
     * @param httpMethod
     *            the http method to send the request with
     * @param path
     *            the relative path of the service
     * @param body
     *            the body to sent
     * @param mediaType
     *            the mediaType with which the body should be sent
     * @return the plain response
     */
    public String request(HttpMethod httpMethod, String path, Object body, MediaType mediaType) {
        startServer(0);
        String endpointUrl = getBasePath() + path;
        LOG.debug("Requesting {} {} with media type {}", httpMethod, endpointUrl, mediaType);
        try {
            Response response = null;
            switch (httpMethod) {
            case POST:
                response = httpClient.newCall(new Request.Builder().url(endpointUrl)
                    .post(RequestBody.create(new Gson().toJson(body), mediaType)).build()).execute();
                break;
            case GET:
                response = httpClient.newCall(new Request.Builder().url(endpointUrl).get().build()).execute();
            }

            if (response != null && (response.code() == 200 || response.code() == 201 || response.code() == 204)) {
                LOG.debug("Responded {}", response.code());
                return response.body().string();
            } else {
                throw new InputReaderException("Unable to send or receive the message from the service. Response code: "
                    + (response != null ? response.code() : null));
            }
        } catch (IOException e) {
            throw new InputReaderException("Unable to send or receive the message from the service", e);
        }
    }

    /**
     * @see #request(HttpMethod, String, Object, MediaType)
     */
    @SuppressWarnings("javadoc")
    public String post(String path, Object body, MediaType mediaType) {
        return request(HttpMethod.POST, path, body, mediaType);
    }

    /**
     * @see #request(HttpMethod, String, Object, MediaType)
     */
    @SuppressWarnings("javadoc")
    public String postJsonRequest(String path, Object body) {

        return post(path, body, MediaType.get("application/json"));
    }

    /**
     * @see #request(HttpMethod, String, Object, MediaType)
     */
    @SuppressWarnings("javadoc")
    public String get(String path, MediaType mediaType) {
        return request(HttpMethod.GET, path, null, mediaType);
    }

    /**
     * @see #request(HttpMethod, String, Object, MediaType)
     */
    @SuppressWarnings("javadoc")
    public String getJsonRequest(String path) {

        return get(path, MediaType.get("application/json"));
    }

    /**
     * Executes the exe file
     * @param currentTry
     *            tries to allocate port for starting the server. Should be initialized by 0
     * @return true only if the exe has been correctly executed
     */
    private boolean startServer(int currentTry) {
        // server ist already running
        if (process != null && process.getProcess() != null && process.getProcess().isAlive()) {
            LOG.debug("Server was already running");
            return true;
        }

        if (currentTry > 10) {
            LOG.error("Stopped trying to start the server after 10 retries");
        }

        try {
            String fileName;
            if (OS.indexOf("win") >= 0) {
                fileName = serverFileName + "-" + serverVersion + ".exe";
            } else {
                fileName = serverFileName + "-" + serverVersion;
            }
            String filePath = ExternalProcessConstants.EXTERNAL_PROCESS_FOLDER.toString() + File.separator + fileName;

            if (exeIsNotValid(filePath)) {
                filePath = downloadExecutable(filePath, fileName);
            }

            setPermissions(filePath);

            process = new ProcessExecutor().command(filePath, String.valueOf(port))
                .redirectError(
                    Slf4jStream.of(LoggerFactory.getLogger(getClass().getName() + "." + serverFileName)).asError())
                .readOutput(true).start();
            Future<ProcessResult> result = process.getFuture();

            int retry = 0;
            while (!isConnectedAndValidService() && retry <= 50) {
                if (result.isDone()) { // if process terminated already, it was failing
                    LOG.error("Could not start server in 5s. Closed with output:\n{}", result.get().getOutput());
                    process.getProcess().destroyForcibly();
                    return false;
                }
                Thread.sleep(100);
                retry++;
                LOG.info("Waiting process to be alive for {}s", 100 * retry / 1000d);
            }
            if (retry > 50) {
                return false;
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            BindException bindException = ExceptionUtil.getCause(e, BindException.class);
            if (bindException != null) {
                int newPort = aquireNewPort();
                LOG.debug("Port {} already in use, trying port {}", port, newPort);
                port = newPort;
                return startServer(currentTry++);
            }
            throw new CobiGenRuntimeException("Unable to start the exe/server", e);
        }

        LOG.info("Server started at port {}", port);
        return true;
    }

    /**
     * Getting a new port to start the server with. The new port will be randomly determined.
     * @return the new port
     */
    private int aquireNewPort() {
        return port + new Random().nextInt(100);
    }

    /**
     * Returns true if the current exe server is not valid and we need to force a download.
     * @param filePath
     *            path to the exe of the server
     * @return true if the exe file needs to be downloaded again
     */
    private boolean exeIsNotValid(String filePath) {
        File exeFile = new File(filePath);
        if (!exeFile.exists() || !exeFile.isFile()) {
            LOG.debug("{} is not a file", filePath);
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
        if (OS.indexOf("win") >= 0) {
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
     * @param filePath
     *            path where the external server should be downloaded to
     * @param fileName
     *            name of the external server
     * @return path of the external server
     * @throws IOException
     *             {@link IOException} occurred while downloading the file
     */
    private String downloadExecutable(String filePath, String fileName) throws IOException {

        String tarFileName = "";
        File exeFile = new File(filePath);
        String parentDirectory = exeFile.getParent();

        URL url = new URL(serverDownloadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Downloading tar file
        File tarFile;
        Path tarPath;
        LOG.info("Downloading server from {} to {}", serverDownloadUrl, filePath);
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
                    if (entry.getName().contains(serverVersion)) {
                        // We don't want the directories (src/main/server.exe), we just want to create the
                        // file (server.exe)
                        File targetFile = new File(parentDirectory, fileName);
                        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
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
            throw new CobiGenRuntimeException(
                "Enable to extract the external server package. Possibly a corrupt download. Please try again");
        }

        // Remove tar file
        Files.deleteIfExists(tarPath);
        return filePath;

    }

    /**
     * Sends a dummy request to the server in order to check if it is not connected
     *
     * @return true if it is not connected
     */
    private boolean isConnectedAndValidService() {

        String response = get(ExternalProcessConstants.IS_CONNECTION_READY, MediaType.get("text/plain"));
        if (response.equals(serverVersion)) {
            return true;
        }

        if (response.equals("true")) {
            throw new CobiGenRuntimeException("The old version " + serverVersion + " of " + exeName
                + " is currently deployed. This should not happen as the nestserver is automatically deployed.");
        }
        return false;
    }

    /**
     * @return the base path including http protocol, hostname, port, and context path the server has been
     *         started with.
     */
    public String getBasePath() {
        return "http://" + hostName + ":" + port + contextPath;
    }

    /**
     * @return the {@link OkHttpClient} to send requests with
     */
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    protected void finalize() throws Throwable {
        if (process != null && process.getProcess() != null && process.getProcess().isAlive()) {
            LOG.info("Terminating TS Merger External Process {}", process.getProcess());
            process.getProcess().destroyForcibly();
        }
    }
}
