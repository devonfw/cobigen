/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config.reader;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.cobigen.config.entity.ContainerMatcher;
import com.capgemini.cobigen.config.entity.Increment;
import com.capgemini.cobigen.config.entity.Matcher;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.ITriggerInterpreter;

/**
 * This {@link TestCase} tests the {@link TemplatesConfigurationReader}
 *
 * @author mbrunnli (18.06.2013)
 */
public class TemplatesConfigurationReaderTest extends Assert {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/TemplatesConfigurationReaderTest/";

  /**
   * Tests whether all templates of a template package could be retrieved successfully
   *
   * @author mbrunnli (18.06.2013)
   */
  @Test
  public void testTemplatesOfAPackageRetrieval() {

    // given
    TemplatesConfigurationReader target =
        new TemplatesConfigurationReader(new File(testFileRootPath + "templates.xml"));

    Trigger trigger =
        new Trigger("", "asdf", "", Charset.forName("UTF-8"), new LinkedList<Matcher>(),
            new LinkedList<ContainerMatcher>());
    Template templateMock = mock(Template.class);
    ITriggerInterpreter triggerInterpreter = null;
    String templateIdSpringCommon = "resources_resources_spring_common";

    // when
    Map<String, Template> templates = target.loadTemplates(trigger, triggerInterpreter);
    Map<String, Increment> increments = target.loadIncrements(templates, trigger);

    // then
    assertNotNull(templates);
    Template templateSpringCommon = templates.get(templateIdSpringCommon);
    assertNotNull(templateSpringCommon);
    assertEquals(templateIdSpringCommon, templateSpringCommon.getId());
    assertEquals("resources/resources/spring/common.ftl", templateSpringCommon.getTemplateFile());
    assertEquals("src/main/resources/resources/spring/common.xml", templateSpringCommon.getUnresolvedDestinationPath());
    assertNull(templateSpringCommon.getMergeStrategy());

    String templateIdFooClass = "prefix_FooClass";
    Template templateFooClass = templates.get(templateIdFooClass);
    assertNotNull(templateFooClass);
    assertEquals(templateIdFooClass, templateFooClass.getId());
    assertEquals("foo/FooClass.ftl", templateFooClass.getTemplateFile());
    assertEquals("src/main/java/foo/FooClass.java", templateFooClass.getUnresolvedDestinationPath());
    assertNull(templateFooClass.getMergeStrategy());

    // this one is a predefined template and shall not be overriden by scan...
    String templateIdFoo2Class = "prefix_Foo2Class";
    Template templateFoo2Class = templates.get(templateIdFoo2Class);
    assertNotNull(templateFoo2Class);
    assertEquals(templateIdFoo2Class, templateFoo2Class.getId());
    assertEquals("foo/Foo2Class.ftl", templateFoo2Class.getTemplateFile());
    assertEquals("src/main/java/foo/Foo2Class${variable}.java", templateFoo2Class.getUnresolvedDestinationPath());
    assertEquals("javaMerge", templateFoo2Class.getMergeStrategy());

    String templateIdBarClass = "prefix_BarClass";
    Template templateBarClass = templates.get(templateIdBarClass);
    assertNotNull(templateBarClass);
    assertEquals(templateIdBarClass, templateBarClass.getId());
    assertEquals("foo/bar/BarClass.ftl", templateBarClass.getTemplateFile());
    assertEquals("src/main/java/foo/bar/BarClass.java", templateBarClass.getUnresolvedDestinationPath());
    assertNull(templateBarClass.getMergeStrategy());
  }

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() {

    new TemplatesConfigurationReader(new File(testFileRootPath + "templates_faulty.xml"));
  }

}
