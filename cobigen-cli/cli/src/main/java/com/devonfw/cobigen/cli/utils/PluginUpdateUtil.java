package com.devonfw.cobigen.cli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.devonfw.cobigen.cli.CobiGenCLI;

/**
 * This class facilitates to check plug-in is outdated or not as per central plug-in and provide latest
 * plug-in version .
 */
public class PluginUpdateUtil {

    /**
     * Logger to output useful information to the user
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Checks which is the last version of a plug-in. It connects to Maven central in order to find this
     * information
     * @param artificialDependency
     *            artificialDependency defines which plugin to compare with Maven central
     * @return this method return the latest plugin version
     */
    public static String latestPluginVersion(String artificialDependency) {
        String mavenUrl =
            "https://repo.maven.apache.org/maven2/com/devonfw/cobigen/" + artificialDependency + "/maven-metadata.xml";

        HttpURLConnection conn = initializeConnection(mavenUrl);

        Document document = null;
        DocumentBuilder builder = null;
        String latestVersionString = "";
        try (InputStream inputStream = conn.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            builder = factory.newDocumentBuilder();
            document = builder.parse(inputStream);

            NodeList nList = document.getElementsByTagName("metadata");
            for (int i = 0; i <= nList.getLength(); i++) {
                Node mainNode = nList.item(i);
                if ((mainNode != null) && (mainNode.getNodeType() == Node.ELEMENT_NODE)) {
                    Element firstElement = (Element) mainNode;
                    NodeList forumidNameList = firstElement.getElementsByTagName("versioning");
                    for (int j = 0; j < forumidNameList.getLength(); ++j) {
                        Element value = (Element) forumidNameList.item(j);

                        NodeList conditionList = value.getElementsByTagName("latest");
                        for (int k = 0; k < conditionList.getLength(); ++k) {
                            Element condition = (Element) conditionList.item(k);
                            latestVersionString = condition.getFirstChild().getNodeValue();

                        }
                    }

                }

            }

        } catch (IOException e) {
            LOG.error("Error while creating an input stream to read Maven metadata file. Please try again.", e);
        } catch (SAXException | ParserConfigurationException e) {
            LOG.error("Not able to parse the Maven metadata file in order to find the latest plug-in version. "
                + "Please check your connection and try again", e);
        }

        return latestVersionString;
    }

    /**
     * Initializes a new connection to the specified Maven URL
     * @param mavenUrl
     *            the URL we need to connect to
     * @return the connection instance
     *
     */
    private static HttpURLConnection initializeConnection(String mavenUrl) {
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(mavenUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
        } catch (IOException e) {
            LOG.error("Not able to initialize connection to Maven Central. Please check your connection and try again.",
                e);
        }
        return conn;
    }

}
