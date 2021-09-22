package com.devonfw.cobigen.javaplugin.inputreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.javaplugin.inputreader.to.PackageFolder;
import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.devonfw.cobigen.javaplugin.model.ModelConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

/**
 * Extension for the {@link InputReader} Interface of the CobiGen, to be able to read Java classes into FreeMarker
 * models
 */
public class JavaInputReader implements InputReader {

  /** Valid file extension for the reader */
  public static final String VALID_EXTENSION = "java";

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(JavaInputReader.class);

  @Override
  public boolean isValidInput(Object input) {

    if (input instanceof Class<?> || input instanceof JavaClass || input instanceof PackageFolder) {
      return true;
    } else if (input instanceof Object[]) {
      // check whether the same Java class has been provided as parser as well as reflection object
      Object[] inputArr = (Object[]) input;
      if (inputArr.length == 2) {
        if (inputArr[0] instanceof JavaClass && inputArr[1] instanceof Class<?>) {
          if (((JavaClass) inputArr[0]).getFullyQualifiedName().equals(((Class<?>) inputArr[1]).getCanonicalName())) {
            return true;
          } else {
            LOG.debug("Invalid array input, not reflecting the same java class: JavaClass[{}], Class<>[{}]",
                ((JavaClass) inputArr[0]).getFullyQualifiedName(), ((Class<?>) inputArr[1]).getCanonicalName());
          }
        } else if (inputArr[0] instanceof Class<?> && inputArr[1] instanceof JavaClass) {
          if (((Class<?>) inputArr[0]).getCanonicalName().equals(((JavaClass) inputArr[1]).getFullyQualifiedName())) {
            return true;
          } else {
            LOG.debug("Invalid array input, not reflecting the same java class: JavaClass[{}], Class<>[{}]",
                ((JavaClass) inputArr[1]).getFullyQualifiedName(), ((Class<?>) inputArr[0]).getCanonicalName());
          }
        } else {
          LOG.debug("No valid input. 0: {} / 1: {}", inputArr[0].getClass(), inputArr[1].getClass());
        }
      } else {
        LOG.debug("No valid input. Unknown array length {}", inputArr.length);
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> createModel(Object o) {

    if (o instanceof Class<?>) {
      LOG.debug("Creating model based on reflection.");
      return new ReflectedJavaModelBuilder().createModel((Class<?>) o);
    }
    if (o instanceof JavaClass) {
      LOG.debug("Creating model based on parsed content.");
      return new ParsedJavaModelBuilder().createModel((JavaClass) o);
    }
    if (o instanceof Object[] && isValidInput(o)) {
      Object[] inputArr = (Object[]) o;
      Object parsedModel;
      Object reflectionModel;
      if (inputArr[0] instanceof JavaClass) {
        parsedModel = new ParsedJavaModelBuilder().createModel((JavaClass) inputArr[0]);
        reflectionModel = new ReflectedJavaModelBuilder().createModel((Class<?>) inputArr[1]);
      } else {
        parsedModel = new ParsedJavaModelBuilder().createModel((JavaClass) inputArr[1]);
        reflectionModel = new ReflectedJavaModelBuilder().createModel((Class<?>) inputArr[0]);
      }
      LOG.debug("Provided both (reflection + parsed) models - merging both information");
      return (Map<String, Object>) mergeModelsRecursively(parsedModel, reflectionModel);
    }
    return null;
  }

  @Override
  public List<Object> getInputObjects(Object input, Charset inputCharset) {

    return getInputObjects(input, inputCharset, false);
  }

  @Override
  public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {

    return getInputObjects(input, inputCharset, true);
  }

  /**
   * Returns all input objects for the given container input.
   *
   * @param input container input (only {@link PackageFolder} instances will be supported)
   * @param inputCharset {@link Charset} to be used to read the children
   * @param recursively states, whether the children should be retrieved recursively
   * @return the list of children. In this case {@link File} objects
   */
  public List<Object> getInputObjects(Object input, Charset inputCharset, boolean recursively) {

    LOG.debug("Retrieve input object for input {} {}", input, recursively ? "recursively" : "");
    List<Object> javaClasses = new LinkedList<>();
    if (input instanceof PackageFolder) {
      File packageFolder = new File(((PackageFolder) input).getLocation());
      List<File> files = retrieveAllJavaSourceFiles(packageFolder, recursively);
      for (File f : files) {

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        ClassLoader containerClassloader = ((PackageFolder) input).getClassLoader();
        if (containerClassloader != null) {
          classLibraryBuilder.appendClassLoader(containerClassloader);
        }
        try (FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, inputCharset)) {
          classLibraryBuilder.addSource(isr);
          JavaSource source = null;
          for (JavaSource s : classLibraryBuilder.getClassLibrary().getJavaSources()) {
            source = s;
            // only consider one class per file
            break;
          }
          if (source != null) {
            // save cast as given by the customized builder
            if (source.getClasses().size() > 0) {
              JavaClass javaClass = source.getClasses().get(0);

              // try loading class
              if (containerClassloader != null) {
                try {
                  Class<?> loadedClass = containerClassloader.loadClass(javaClass.getCanonicalName());
                  javaClasses.add(new Object[] { javaClass, loadedClass });
                } catch (ClassNotFoundException e) {
                  LOG.info("Could not load Java type '{}' with the containers class loader. "
                      + "Just returning the parsed Java model.", javaClass.getCanonicalName());
                  javaClasses.add(javaClass);
                }
              } else {
                javaClasses.add(javaClass);
              }
            }
          }
        } catch (IOException e) {
          LOG.error("The file {} could not be parsed as a java class", f.getAbsolutePath().toString(), e);
        }

      }
    }
    LOG.debug("{} java classes found!", javaClasses.size());
    return javaClasses;
  }

  /**
   * Retrieves all java source files (with ending *.java) under the package's folder non-recursively
   *
   * @param packageFolder the package's folder
   * @param recursively states whether the java source files should be retrieved recursively
   * @return the list of files contained in the package's folder
   * @author mbrunnli (03.06.2014)
   */
  private List<File> retrieveAllJavaSourceFiles(File packageFolder, boolean recursively) {

    List<File> files = new LinkedList<>();
    List<File> directories = new LinkedList<>();
    if (packageFolder.isDirectory()) {
      for (File f : packageFolder.listFiles()) {
        if (f.isFile() && f.getName().endsWith(".java")) {
          files.add(f);
          LOG.debug("Found java source {}", f.getAbsolutePath());
        } else if (f.isDirectory()) {
          directories.add(f);
        }
      }
      if (recursively) {
        for (File dir : directories) {
          files.addAll(retrieveAllJavaSourceFiles(dir, recursively));
        }
      }
    }
    return files;

  }

  /**
   * Merges two models recursively. The current implementation only merges Lists and Maps recursively. Structures will
   * be merged as follows:<br>
   * <table>
   * <tr>
   * <td>Maps:</td>
   * <td>equal map entries will be discovered and the values will be merged recursively</td>
   * </tr>
   * <tr>
   * <td>Lists:</td>
   * <td>Lists will only be handled if their elements are {@link Map Maps}. If so, the {@link Map Maps} will be compared
   * due to their {@link ModelConstant#NAME} value. If equal, the elements will be recursively merged.</td>
   * </tr>
   * </table>
   *
   * @param parsedModel model created by parsing to be merged and preferred in case of conflicts
   * @param reflectionModel model created by reflection to be merged
   * @return the merged model. Due to implementation restrictions a {@link Map} of {@link String} to {@link Object}
   */
  @SuppressWarnings("unchecked")
  private Object mergeModelsRecursively(Object parsedModel, Object reflectionModel) {

    if (parsedModel == null && reflectionModel == null) {
      return null;
    } else if (parsedModel == null) {
      return reflectionModel;
    } else if (reflectionModel == null) {
      return parsedModel;
    } else if (parsedModel.equals(reflectionModel)) {
      return parsedModel;
    }

    if (parsedModel.getClass().equals(reflectionModel.getClass())) {
      if (parsedModel instanceof Map && reflectionModel instanceof Map) {
        Map<String, Object> mergedModel = Maps.newHashMap();
        Map<String, Object> model1Map = (Map<String, Object>) parsedModel;
        Map<String, Object> model2Map = (Map<String, Object>) reflectionModel;

        Set<String> union = Sets.newHashSet(model1Map.keySet());
        union.addAll(model2Map.keySet());
        for (String unionKey : union) {
          if (model1Map.containsKey(unionKey) && model2Map.containsKey(unionKey)) {
            // Recursively merge equal keys
            mergedModel.put(unionKey, mergeModelsRecursively(model1Map.get(unionKey), model2Map.get(unionKey)));
          } else if (model1Map.containsKey(unionKey)) {
            mergedModel.put(unionKey, model1Map.get(unionKey));
          } else {
            mergedModel.put(unionKey, model2Map.get(unionKey));
          }
        }
        return mergedModel;
      }
      // Case: List<Map<String, Object>> available in fields and methods
      else if (parsedModel instanceof List && reflectionModel instanceof List) {
        if (!((List<?>) parsedModel).isEmpty() && ((List<?>) parsedModel).get(0) instanceof Map
            || !((List<?>) reflectionModel).isEmpty() && ((List<?>) reflectionModel).get(0) instanceof Map) {
          List<Map<String, Object>> model1List = Lists.newLinkedList((List<Map<String, Object>>) parsedModel);
          List<Map<String, Object>> model2List = Lists.newLinkedList((List<Map<String, Object>>) reflectionModel);
          List<Object> mergedModel = Lists.newLinkedList();

          // recursively merge list entries. Match them by name attribute. This is currently valid
          // and might be adapted if there are greater model changes in future
          Iterator<Map<String, Object>> model1ListIt = model1List.iterator();
          while (model1ListIt.hasNext()) {
            Map<String, Object> model1Entry = model1ListIt.next();
            Iterator<Map<String, Object>> model2ListIt = model2List.iterator();
            while (model2ListIt.hasNext()) {
              Map<String, Object> model2Entry = model2ListIt.next();
              // valid merging for fields and methods
              if (model1Entry.get(ModelConstant.NAME) != null) {
                if (model1Entry.get(ModelConstant.NAME).equals(model2Entry.get(ModelConstant.NAME))) {
                  mergedModel.add(mergeModelsRecursively(model1Entry, model2Entry));

                  // remove both entries as they have been matched and recursively merged
                  model1ListIt.remove();
                  model2ListIt.remove();
                  break;
                }
              } else
              // this is the case for merging recursive annotation arrays
              if (model1Entry.size() == 1 && model2Entry.size() == 1) {
                mergeModelsRecursively(model1Entry.get(model1Entry.keySet().iterator().next()),
                    model2Entry.get(model2Entry.keySet().iterator().next()));
              } else {
                throw new IllegalStateException(
                    "Anything unintended happened. Please state an issue at GitHub or mail one of the developers");
              }
            }
          }

          // append not matched entries from list1 and list2
          mergedModel.addAll(model1List);
          mergedModel.addAll(model2List);
          return mergedModel;
        }
        // we will prefer parsed model if the values of the parsed result list are of type String.
        // This is the case for annotation values. QDox will always return the expression,
        // which is a assigned to the annotation's value, as a string.
        else if (!((List<?>) parsedModel).isEmpty() && ((List<?>) parsedModel).get(0) instanceof String) {
          return parsedModel;
        } else {
          if (reflectionModel instanceof Object[]) {
            return Lists.newLinkedList(Arrays.asList(reflectionModel));
          } else {
            return reflectionModel;
          }
        }
      } else {
        // any other type might not be merged. As the values are not equal, this might be a conflict,
        // so take model as documented
        return parsedModel;
      }
    } else if (parsedModel instanceof String[]) {
      return Lists.newLinkedList(Arrays.asList(parsedModel));
    }
    // we will prefer parsed model if parsed value of type String. This is the case for annotation values.
    // QDox will always return the expression, which is a assigned to the annotation's value, as a string.
    else {
      return parsedModel;
    }
  }

  /**
   * Reads the data at the specified path.
   *
   * @param path the Path of the content to read
   * @param additionalArguments
   *        <ul>
   *        <li>In case of path pointing to a folder
   *        <ol>
   *        <li>packageName: String, required</li>
   *        <li>classLoader: ClassLoader, required</li>
   *        </ol>
   *        additional arguments are ignored</li>
   *        <li>In case of path pointing to a file
   *        <ol>
   *        </ol>
   *        </li>
   *        </ul>
   */
  @Override
  public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {

    if (LOG.isDebugEnabled()) {
      LOG.debug("Read input file {} by java plugin with charset {} and additional arguments {}...", path, inputCharset,
          Arrays.toString(additionalArguments));
    }
    ClassLoader classLoader = null;

    if (Files.isDirectory(path)) {
      LOG.debug("Path {} to be read is a directory", path);
      String packageName = null;
      for (Object addArg : additionalArguments) {
        if (packageName == null && addArg instanceof String) {
          packageName = (String) addArg;
        } else if (classLoader == null && addArg instanceof ClassLoader) {
          classLoader = new CompositeClassLoader(JavaInputReader.class.getClassLoader(), (ClassLoader) addArg);
        }
      }
      if (classLoader == null) {
        classLoader = createParsedClassLoader(path);
      }
      if (packageName == null || classLoader == null) {
        throw new IllegalArgumentException(
            "Expected packageName:String and classLoader:ClassLoader as additional arguments but was "
                + toString(additionalArguments));
      }
      PackageFolder packageFolder = new PackageFolder(path.toUri(), packageName, classLoader);
      LOG.debug("Read {}.", packageFolder);
      return packageFolder;
    } else {
      Class<?> clazz = null;
      for (Object addArg : additionalArguments) {
        if (clazz == null && addArg instanceof Class) {
          clazz = (Class<?>) addArg;
        } else if (classLoader == null && addArg instanceof ClassLoader) {
          classLoader = new CompositeClassLoader(JavaInputReader.class.getClassLoader(), (ClassLoader) addArg);
        }
      }
      if (classLoader == null) {
        classLoader = createParsedClassLoader(path);
      }
      try (BufferedReader pathReader = Files.newBufferedReader(path, inputCharset)) {
        // couldn't think of another way here... Java8 compliance would made this a lot easier due to
        // lambdas
        if (clazz == null) {
          if (classLoader == null) {
            JavaClass firstJavaClass = JavaParserUtil.getFirstJavaClass(pathReader);
            LOG.debug("Reading {} without classloader support.", firstJavaClass);
            return firstJavaClass;
          } else {
            JavaClass firstJavaClass = JavaParserUtil.getFirstJavaClass(classLoader, pathReader);
            try {
              clazz = classLoader.loadClass(firstJavaClass.getCanonicalName());
            } catch (ClassNotFoundException e) {
              // ignore
              LOG.warn("Class {} not found in classloader, ignoring as it might be neglectable.", firstJavaClass, e);
              return firstJavaClass;
            }
            Object[] result = new Object[] { firstJavaClass, clazz };
            if (LOG.isDebugEnabled()) {
              LOG.debug("Read {}.", Arrays.toString(result));
            }
            return result;
          }
        } else {
          Object[] result = new Object[] { null, clazz };
          if (classLoader == null) {
            result[0] = JavaParserUtil.getFirstJavaClass(pathReader);
          } else {
            result[0] = JavaParserUtil.getFirstJavaClass(classLoader, pathReader);
          }
          if (LOG.isDebugEnabled()) {
            LOG.debug("Read {}.", Arrays.toString(result));
          }
          return result;
        }
      } catch (IOException e) {
        throw new InputReaderException("Could not read file " + path.toString(), e);
      } catch (ParseException e) {
        throw new InputReaderException("Failed to parse java sources in " + path.toString() + ".", e);
      }
    }
  }

  /**
   * Creates a classloader instance on the basis of parsed source code and maven configuration
   *
   * @param path the path of the input
   * @return a classloader created on the parsed source code
   */
  private ClassLoader createParsedClassLoader(Path path) {

    ClassLoader classLoader = null;
    LOG.debug("No classloader given, trying to create own...");
    Path inputProject = MavenUtil.getProjectRoot(path, false);
    if (inputProject != null) {
      classLoader = JavaParserUtil.getJavaContext(path, inputProject).getClassLoader();
    } else {
      LOG.debug("No maven project detected defining the input path {}, executing without classloader support", path);
    }
    return classLoader;
  }

  @Override
  public boolean isMostLikelyReadable(Path path) {

    String fileExtension = FilenameUtils.getExtension(path.toString()).toLowerCase();
    return VALID_EXTENSION.equals(fileExtension) || Files.isDirectory(path);
  }

  /**
   * Pretty Prints Objects for Logging
   *
   * @param any object.
   * @return String representation.
   */
  private String toString(Object any) {

    if (any == null) {
      return "null";
    }
    if (any instanceof Object[]) {
      String result = "[";
      for (Object o : (Object[]) any) {
        if ("[".equals(result)) {
          result = toString(o);
        } else {
          result = result + ", " + toString(o);
        }
      }
      return result + "]";
    } else if (Object.class.equals(any.getClass())) {
      return any.toString();
    } else {
      return any.getClass().getName() + "@" + any.toString();
    }
  }
}
