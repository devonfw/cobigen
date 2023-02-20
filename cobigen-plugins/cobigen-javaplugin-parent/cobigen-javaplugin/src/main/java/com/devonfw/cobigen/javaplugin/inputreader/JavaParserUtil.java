package com.devonfw.cobigen.javaplugin.inputreader;

import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import io.github.mmm.code.impl.java.JavaContext;
import io.github.mmm.code.impl.java.source.maven.JavaSourceProviderUsingMaven;
import io.github.mmm.code.impl.java.source.maven.MavenDependencyCollector;
import io.github.mmm.code.java.maven.impl.MavenBridgeImpl;

/**
 * The {@link JavaParserUtil} class provides helper functions for generating
 * parsed inputs
 */
public class JavaParserUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(JavaParserUtil.class);

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all
     * upcoming parsed java files will be
     * added to the class library
     *
     * @param reader {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     */
    public static JavaClass getFirstJavaClass(Reader... reader) {

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        return getFirstJavaClass(classLibraryBuilder, reader);
    }

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all
     * upcoming parsed java files will be
     * added to the class library. By passing a {@link ClassLoader}, you can take
     * impact on the class name resolving
     *
     * @param classLoader which should be used for class name resolving
     * @param reader      {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     */
    public static JavaClass getFirstJavaClass(ClassLoader classLoader, Reader... reader) {

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendClassLoader(classLoader);
        return getFirstJavaClass(classLibraryBuilder, reader);
    }

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all
     * upcoming parsed java files will be
     * added to the class library. Furthermore, a pre-built
     * {@link ClassLibraryBuilder} should be passed, which should be
     * previously enriched by all necessary {@link ClassLoader}s.
     *
     * @param classLibraryBuilder {@link ClassLibraryBuilder} to build the sources
     *                            with
     * @param reader              {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     */
    private static JavaClass getFirstJavaClass(ClassLibraryBuilder classLibraryBuilder, Reader... reader) {

        JavaSource source = null;
        ModifyableJavaClass targetClass = null;
        for (Reader r : reader) {
            source = classLibraryBuilder.addSource(r);
            if (targetClass == null) {
                targetClass = (ModifyableJavaClass) source.getClasses().get(0);
            }
        }
        return targetClass;
    }

    /**
     * Converts the String representation of a canonical type into a String which
     * represents the simple type. E.g.:
     * <ul>
     * <li><code>java.lang.String</code> is converted into <code>String</code></li>
     * <li><code>java.util.List&lt;java.lang.String&gt;</code> is converted into
     * <code>List&lt;String&gt;</code></li>
     * </ul>
     *
     * @param canonicalType the String representation of the canonical type to be
     *                      resolved
     * @return the resolved simple type as String representation.
     */
    public static String resolveToSimpleType(String canonicalType) {

        String simpleType = new String(canonicalType).replaceAll("(([\\w]+\\.))", "");
        return simpleType;
    }

    /**
     * Tries to get the Java context by creating a new class loader of the input
     * project that is able to load the input
     * file. We need this in order to perform reflection on the templates.
     *
     * @param inputFile    input file the user wants to generate code from
     * @param inputProject input project where the input file is located. We need
     *                     this in order to build the classpath of
     *                     the input file
     * @return the Java context created from the input project
     */
    public static JavaContext getJavaContext(Path inputFile, Path inputProject) {

        String fqn = null;
        MavenUtil.resolveDependencies(inputProject);
        try {
            MavenDependencyCollector dependencyCollector = new MavenDependencyCollector(
                    new MavenBridgeImpl(MavenUtil.determineMavenRepositoryPath().toFile()), false, true, null);
            JavaContext context = JavaSourceProviderUsingMaven.createFromLocalMavenProject(inputProject.toFile(),
                    dependencyCollector);
            LOG.debug("Checking dependencies to exist.");

            if (dependencyCollector.asClassLoader() instanceof URLClassLoader) {
                for (URL url : dependencyCollector.asUrls()) {
                    try {
                        if (!Files.exists(Paths.get(url.toURI()))) {
                            LOG.info("Found at least one maven dependency not to exist ({}).", url);
                            MavenUtil.resolveDependencies(inputProject);

                            // rerun collection
                            context = JavaSourceProviderUsingMaven.createFromLocalMavenProject(inputProject.toFile(),
                                    true);
                            break;
                        }
                    } catch (URISyntaxException e) {
                        LOG.warn("Unable to check {} for existence", url, (LOG.isDebugEnabled() ? e : null));
                    }
                }
                LOG.info("All dependencies exist on file system.");
            } else {
                LOG.debug("m-m-m classloader is instance of {}. Unable to check dependencies",
                        dependencyCollector.asClassLoader().getClass());
            }
            fqn = getFQN(inputFile);
            context.getClassLoader().loadClass(fqn);
            return context;
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            throw new CobiGenRuntimeException("Compiled class " + fqn
                    + " has not been found. Most probably you need to build project " + inputProject.toString() + ".",
                    e);
        } catch (Exception e) {
            throw new CobiGenRuntimeException(
                    "Transitive dependencies have not been found on your m2 repository (Maven). Please run 'mvn package' "
                            + "in your input project in order to download all the needed dependencies.",
                    e);
        }
    }

    /**
     * This method is traversing parent folders until it reaches java folder in
     * order to get the FQN
     *
     * @param inputFile Java input file to retrieve FQN (Full Qualified Name)
     * @return qualified name with full package
     */
    private static String getFQN(Path inputFile) {

        String simpleName = inputFile.getFileName().toString().replaceAll("\\.(?i)java", "");
        String packageName = getPackageName(inputFile.getParent(), "");

        return packageName + "." + simpleName;
    }

    /**
     * This method traverse the folder in reverse order from child to parent
     *
     * @param folder      parent input file
     * @param packageName the package name
     * @return package name
     */
    private static String getPackageName(Path folder, String packageName) {

        if (folder == null) {
            return null;
        }

        if (folder.getFileName().toString().toLowerCase().equals("java")) {
            String[] pkgs = packageName.split("\\.");

            packageName = pkgs[pkgs.length - 1];
            // Reverse order as we have traversed folders from child to parent
            for (int i = pkgs.length - 2; i > 0; i--) {
                packageName = packageName + "." + pkgs[i];
            }
            return packageName;
        }
        return getPackageName(folder.getParent(), packageName + "." + folder.getFileName().toString());
    }
}
