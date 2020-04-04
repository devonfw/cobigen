package com.devonfw.cobigen.javaplugin.unittest.merger;

import static com.devonfw.cobigen.javaplugin.inputreader.JavaParserUtil.getFirstJavaClass;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.javaplugin.merger.JavaMerger;
import com.devonfw.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.google.common.io.Files;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

/**
 * TestCase testing {@link JavaMerger}
 *
 * @author mbrunnli (04.04.2013)
 */
public class JavaMergerTest {

    /**
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

    /**
     * Test of {@link JavaMerger} merging imports
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeImport_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_import.java");
        File patchFile = new File(testFileRootPath + "PatchFile_import.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).hasSize(2);
        assertThat(mergedSource.getImports()).contains("com.devonfw.BaseClassImport");
        assertThat(mergedSource.getImports()).contains("com.devonfw.PatchClassImport");
    }

    /**
     * Test of {@link JavaMerger} merging fields
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeProperty_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_field.java");
        File patchFile = new File(testFileRootPath + "PatchFile_field.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).isEmpty();
        assertThat(mergedSource.getClasses()).hasSize(1);

        JavaClass clsFooBar = mergedSource.getClassByName("com.devonfw.FooBar");
        assertThat(clsFooBar).isNotNull();
        assertThat(clsFooBar.getFields()).hasSize(3);

        JavaField field = clsFooBar.getFieldByName("baseField");
        assertThat(field).isNotNull();
        assertThat(field.getInitializationExpression()).isEqualTo("0");
    }

    /**
     * Test of {@link JavaMerger} merging methods
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeMethod_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_method.java");
        File patchFile = new File(testFileRootPath + "PatchFile_method.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).isEmpty();
        assertThat(mergedSource.getClasses()).hasSize(1);

        JavaClass clsFooBar = mergedSource.getClassByName("com.devonfw.FooBar");
        assertThat(clsFooBar).isNotNull();
        assertThat(clsFooBar.getConstructors()).hasSize(2);
        assertThat(clsFooBar.getMethods()).hasSize(2);

        JavaConstructor emptyConstructor = clsFooBar.getConstructor(new LinkedList<JavaType>());
        assertThat(emptyConstructor).isNotNull();
        assertThat(emptyConstructor.getSourceCode().trim()).isEqualTo("");

        JavaMethod baseMethod = clsFooBar.getMethodBySignature("baseMethod", new LinkedList<JavaType>());
        assertThat(baseMethod).isNotNull();
        assertThat(baseMethod.getReturnType(true).getCanonicalName()).isEqualTo(void.class.getCanonicalName());
    }

    /**
     * Test of {@link JavaMerger} merging classes recursively
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeInnerClasses_defaultNonOverride() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_innerClass.java");
        File patchFile = new File(testFileRootPath + "PatchFile_innerClass.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, false);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).isEmpty();
        assertThat(mergedSource.getClasses()).hasSize(1);

        JavaClass clsFooBar = mergedSource.getClassByName("com.devonfw.FooBar");
        assertThat(clsFooBar).isNotNull();
        assertThat(clsFooBar.getNestedClasses()).hasSize(3);

        JavaClass innerClass = clsFooBar.getNestedClassByName("InnerBaseClass");
        assertThat(innerClass).isNotNull();
        assertThat(innerClass.getMethods()).hasSize(2);
        assertThat(innerClass.getFields()).hasSize(2);
        assertThat(innerClass.getNestedClasses()).hasSize(1);

        JavaField innerField = innerClass.getFieldByName("innerBaseField");
        assertThat(innerField).isNotNull();
        assertThat(innerField.getInitializationExpression().equals("0")).isEqualTo(true);

        JavaMethod baseMethod = innerClass.getMethodBySignature("innerBaseMethod", new LinkedList<JavaType>());
        assertThat(baseMethod).isNotNull();
        assertThat(baseMethod.getReturnType(true).getCanonicalName()).isEqualTo(void.class.getCanonicalName());

        JavaClass mergedInnerEnum = innerClass.getNestedClassByName("InnerBaseEnum");
        assertThat(mergedInnerEnum).isNotNull();
        assertThat(mergedInnerEnum.getFields()).hasSize(2);

        JavaClass mergedEnum = clsFooBar.getNestedClassByName("BaseEnum");
        assertThat(mergedEnum).isNotNull();
        assertThat(mergedEnum.getFields()).hasSize(2);

    }

    /**
     * Test of {@link JavaMerger} merging imports
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeImport_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_import.java");
        File patchFile = new File(testFileRootPath + "PatchFile_import.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).hasSize(2);
        assertThat(mergedSource.getImports()).contains("com.devonfw.conflicting.BaseClassImport");
        assertThat(mergedSource.getImports()).contains("com.devonfw.PatchClassImport");
    }

    /**
     * Test of {@link JavaMerger} merging fields
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeProperty_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_field.java");
        File patchFile = new File(testFileRootPath + "PatchFile_field.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).isEmpty();
        assertThat(mergedSource.getClasses()).hasSize(1);

        JavaClass clsFooBar = mergedSource.getClassByName("com.devonfw.FooBar");
        assertThat(clsFooBar).isNotNull();
        assertThat(clsFooBar.getFields()).hasSize(3);

        JavaField field = clsFooBar.getFieldByName("baseField");
        assertThat(field).isNotNull();
        assertThat(field.getInitializationExpression()).isEqualTo("1");
    }

    /**
     * Test of {@link JavaMerger} merging methods
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeMethod_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_method.java");
        File patchFile = new File(testFileRootPath + "PatchFile_method.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).isEmpty();
        assertThat(mergedSource.getClasses()).hasSize(1);

        JavaClass clsFooBar = mergedSource.getClassByName("com.devonfw.FooBar");
        assertThat(clsFooBar).isNotNull();
        assertThat(clsFooBar.getConstructors()).hasSize(2);
        assertThat(clsFooBar.getMethods()).hasSize(2);

        JavaConstructor emptyConstructor = clsFooBar.getConstructor(new LinkedList<JavaType>());
        assertThat(emptyConstructor).isNotNull();
        assertThat(emptyConstructor.getSourceCode().trim()).isEqualTo("super();");

        JavaMethod baseMethod = clsFooBar.getMethodBySignature("baseMethod", new LinkedList<JavaType>());
        assertThat(baseMethod).isNotNull();
        assertThat(baseMethod.getReturnType(true).getCanonicalName()).isEqualTo(String.class.getCanonicalName());
    }

    /**
     * Test of {@link JavaMerger} merging classes recursively
     * @throws Exception
     *             test fails
     * @author mbrunnli (04.04.2013)
     */
    @Test
    public void testMergeInnerClasses_Override() throws Exception {
        File baseFile = new File(testFileRootPath + "BaseFile_innerClass.java");
        File patchFile = new File(testFileRootPath + "PatchFile_innerClass.java");
        JavaSource mergedSource = getMergedSource(baseFile, patchFile, true);

        assertThat(mergedSource.getPackageName()).isEqualTo("com.devonfw");
        assertThat(mergedSource.getImports()).isEmpty();
        assertThat(mergedSource.getClasses()).hasSize(1);

        JavaClass clsFooBar = mergedSource.getClassByName("com.devonfw.FooBar");
        assertThat(clsFooBar).isNotNull();
        assertThat(clsFooBar.getNestedClasses()).hasSize(3);

        JavaClass innerClass = clsFooBar.getNestedClassByName("InnerBaseClass");
        assertThat(innerClass).isNotNull();
        assertThat(innerClass.getMethods()).hasSize(2);
        assertThat(innerClass.getFields()).hasSize(2);
        assertThat(innerClass.getNestedClasses()).hasSize(1);

        JavaField innerField = innerClass.getFieldByName("innerBaseField");
        assertThat(innerField).isNotNull();
        assertThat(innerField.getInitializationExpression()).isEqualTo("1");

        JavaMethod baseMethod = innerClass.getMethodBySignature("innerBaseMethod", new LinkedList<JavaType>());
        assertThat(baseMethod).isNotNull();
        assertThat(baseMethod.getReturnType(true).getCanonicalName()).isEqualTo(String.class.getCanonicalName());

        JavaClass mergedInnerEnum = innerClass.getNestedClassByName("InnerBaseEnum");
        assertThat(mergedInnerEnum).isNotNull();
        assertThat(mergedInnerEnum.getFields()).hasSize(2);

        JavaClass mergedEnum = clsFooBar.getNestedClassByName("BaseEnum");
        assertThat(mergedEnum).isNotNull();
        assertThat(mergedEnum.getFields()).hasSize(2);

    }

