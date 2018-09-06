package com.devonfw.cobigen.impl.generator.api;

import java.util.List;

import com.devonfw.cobigen.impl.config.entity.Trigger;

/** Input resolver especially resolving containers. */
public interface InputResolver {

    /**
     * Resolves any input, which serves as a container and returns the list of contained inputs, which are
     * valid for generation.
     * @param input
     *            container to resolve
     * @param trigger
     *            used for element resolution
     * @return the list of matching entries in the container or the input itself if it is not a container.
     */
    public List<Object> resolveContainerElements(Object input, Trigger trigger);
}
