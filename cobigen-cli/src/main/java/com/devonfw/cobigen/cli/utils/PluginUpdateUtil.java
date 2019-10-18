package com.devonfw.cobigen.cli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class facilitates to check plugin is outdated or not as per central plugin and uprovide latest plugin
 * version .
 */
public class PluginUpdateUtil {

    /**
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @param artificialDependency
     *            artificialDependency defines to plugin whichone compare with maven central plugin
     * @return this method return the latest plugin version
     */
    public static String latestPluginVersion(String artificialDependency)
        throws MalformedURLException, IOException, ParserConfigurationException {
        String mavenUrl =
            "https://repo.maven.apache.org/maven2/com/devonfw/cobigen/" + artificialDependency + "/maven-metadata.xml";

        HttpURLConnection conn = initializeConnection(mavenUrl);
        Document document = null;
        DocumentBuilder builder = null;
        String conditionText = "";
        try (InputStream inputStream = conn.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(inputStream);
            } catch (SAXException e) {
                e.printStackTrace();
            }
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
                            conditionText = condition.getFirstChild().getNodeValue();

                        }
                    }

                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return conditionText;
    }

    /**
     * Initializes a new connection to the specified Maven URL
     * @param mavenUrl
     *            the URL we need to connect to
     * @return the connection instance
     * @throws MalformedURLException
     *             if the URL is invalid
     * @throws IOException
     *             if we could not connect properly
     * @throws ProtocolException
     *             if the request protocol is invalid
     */
    private static HttpURLConnection initializeConnection(String mavenUrl)
        throws MalformedURLException, IOException, ProtocolException {
        URL url = new URL(mavenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    }

}
