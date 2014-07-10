/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.eclipse.wizard.generatecustombatch;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.CustomBatch;
import com.capgemini.CustomBatches;
import com.capgemini.File;
import com.capgemini.Mapping;
import com.capgemini.TemplateRef;
import com.capgemini.cobigen.eclipse.common.constants.ConfigResources;
import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.tools.JavaModelUtil;
import com.capgemini.cobigen.eclipse.generator.java.JavaGeneratorWrapper;
import com.capgemini.cobigen.eclipse.wizard.generate.control.GenerateBatchSelectionProcess;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * "Generate Custom Batch"-Wizard
 * @author mbrunnli (20.03.2014)
 */
public class GenerateCustomBatchWizard extends Wizard {

    /**
     * Batch selection wizard page
     */
    private CustomBatchSeletionPage page1;

    /**
     * Mapping from custom batch ids to {@link CustomBatch}es
     */
    private Map<String, CustomBatch> customBatchIds;

    /**
     * Java generator wrapper for CobiGen application
     */
    private JavaGeneratorWrapper g;

    /**
     * Assigning logger to GenerateCustomBatchWizard
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenerateCustomBatchWizard.class);

    /**
     * Creates a new "Generate Custom Batch"-Wizard
     * @param targetProject
     *            target project for the generation
     * @param targetPath
     *            target path for generation to be matched against the batch root
     * @throws InvalidConfigurationException
     *             if the given configuration does not match the templates.xsd
     * @throws IOException
     *             if the generator project "RF-Generation" could not be accessed
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project "RF-Generation" is not existent
     * @throws CoreException
     *             if the generator configuration project could not be opened
     * @throws UnknownContextVariableException
     *             if the destination path contains an undefined context variable
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @author mbrunnli (12.05.2014)
     */
    public GenerateCustomBatchWizard(IProject targetProject, IPath targetPath)
        throws UnknownExpressionException, UnknownContextVariableException,
        GeneratorProjectNotExistentException, CoreException, IOException, InvalidConfigurationException {
        customBatchIds = loadCustomBatches(targetPath);
        page1 = new CustomBatchSeletionPage("Select Batch Runner", customBatchIds.keySet());

        g = new JavaGeneratorWrapper();
        g.setGenerationTargetProject(targetProject);
    }

