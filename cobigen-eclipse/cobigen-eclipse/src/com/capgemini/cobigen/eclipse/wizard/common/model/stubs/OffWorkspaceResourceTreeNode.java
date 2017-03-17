package com.capgemini.cobigen.eclipse.wizard.common.model.stubs;

import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

/** Resource indicating the root of any resource external from the workspace. */
public class OffWorkspaceResourceTreeNode {

    /** See {@link #getPath()} */
    private Path path;

    /** See {@link #getChildren()} */
    private List<OffWorkspaceResourceTreeNode> children = Lists.newArrayList();

    /**
     * Creates a new resource which is out of scope of the workspace
     * @param representingPath
     *            path represented by this resource
     */
    public OffWorkspaceResourceTreeNode(Path representingPath) {
        path = representingPath;
    }

    /**
     * @return the path represented by this resource node
     */
    public Path getPath() {
        return path;
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
}
