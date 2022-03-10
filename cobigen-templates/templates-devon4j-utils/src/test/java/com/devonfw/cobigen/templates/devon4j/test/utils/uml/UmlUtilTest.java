package com.devonfw.cobigen.templates.devon4j.test.utils.uml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.xerces.dom.DeferredNode;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.devonfw.cobigen.templates.devon4j.utils.uml.Connector;
import com.devonfw.cobigen.templates.devon4j.utils.uml.UmlUtil;

/**
 *
 */
@SuppressWarnings("restriction")
public class UmlUtilTest {

  Element root;

  @Before
  public void beforeAll() throws Exception {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();

    StringBuilder xmlStringBuilder = new StringBuilder();
    xmlStringBuilder.append(FileUtils.readFileToString(
        new File("src/test/java/com/devonfw/cobigen/templates/devon4j/test/utils/resources/uml/completeUmlXmi.xml"),
        "UTF-8"));
    ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
    Document doc = builder.parse(input);

    this.root = doc.getDocumentElement();
  }

  @Test
  public void testSettingConnectorsContent() {

    List<Connector> connectorList = getConnectors();

    assertThat(connectorList).hasSize(3);
    for (Connector con : connectorList) {
      if (con.getClassName().equals("Student")) {
        assertThat(con.getCounterpartName()).isEqualTo("Marks");
        assertThat(con.getCounterpartMultiplicity()).isEqualTo("*");
        assertThat(con.getMultiplicity()).isEqualTo("1");

      } else if (con.getClassName().equals("Teacher")) {
        assertThat(con.getCounterpartName()).isEqualTo("Student");
        assertThat(con.getCounterpartMultiplicity()).isEqualTo("1");
        assertThat(con.getMultiplicity()).isEqualTo("1");

      } else if (con.getClassName().equals("Teacher")) {
        assertThat(con.getCounterpartName()).isEqualTo("Student");
        assertThat(con.getCounterpartMultiplicity()).isEqualTo("*");
        assertThat(con.getMultiplicity()).isEqualTo("*");
      }
    }
  }

  @Test
  public void testGeneratingText() {

    List<Connector> connectors = getConnectors();

  }

  private List<Connector> getConnectors() {

    UmlUtil uml = new UmlUtil();
    Node source = null;
    Node target = null;
    String className = "";
    NodeList connectors = this.root.getElementsByTagName("connector");
    for (int i = 0; i < connectors.getLength(); i++) {
      Node connector = connectors.item(i);
      NodeList attributes = connector.getChildNodes();
      for (int j = 0; j < attributes.getLength(); j++) {
        DeferredNode attr = (DeferredNode) attributes.item(j);
        if (attr.getNodeName().equals("source")) {
          source = attr;
          NodeList sourceAttributes = attr.getChildNodes();
          for (int k = 0; k < sourceAttributes.getLength(); k++) {
            Node sourceAttr = sourceAttributes.item(k);
            if (sourceAttr.getNodeName().equals("model")) {
              className = sourceAttr.getAttributes().getNamedItem("name").getTextContent();
            }
          }
        } else if (attr.getNodeName().equals("target")) {
          target = attr;
        }
      }
      uml.resolveConnectorsContent(source, target, className);
    }
    return uml.getConnectors();
  }

}