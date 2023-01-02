package com.devonfw.cobigen.eclipse.wizard.common.model.stubs;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;

/**
 * Stub for {@link IFile} in order to simulate resources in the generate wizard
 */
public class IFileStub extends IResourceStub implements IFile {

  @Override
  public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {

  }

  @Override
  public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public String getCharset() throws CoreException {

    return null;
  }

  @Override
  public String getCharset(boolean checkImplicit) throws CoreException {

    return null;
  }

  @Override
  public String getCharsetFor(Reader reader) throws CoreException {

    return null;
  }

  @Override
  public IContentDescription getContentDescription() throws CoreException {

    return null;
  }

  @Override
  public InputStream getContents() throws CoreException {

    return null;
  }

  @Override
  public InputStream getContents(boolean force) throws CoreException {

    return null;
  }

  @Override
  public int getEncoding() throws CoreException {

    return 0;
  }

  @Override
  public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {

    return null;
  }

  @Override
  public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {

  }

  @Override
  public void setCharset(String newCharset) throws CoreException {

  }

  @Override
  public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {

  }

  @Override
  public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {

  }

  @Override
  public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {

  }

  @Override
  public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {

  }

}
