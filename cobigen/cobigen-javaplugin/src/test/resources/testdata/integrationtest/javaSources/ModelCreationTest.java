package com.capgemini.cobigen.javaplugin.integrationtest;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ContextConfiguration.ContextSetting;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import com.capgemini.cobigen.javaplugin.util.JavaModelUtil;
import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;

/**
 *
 * @author mbrunnli (22.01.2015)
 */
public class ModelCreationTest extends AbstractIntegrationTest {

    private List<String> testField;

}
