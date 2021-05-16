package com.devonfw.cobigen.impl.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * FileSystem utils.
 */
public class FileSystemUtil {

    /**
     * Creates the {@link Path} dependent on the {@link FileSystem} to be retrieved or created due to the
     * {@link URI} declaration. This function will also check whether the target of the {@link URI} is a zip
     * file. When it is, a new {@link FileSystem} will be created (or retrieved if already opened), which
     * represents the zip-file contents.
     * @param targetUri
     *            {@link URI}, the {@link Path} should be resolved for.
     * @return the path representing the configuration root
     */
    public static Path createFileSystemDependentPath(URI targetUri) {

        URI workURI = URI.create(targetUri.toString());
        if (FileSystemUtil.isZipFile(workURI)) {
            // reformat URI to be loaded by the zip/jar file system provider
            workURI = URI.create("jar:file:" + new File(workURI).toURI().getPath());
        }

        FileSystem configFileSystem;
        Path configFolder;
        if ("file".equals(workURI.getScheme())) {
            // Windows file system provider only allows "/" as URI to create a new file system... crap...
            configFolder = Paths.get(workURI);
        } else {
            configFileSystem = getOrCreateFileSystem(workURI);
            configFolder = configFileSystem.getPath("/");
        }
        return configFolder;
    }

    /**
     * Creates a new {@link FileSystem} if necessary or retrieves an already opened one for the given
     * {@link URI}
     * @param fileSystemUri
     *            {@link URI} of the {@link FileSystem} to be retrieved/created
     * @return the {@link FileSystem} of the given {@link URI}
     */
    public static FileSystem getOrCreateFileSystem(URI fileSystemUri) {
        FileSystem configFileSystem;
        try {
            configFileSystem = FileSystems.getFileSystem(fileSystemUri);
            if (!configFileSystem.isOpen()) {
                throw new FileSystemNotFoundException();
            }
        } catch (FileSystemNotFoundException e) {
            try {
                configFileSystem = FileSystems.newFileSystem(fileSystemUri, Collections.EMPTY_MAP);
            } catch (IOException e1) {
                throw new CobiGenRuntimeException("Unable to create file system from URI " + fileSystemUri, e);
            }
        }
        return configFileSystem;
    }

    /**
     * Determine whether a file is a ZIP File.
     * @param uri
     *            {@link URI} to be checked
     * @return <code>true</code> if the file is a zip/jar file
     */
    public static boolean isZipFile(URI uri) {
        File file = new File(uri);
        if (file.isDirectory()) {
            return false;
        }
        if (!file.canRead()) {
            throw new CobiGenRuntimeException("No permission to read file " + file.getAbsolutePath());
        }
        if (file.length() < 4) {
            return false;
        }

        // check "magic number" in the first 4bytes to indicate zip/jar resources
        try (FileInputStream fileInputstream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputstream);
            DataInputStream in = new DataInputStream(bufferedInputStream)) {
            int test = in.readInt();
            in.close();
            return test == 0x504b0304;
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Unable to read file " + file.getAbsolutePath(), e);
        }
    }
}