    /**
     * Loads the custom batches which matches the target path
     * @param targetPath
     *            to be matched
     * @return a map of batch ids to the {@link CustomBatch} instances
     * @author mbrunnli (12.05.2014)
     */
    private Map<String, CustomBatch> loadCustomBatches(IPath targetPath) {
        IProject generatorProj;
        try {
            generatorProj = ConfigResources.getGeneratorConfigurationProject();
            IFile file = generatorProj.getFile(ConfigResources.CUSTOM_BATCHES_ROOT_CONFIG);
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(CustomBatches.class);
                Unmarshaller unmarschaller = context.createUnmarshaller();
                CustomBatches cb =
                    (CustomBatches) unmarschaller.unmarshal(new java.io.File(file.getLocationURI()));
                Map<String, CustomBatch> customBatchesIds = Maps.newHashMap();
                for (CustomBatch batch : cb.getCustomBatch()) {
                    if (targetPath.toString().matches("/[^/]+" + batch.getBatchRoot())) {
                        customBatchesIds.put(batch.getId(), batch);
                    }
                }
                return customBatchesIds;
            }
        } catch (GeneratorProjectNotExistentException e) {
            // should not occur, as otherwise the command could not be triggered
            LOG.error(e.toString());
        } catch (CoreException e) {
            // TODO inform user
            LOG.error(e.toString());
        } catch (JAXBException e) {
            // TODO inform user
            LOG.error(e.toString());
        }
        return Maps.newHashMap();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (20.03.2014)
     */
    @Override
    public boolean performFinish() {
        String selectedBatchId = page1.getSelectedBatchId();
        if (selectedBatchId != null && customBatchIds.get(selectedBatchId) != null) {
            CustomBatch batch = customBatchIds.get(selectedBatchId);

            ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

            if (!userConfirmed()) {
                return false;
            }

            for (Mapping m : batch.getMapping()) {
                List<IType> inputTypes = getInputTypesToGenerate(g, batch, m);
                if (inputTypes.size() > 0) {
                    try {
                        g.setInputType(inputTypes.get(0));
                        GenerateBatchSelectionProcess bp =
                            new GenerateBatchSelectionProcess(getShell(), g, getTemplatesToGenerate(g,
                                batch.getTrigger(), m), inputTypes);

                        dialog.run(false, false, bp);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    } catch (UnknownExpressionException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    } catch (UnknownContextVariableException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    } catch (CoreException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.toString());
                    }
                }
            }
            return true;

        }
        return false;
    }

    /**
     * Open up a confirmation dialog for the batch generation process
     * @return <code>true</code> if the user confirms<br>
     *         <code>false</code>, otherwise
     * @author mbrunnli (21.03.2014)
     */
    private boolean userConfirmed() {
        MessageDialog dialog =
            new MessageDialog(
                getShell(),
                "Warning!",
                null,
                "The customized batch process might override existing files with generated ones.<br>Do you want continue?",
                MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
        int result = dialog.open();
        if (result == 1 || result == SWT.DEFAULT) {
            return false;
        }
        return true;
    }

    /**
     * Retrieves all input types for a given {@link CustomBatch} and the selected {@link Mapping}
     * @param g
     *            java generator wrapper for CobiGen usage
     * @param batch
     *            {@link CustomBatch} for which all input types should be retrieved
     * @param mapping
     *            Mapping object which declares all files which are affected by the custom batch generator
     * @return the list of {@link IType}s which are available for all declared files in the mapping
     * @author mbrunnli (21.03.2014)
     */
    // TODO decouple from JAXB interface
    private List<IType> getInputTypesToGenerate(JavaGeneratorWrapper g, CustomBatch batch, Mapping mapping) {
        List<IType> typesToGenerate = Lists.newLinkedList();
        for (File f : mapping.getFiles().getFile()) {
            IFolder rootFolder = g.getGenerationTargetProject().getFolder(batch.getBatchRoot());
            if (rootFolder.exists()) {
                IFile iFile = rootFolder.getFile(f.getName());
                if (iFile.exists()) {
                    IJavaElement elem = JavaCore.create(iFile);
                    if (elem instanceof ICompilationUnit) {
                        IType type = JavaModelUtil.getJavaClassType((ICompilationUnit) elem);
                        typesToGenerate.add(type);
                    }
                }// TODO inform user about missing files
            } else {
                MessageDialog
                    .openError(
                        getShell(),
                        "Root folder does not exist!",
                        "The specified root folder "
                            + rootFolder.getFullPath().toString()
                            + " does not exist. Maybe it has been moved and the custom batch configuration has not been adapted yet");
            }
        }
        return typesToGenerate;
    }

    /**
     * Returns all templates to be generated for the given batch and the given mapping
     * @param g
     *            java generator wrapper for CobiGen usage
     * @param triggerId
     *            for the correlated {@link CustomBatch}
     * @param m
     *            Mapping declaring the templates
     * @return the {@link List} of {@link TemplateTo}s
     * @author mbrunnli (21.03.2014)
     */
    private List<TemplateTo> getTemplatesToGenerate(JavaGeneratorWrapper g, String triggerId, Mapping m) {
        List<TemplateTo> templatesToGenerate = Lists.newLinkedList();
        for (TemplateRef t : m.getTemplates().getTemplate()) {
            templatesToGenerate.add(g.getTemplateForId(t.getIdref(), triggerId));
        }
        return templatesToGenerate;
    }

    /**
     * 
     * {@inheritDoc}
     * @author mbrunnli (20.03.2014)
     */
    @Override
    public void addPages() {
        addPage(page1);
    }

}
