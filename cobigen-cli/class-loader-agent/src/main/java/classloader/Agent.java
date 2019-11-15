package src.main.java.classloader;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

/**
 * Java agent able to modify byte-code on runtime. We use it for loading external jars to the CobiGen CLI
 */
public class Agent {
    /**
     * Instrumentation is the addition of byte-codes to methods for the purpose of gathering data to be
     * utilized by tools
     */
    public static Instrumentation instrumentation;

    /**
     * @param args
     *            args to be passed to the agent (automatically filled by JVM)
     * @param instrumentation
     *            tool to add byte-codes (automatically filled by JVM)
     */
    @SuppressWarnings("unused")
    public static void premain(String args, Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
    }

    /**
     * @param args
     *            args to be passed to the agent (automatically filled by JVM)
     * @param instrumentation
     *            tool to add byte-codes (automatically filled by JVM)
     */
    @SuppressWarnings("unused")
    public static void agentmain(String args, Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
    }

    /**
     * Append an external Jar to the running CLI. Used to load CobiGen plug-ins to the CLI
     * @param file
     *            jar file to load
     */
    public static void appendJarFile(JarFile file) {
        if (instrumentation != null) {
            instrumentation.appendToSystemClassLoaderSearch(file);
        }
    }
}
