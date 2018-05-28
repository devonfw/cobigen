package com.devonfw.cobigen.impl.config.upgrade;

/**
 * Result object encompassing all results from a configuration upgrade step.
 * @author mbrunnli (Jun 23, 2015)
 */
public class ConfigurationUpgradeResult {

    /** The JAXB root node of the upgraded configuration */
    private Object resultConfigurationJaxbRootNode;

    /** States, whether manual adoptions are necessary after the automatic upgrade to work properly */
    private boolean manualAdoptionsNecessary;

    /**
     * Returns the field 'resultConfigurationJaxbRootNode'
     * @return value of resultConfigurationJaxbRootNode
     * @author mbrunnli (Jun 23, 2015)
     */
    public Object getResultConfigurationJaxbRootNode() {
        return resultConfigurationJaxbRootNode;
    }

    /**
     * Sets the field 'resultConfigurationJaxbRootNode'.
     * @param resultConfigurationJaxbRootNode
     *            new value of resultConfigurationJaxbRootNode
     * @author mbrunnli (Jun 23, 2015)
     */
    public void setResultConfigurationJaxbRootNode(Object resultConfigurationJaxbRootNode) {
        this.resultConfigurationJaxbRootNode = resultConfigurationJaxbRootNode;
    }

    /**
     * Returns the field 'manualAdoptionsNecessary'
     * @return value of manualAdoptionsNecessary
     * @author mbrunnli (Jun 23, 2015)
     */
    public boolean areManualAdoptionsNecessary() {
        return manualAdoptionsNecessary;
    }

    /**
     * Sets the field 'manualAdoptionsNecessary'.
     * @param manualAdoptionsNecessary
     *            new value of manualAdoptionsNecessary
     * @author mbrunnli (Jun 23, 2015)
     */
    public void setManualAdoptionsNecessary(boolean manualAdoptionsNecessary) {
        this.manualAdoptionsNecessary = manualAdoptionsNecessary;
    }

}
