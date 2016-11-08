package com.capgemini.cobigen.impl.util;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Java NIO Visitor used for copying directories by the
 * {@link Files#walkFileTree(Path, java.nio.file.FileVisitor)} API.
 */
public class CopyDirectoryVisitor extends SimpleFileVisitor<Path> {

    /** Source path */
    private final Path srcRootPath;

    /** Target path */
    private final Path targetRootPath;

    /** Copy options */
    private final CopyOption copyOption;

    /**
     * Creates a new {@link FileVisitor} for copying directories.
     * @param srcRootPath
     *            source root path
     * @param targetRootPath
     *            target root path
     * @param copyOption
     *            Copy option
     */
    public CopyDirectoryVisitor(Path srcRootPath, Path targetRootPath, CopyOption copyOption) {
        this.srcRootPath = srcRootPath;
        this.targetRootPath = targetRootPath;
        this.copyOption = copyOption;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path targetPath = targetRootPath.resolve(srcRootPath.relativize(dir));
        if (!Files.exists(targetPath)) {
            Files.createDirectory(targetPath);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, targetRootPath.resolve(srcRootPath.relativize(file)), copyOption);
        return FileVisitResult.CONTINUE;
    }
}