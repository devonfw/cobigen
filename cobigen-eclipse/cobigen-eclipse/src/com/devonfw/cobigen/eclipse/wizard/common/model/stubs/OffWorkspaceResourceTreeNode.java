package com.devonfw.cobigen.eclipse.wizard.common.model.stubs;

import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

/** Resource indicating the root of any resource external from the workspace. */
public class OffWorkspaceResourceTreeNode {

    /** See {@link #getPath()} */
    private Path path;

    /** See {@link #getChildren()} */
    private List<OffWorkspaceResourceTreeNode> children = Lists.newArrayList();

    /** Parent of this node */
    private final OffWorkspaceResourceTreeNode parent;

    /**
     * Creates a new resource which is out of scope of the workspace
     * @param parent
     *            parent of this node
     * @param representingPath
     *            path represented by this resource
     */
    public OffWorkspaceResourceTreeNode(OffWorkspaceResourceTreeNode parent, Path representingPath) {
        this.parent = parent;
        path = representingPath;
    }

    /**
     * @return the absolute path for this tree node
     */
    public Path getAbsolutePath() {
        if (parent != null) {
            return parent.getAbsolutePath().resolve(path);
        } else {
            return path;
        }
    }

    /**
     * @return the absolute path for this tree node converted to a string
     */
    public String getAbsolutePathStr() {
        return getAbsolutePath().toString().replace("\\", "/");
    }

    /**
     * @return the path segment represented by this resource node
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return the path segment represented by this resource node converted to a string
     */
    public String getPathStr() {
        return path.toString().replace("\\", "/");
    }

    /**
     * Also see {@link #getPath()}.
     * @param representingPath
     *            the new path this node should represent
     */
    public void setPath(Path representingPath) {
        path = representingPath;
    }

    /**
     * Adds a child to the already existing children of this node
     * @param child
     *            new child to be added.
     */
    public void addChild(OffWorkspaceResourceTreeNode child) {
        children.add(child);
    }

    /**
     * @return the children of the tree node.
     */
    public List<OffWorkspaceResourceTreeNode> getChildren() {
        return children;
    }

    /**
     * @return {@code true} if this node is a file
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
