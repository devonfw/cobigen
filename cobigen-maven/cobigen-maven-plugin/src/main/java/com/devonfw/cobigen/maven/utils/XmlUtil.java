package com.devonfw.cobigen.maven.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlUtil {

  /**
   * Parses an xml file to the corresponding DOM.
   *
   * @param file the file (xml) to parse
   * @return a DOM {@link Document} according to the
   * @throws IOException thrown if input File cannot be found
   * @throws SAXException thrown if input file is not a valid xml document
   * @throws ParserConfigurationException thrown if document builder is configured wrong
   */
  public static Document parseXmlFileToDom(File file) throws SAXException, IOException, ParserConfigurationException {

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document parsedDocument = dBuilder.parse(file);

    return parsedDocument;
  }

  /**
   * Parses an xml inputstream to the corresponding DOM.
   *
   * @param stream the inputstream (xml) to parse
   * @return a DOM {@link Document} according to the
   * @throws IOException thrown if input File cannot be found
   * @throws SAXException thrown if input file is not a valid xml document
   * @throws ParserConfigurationException thrown if document builder is configured wrong
   */
  public static Document parseXmlStreamToDom(InputStream stream)
      throws SAXException, IOException, ParserConfigurationException {

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document parsedDocument = dBuilder.parse(stream);

    return parsedDocument;
  }

}
