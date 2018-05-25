package com.devonfw.cobigen.tempeng.velocity.runtime.resources;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;

/**
 * This class tries to tackle the recurring 'ResourceManagerImpl doesn't implement ResourceManager' error. It
 * is a simple delegate to the a ResourceManagerImpl instance implementing the ResourceManager interface
 */
public class ResourceManagerDelegate implements ResourceManager {

    /**
     * The ResourceManager all method calls are delegated to
     */
    private final ResourceManager manager = new ResourceManagerImpl();

    @Override
    public String getLoaderNameForResource(String arg0) {
        return manager.getLoaderNameForResource(arg0);
    }

    @Override
    public Resource getResource(String arg0, int arg1, String arg2)
        throws ResourceNotFoundException, ParseErrorException {
        return manager.getResource(arg0, arg1, arg2);
    }

    @Override
    public void initialize(RuntimeServices arg0) {
        manager.initialize(arg0);
    }

}
