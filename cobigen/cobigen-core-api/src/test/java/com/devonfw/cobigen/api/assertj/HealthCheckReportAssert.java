package com.devonfw.cobigen.api.assertj;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.assertj.core.api.AbstractAssert;
import org.custommonkey.xmlunit.XMLTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.HealthCheckReport;

/**
 * AssertJ assertion for {@link GenerationReportTo}.
 */
public class HealthCheckReportAssert extends AbstractAssert<HealthCheckReportAssert, HealthCheckReport> {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(HealthCheckReportAssert.class);

  /**
   * The constructor.
   *
   * @param actual {@link HealthCheckReport} to be asserted
   */
  public HealthCheckReportAssert(HealthCheckReport actual) {

    super(actual, HealthCheckReportAssert.class);
  }

  /**
   * Checks whether the {@link GenerationReportTo} reports a successful generation. In case of a non-successful
   * generation report, warnings and errors will be reported in the assertion error message.
   *
   * @return the {@link HealthCheckReportAssert}
   */

  public HealthCheckReportAssert isSuccessful() {

    try {
      assertThat(this.actual.getErrorMessages()).overridingErrorMessage(
          "Upgrade was not successful. Error Messages: %s // Please see the printed stack traces in the LOG.",
          this.actual.getErrorMessages()).isEmpty();
      assertThat(this.actual.getErrors()).overridingErrorMessage(
          "Upgrade was not successful. Error Messages: %s // Errors: %s. Please see the printed stack traces in the LOG.",
          this.actual.getErrorMessages(), this.actual.getErrors()).isEmpty();
    } catch (AssertionError e) {
      for (Throwable entry : this.actual.getErrors()) {
        LOG.error(entry.getMessage(), entry);
      }
      throw e;
    }
    return this;
  }

  /**
   * Returns the version of the document given and the version of the current template verison
   *
   * @param configRoot root directory of the configuration
   * @param version is the current context version
   * @return the version of the document given and the version of the current template verison
   * @throws Exception if a problem with the comparison occurs
   */
  public HealthCheckReportAssert isOfContextVersion(Path configRoot, String version) throws Exception {

    Path contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = factory.newDocumentBuilder();
    try (InputStream in = Files.newInputStream(contextFile)) {
      Document doc = db.parse(in);

      new XMLTestCase() {
      }.assertXpathEvaluatesTo(version, "/contextConfiguration/@version", doc);
    }

    return this;
  }

  /**
   * Return the version of the document given and the version of the current template version
   *
   * @param configRoot root directory of the template file
   * @param version is the current template version
   * @return the version of the document given and the version of the current template version
   * @throws Exception if a problem with the comparison occurs
   */
  public HealthCheckReportAssert isOfTemplatesVersion(Path configRoot, String version) throws Exception {

    Path templateFile = configRoot.resolve(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = factory.newDocumentBuilder();
    try (InputStream in = Files.newInputStream(templateFile)) {
      Document doc = db.parse(in);

      new XMLTestCase() {
      }.assertXpathEvaluatesTo(version, "/templatesConfiguration/@version", doc);
    }

    return this;
  }
}
