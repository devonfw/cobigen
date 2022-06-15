package com.devonfw.cobigen.impl.config.upgrade;

import java.nio.file.Path;

/**
 * Result object encompassing all results from a configuration upgrade step.
 *
 * @author mbrunnli (Jun 23, 2015)
 */
public class ConfigurationUpgradeResult {

	/** The JAXB root node of the upgraded configuration */
	private Object resultConfigurationJaxbRootNode;

	/** The Path to the upgraded configuration */
	private Path configurationPath;

	/**
	 * Returns the value of the field 'configurationPath'
	 *
	 * @return {@link Path} to the configuration
	 */
	public Path getConfigurationPath() {
		return configurationPath;
	}

	/**
	 * Sets the field 'configurationPath'
	 *
	 * @param {@link Path} to the configuration
	 */
	public void setConfigurationPath(Path configurationPath) {
		this.configurationPath = configurationPath;
	}

	/**
	 * States, whether manual adoptions are necessary after the automatic upgrade to
	 * work properly
	 */
	private boolean manualAdoptionsNecessary;

	/**
	 * Returns the field 'resultConfigurationJaxbRootNode'
	 *
	 * @return value of resultConfigurationJaxbRootNode
	 * @author mbrunnli (Jun 23, 2015)
	 */
	public Object getResultConfigurationJaxbRootNode() {

		return this.resultConfigurationJaxbRootNode;
	}

	/**
	 * Sets the field 'resultConfigurationJaxbRootNode' and 'configurationPath'.
	 *
	 * @param resultConfigurationJaxbRootNode new value of
	 *                                        resultConfigurationJaxbRootNode
	 * @param {{@link Path} new value of configurationPath
	 * @author mbrunnli (Jun 23, 2015)
	 */
	public void setResultConfigurationJaxbRootNodeAndPath(Object resultConfigurationJaxbRootNode, Path configurationPath) {

		this.resultConfigurationJaxbRootNode = resultConfigurationJaxbRootNode;
		this.configurationPath = configurationPath;
	}

	/**
	 * Returns the field 'manualAdoptionsNecessary'
	 *
	 * @return value of manualAdoptionsNecessary
	 * @author mbrunnli (Jun 23, 2015)
	 */
	public boolean areManualAdoptionsNecessary() {

		return this.manualAdoptionsNecessary;
	}

	/**
	 * Sets the field 'manualAdoptionsNecessary'.
	 *
	 * @param manualAdoptionsNecessary new value of manualAdoptionsNecessary
	 * @author mbrunnli (Jun 23, 2015)
	 */
	public void setManualAdoptionsNecessary(boolean manualAdoptionsNecessary) {

		this.manualAdoptionsNecessary = manualAdoptionsNecessary;
	}

}
