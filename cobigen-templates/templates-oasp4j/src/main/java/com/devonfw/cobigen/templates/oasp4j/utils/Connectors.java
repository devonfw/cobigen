package utils;

import java.util.ArrayList;
import java.util.List;

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
    public String generateText() {
        String content = "";
        for (Connector connector : connectors) {
            String connectedClassName = connector.getClassName();
            String multiplicity = connector.getMultiplicity();
            if (multiplicity.equals("1")) {
                content =
                    content + "\n\n\tprivate " + connectedClassName + " " + connectedClassName.toLowerCase() + ";";
            } else if (multiplicity.equals("*")) {
                content = content + "\n\n\tprivate List<" + connectedClassName + "> "
                    + removePlural(connectedClassName.toLowerCase()) + "s;";
            }
        }

        for (Connector connector : connectors) {
            String connectedClassName = connector.getClassName();
            String multiplicity = connector.getMultiplicity();
            if (multiplicity.equals("1")) {
                content = content + "\n\n\t@Override" + "\n\tpublic " + connectedClassName + " get" + connectedClassName
                    + "(){" + "\n\t\treturn this." + connectedClassName.toLowerCase() + ";" + "\n\t}"
                    + "\n\n\t@Override" + "\n\tpublic void set" + connectedClassName + "(" + connectedClassName + " "
                    + connectedClassName.toLowerCase() + "){" + "\n\t\tthis." + connectedClassName.toLowerCase() + " = "
                    + connectedClassName.toLowerCase() + ";" + "\n\t}";
            } else if (multiplicity.equals("*")) {
                content =
                    content + "\n\n\tpublic List<" + connectedClassName + "> get" + removePlural(connectedClassName)
                        + "s(){" + "\n\t\treturn this." + removePlural(connectedClassName.toLowerCase()) + "s;"
                        + "\n\t}" + "\n\n\tpublic void set" + removePlural(connectedClassName) + "s(List<"
                        + connectedClassName + "> " + removePlural(connectedClassName.toLowerCase()) + "s){"
                        + "\n\t\tthis." + removePlural(connectedClassName.toLowerCase()) + "s = "
                        + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
            }
        }
        return content;
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
