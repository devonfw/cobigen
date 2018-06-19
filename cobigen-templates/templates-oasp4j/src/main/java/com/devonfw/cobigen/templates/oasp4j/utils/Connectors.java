package com.devonfw.cobigen.templates.oasp4j.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Class that contains every connector found for one class and generates the resultant text for the template.
 */
public class Connectors {

    private List<Connector> connectors;

    public Connectors() {
        connectors = new ArrayList<Connector>();
    }

    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    /**
     * @return
     */
    public String generateText(boolean isImpl, boolean isOverride, String className) {
        String content = "";
        if (isImpl) {
            for (Connector connector : connectors) {
                String connectedClassName = connector.getCounterpartName();
                String multiplicity = connector.getMultiplicity();
                if (multiplicity.equals("1")) {
                    content +=
                        "\n\n\tprivate " + connectedClassName + "Entity " + connectedClassName.toLowerCase() + ";";
                } else if (multiplicity.equals("*")) {
                    content += "\n\n\tprivate List<" + connectedClassName + "Entity> "
                        + removePlural(connectedClassName.toLowerCase()) + "s;";
                }
            }
        }

        for (Connector connector : connectors) {
            String connectedClassName = connector.getCounterpartName();
            String multiplicity = connector.getMultiplicity();
            if (multiplicity.equals("1")) {

                content += "\n\n\t";
                if (isOverride) {
                    content += "@Override\n\t";
                }
                if (isImpl) {
                    content += getRelationshipAnnotations(connector) + "\n\t";
                }
                content += "public " + connectedClassName + "Entity get" + connectedClassName + "()";
                if (isImpl) {
                    content += "{" + "\n\t\treturn this." + connectedClassName.toLowerCase() + ";" + "\n\t}";
                } else {
                    content += ";";
                }

                content += "\n\n\t";
                if (isOverride) {
                    content += "@Override\n\t";
                }
                content += "public void set" + connectedClassName + "(" + connectedClassName + "Entity "
                    + connectedClassName.toLowerCase() + ")";
                if (isImpl) {
                    content += "{" + "\n\t\tthis." + connectedClassName.toLowerCase() + " = "
                        + connectedClassName.toLowerCase() + ";" + "\n\t}";
                } else {
                    content += ";";
                }

            } else if (multiplicity.equals("*")) {

                content += "\n\n\t";
                if (isOverride) {
                    content += "@Override\n\t";
                }
                if (isImpl) {
                    content += getRelationshipAnnotations(connector) + "\n\t";
                }
                content +=
                    "public List<" + connectedClassName + "Entity> get" + removePlural(connectedClassName) + "s()";
                if (isImpl) {
                    content +=
                        "{" + "\n\t\treturn this." + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
                } else {
                    content += ";";
                }

                content += "\n\n\t";
                if (isOverride) {
                    content += "@Override\n\t";
                }
                content += "public void set" + removePlural(connectedClassName) + "s(List<" + connectedClassName
                    + "Entity> " + removePlural(connectedClassName.toLowerCase()) + "s)";
                if (isImpl) {
                    content += "{" + "\n\t\tthis." + removePlural(connectedClassName.toLowerCase()) + "s = "
                        + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
                } else {
                    content += ";";
                }
            }
        }
        return content;
    }

    private String getRelationshipAnnotations(Connector source) {
        String relationship = "";
        if (source.ISSOURCE) {
            if (source.getMultiplicity().equals("*")) {
                if (source.getCounterpartMultiplicity().equals("*")) {
                    relationship += "@ManyToMany()";
                    relationship += "\n\t@JoinTable(name = \"" + WordUtils.capitalize(source.getCounterpartName())
                        + WordUtils.capitalize(source.getClassName()) + "\", joinColumns = @JoinColumn(name = \"id"
                        + WordUtils.capitalize(source.getClassName())
                        + "\"), inverseJoinColumns = @JoinColumn(name = \"id"
                        + WordUtils.capitalize(source.getCounterpartName()) + "\"))";
                } else if (source.getCounterpartMultiplicity().equals("1")) {
                    relationship += "@ManyToOne(fetch = FetchType.LAZY)\n\t@JoinColumn(name = \"id"
                        + WordUtils.capitalize(source.getCounterpartName()) + "\")";
                }
            } else if (source.getMultiplicity().equals("1")) {
                if (source.getCounterpartMultiplicity().equals("*")) {
                    relationship = "@OneToMany(fetch = FetchType.LAZY\")\n\t@JoinColumn(name = \"id"
                        + WordUtils.capitalize(source.getCounterpartName()) + "\")";
                } else if (source.getCounterpartMultiplicity().equals("1")) {
                    relationship =
                        "@OneToOne()" + "\n\t@JoinColumn(name = \"" + source.getClassName().toLowerCase() + "\")";
                }
            }
        } else if (source.ISTARGET) {
            if (source.getCounterpartMultiplicity().equals("*")) {
                if (source.getMultiplicity().equals("*")) {
                    relationship +=
                        "@ManyToMany(mappedBy = \"" + removePlural(source.getCounterpartName()).toLowerCase() + "s\")";
                } else if (source.getMultiplicity().equals("1")) {
                    relationship = "@OneToMany(fetch = FetchType.LAZY, mappedBy = \""
                        + source.getCounterpartName().toLowerCase() + "\")";
                }
            } else if (source.getCounterpartMultiplicity().equals("1")) {
                if (source.getMultiplicity().equals("*")) {
                    relationship += "@ManyToOne(fetch = FetchType.LAZY)\n\t@JoinColumn(name = \"id"
                        + WordUtils.capitalize(source.getCounterpartName()) + "\")";
                } else if (source.getMultiplicity().equals("1")) {
                    relationship +=
                        "@OneToOne(mappedBy = \"id" + WordUtils.capitalize(source.getCounterpartName()) + "\")";
                }
            }
        }
        return relationship;
    }

    public ArrayList<String> getConnectedClasses() {
        ArrayList<String> connectedClasses = new ArrayList<String>();

        for (Connector connector : connectors) {
            connectedClasses.add(connector.getClassName());
        }

        return connectedClasses;
    }

    /**
     * If the string last character is an 's', then it gets removed
     * @param targetClassName
     * @return
     */
    private String removePlural(String targetClassName) {
        // Remove last 's' for Many multiplicity
        if (targetClassName.charAt(targetClassName.length() - 1) == 's') {
            targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
        }
        return targetClassName;
    }
}