    /**
     * Tests whether the contents will be rewritten after parsing and printing with QDox with the right
     * encoding
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     * @author mbrunnli (12.04.2013)
     */
    @Test
    public void testReadingEncoding() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_encoding_UTF-8.java");
        File patchFile = new File(testFileRootPath + "PatchFile_encoding.java");
        String mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");
        JavaSource mergedSource = getFirstJavaClass(new StringReader(mergedContents)).getSource();
        assertThat(mergedSource.toString().contains("enthält")).isTrue();

        baseFile = new File(testFileRootPath + "BaseFile_encoding_ISO-8859-1.java");
        mergedContents = new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "ISO-8859-1");
        mergedSource = getFirstJavaClass(new StringReader(mergedContents)).getSource();
        assertThat(mergedSource.toString()).contains("enthält");
    }

    /**
     * Tests whether the output file does not contain different line endings
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     * @author mbrunnli (04.06.2013)
     */
    @Test
    public void testConsistentLineEndings() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_innerClass.java");
        File patchFile = new File(testFileRootPath + "PatchFile_innerClass.java");
        String mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");

        boolean eol1 = mergedContents.contains("\r\n");
        mergedContents = mergedContents.replaceAll("\r\n", "");
        boolean eol2 = mergedContents.contains("\n");
        boolean eol3 = mergedContents.contains("\r");
        assertThat(eol1 ^ eol2 ^ eol3).isTrue();
    }

    /**
     * Tests whether all generics of the original file will be existent after merging
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     * @author mbrunnli (17.06.2013)
     */
    @Test
    public void testMergeWithGenerics() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_generics.java");
        File patchFile = new File(testFileRootPath + "PatchFile_generics.java");

        String mergedContents =
            new JavaMerger("", false).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");

        assertThat(mergedContents).contains("class Clazz<T extends Object>");
        assertThat(mergedContents).contains("Map<String,T>");
        assertThat(mergedContents).contains("private T t;");
        assertThat(mergedContents).contains("public T get()");
        assertThat(mergedContents).contains("public <U extends Number> void inspect(U u)");
    }

    /**
     * Tests merging java without adding new lines to method bodies (was a bug)
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     * @author mbrunnli (07.06.2014)
     */
    @Test
    public void testMergeMethodsWithoutExtendingMethodBodyWithWhitespaces() throws IOException, MergeException {
        File file = new File(testFileRootPath + "PatchFile_method.java");

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        JavaSource source = classLibraryBuilder.addSource(new FileInputStream(file));
        JavaClass origClazz = source.getClasses().get(0);

        String mergedContents =
            new JavaMerger("", true).merge(file, Files.toString(file, Charset.forName("UTF-8")), "UTF-8");

        classLibraryBuilder = new ModifyableClassLibraryBuilder();
        source = classLibraryBuilder.addSource(new StringReader(mergedContents));
        JavaClass resultClazz = source.getClasses().get(0);

        for (JavaMethod method : resultClazz.getMethods()) {
            JavaMethod origMethod = origClazz.getMethodBySignature(method.getName(), method.getParameterTypes());
            assertThat(method.getCodeBlock()).isEqualTo(origMethod.getCodeBlock());
        }
    }

    /**
     * Tests issue <a href=https://github.com/oasp/cobigen/issues/39>#39</a>: inheritance relation
     * should be merged also if the base class (natively) extends java.lang.Object
     * @throws IOException
     *             test fails
     * @throws MergeException
     *             test fails
     * @author mbrunnli (29.09.2014)
     */
    @Test
    public void testMergeInheritanceRelation() throws IOException, MergeException {
        File baseFile = new File(testFileRootPath + "BaseFile_inheritance.java");
        File patchFile = new File(testFileRootPath + "PatchFile_inheritance.java");

        JavaClass origClazz = getFirstJavaClass(new FileReader(baseFile));
        assertThat(origClazz.getSuperClass().getCanonicalName()).isEqualTo("java.lang.Object");

        String mergedContents =
            new JavaMerger("", false).merge(baseFile, Files.toString(patchFile, Charset.forName("UTF-8")), "UTF-8");

        JavaClass resultClazz = getFirstJavaClass(new StringReader(mergedContents));
        assertThat(resultClazz.getSuperClass().getCanonicalName())
            .as("The merged result does not contain the expected inheritance relation 'extends HashMap<String,Long>'")
            .isEqualTo("java.util.HashMap");
        assertThat(resultClazz.getSuperClass().getGenericValue())
            .as("The merged result does not contain the original inheritance declaration'extends HashMap<String,Long>'")
            .isEqualTo("HashMap<String,Long>");
    }

    /**
     * Tests the behavior if one file imports a type while the other uses an explicit type. Corresponds to <a
     * href=https://github.com/devonfw/cobigen/issues/108>#108</a>
     * @throws IOException
     *             shouldn't happen
     * @throws MergeException
     *             shoudln't happen either
     */
    @Test
    public void testMergeExpizitAndImplizitParameterTypes() throws IOException, MergeException {

        File base = new File(testFileRootPath + "BaseFile_QualType.java");
        File patch = new File(testFileRootPath + "PatchFile_QualType.java");

        JavaSource mergedSource = getMergedSource(base, patch, true);
        assertThat(mergedSource.getClasses().isEmpty()).isFalse();
        JavaClass mergedClass = mergedSource.getClasses().get(0);
        // System.out.print(mergedSource.toString());
        assertThat(mergedClass.getFields()).as("Too much fields").hasSize(1);
        assertThat(mergedClass.getMethods().size()).as("Too much methods:\n" + mergedClass.getMethods().toString())
            .isEqualTo(1);

    }

    /**
     * Tests whether static and object Initializers are getting merged
     * https://github.com/devonfw/cobigen/issues/791
     *
     * @throws IOException
     *             shouldn't happen
     * @throws MergeException
     *             shoudln't happen either
     */
    @Test
    public void testStaticIntializer() throws IOException, MergeException {

        File base = new File(testFileRootPath + "BaseFile_staticInitializer.java");
        File patch = new File(testFileRootPath + "PatchFile_staticInitializer.java");

        JavaSource mergedSource = getMergedSource(base, patch, true);
        JavaClass mergedClass = mergedSource.getClasses().get(0);
        assertThat(mergedClass.getInitializers().size()).isEqualTo(3);

    }

    /**
     * Calls the {@link JavaMerger} to merge the base and patch file wit the given overriding behavior
     * @param baseFile
     *            base file
     * @param patchFile
     *            patch file
     * @param override
     *            overriding behavior
     * @return the merged {@link JavaSource}
     * @throws IOException
     *             if one of the files could not be read
     * @throws MergeException
     *             test fails
     */
    private JavaSource getMergedSource(File baseFile, File patchFile, boolean override)
        throws IOException, MergeException {
        String mergedContents =
            new JavaMerger("", override).merge(baseFile, FileUtils.readFileToString(patchFile), "UTF-8");
        return getFirstJavaClass(new StringReader(mergedContents)).getSource();
    }

}
