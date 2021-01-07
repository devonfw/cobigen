package com.devonfw.cobigen.unittest.config.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.impl.config.entity.TemplateFile;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.TemplatePath;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test of {@link TemplatePath} and its sub-classes.
 */
public class TemplatePathTest extends AbstractUnitTest {

    /** Root path of the test resources */
    private static final String TEST_FILES_ROOT_PATH =
        "src/test/resources/testdata/unittest/config/entity/TemplatePathTest/";

    /**
     * Test {@link TemplateFolder#create(Path)} for non existent folder.
     */
    @Test(expected = CobiGenRuntimeException.class)
    public void testNonExistentFolder() {

        String filename = "non-existent-folder";
        Path rootPath = Paths.get(TEST_FILES_ROOT_PATH + filename);
        TemplateFolder.create(rootPath);
    }

    /**
     * Test {@link TemplateFolder#create(Path)} for empty folder.
     * @throws IOException
     *             test fails
     */
    @Test
    public void testEmptyFolder() throws IOException {

        String filename = "empty";
        Path path = Paths.get(TEST_FILES_ROOT_PATH + filename);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        TemplateFolder folder = TemplateFolder.create(path);
        verifyRootFolder(folder, path);
        assertThat(folder.getVariables().asMap()).isEmpty();
        verifyEmptyFolder(folder);
    }

    /**
     * Test {@link TemplateFolder#create(Path)} for folder tree with property overloading.
     */
    @Test
    public void testFolderTree() {

        String filename = "tree";
        String emptyFile = "empty-file";
        Path path = Paths.get(TEST_FILES_ROOT_PATH + filename);
        TemplateFolder folder = TemplateFolder.create(path);
        verifyRootFolder(folder, path);
        assertThat(folder).isNotNull();
        assertThat(folder.getVariables().asMap()).isEmpty();

        TemplateFolder having = verifyChildFolder(folder, path, "having");
        TemplateFolder or = verifyChildFolder(folder, path, "or");
        verifyChildren(folder, having, or);

        assertThat(having.getVariables().asMap()).isEmpty();

        TemplateFolder property = verifyChildFolder(having, having.getPath(), "property");
        verifyChildren(having, property);
        assertThat(property.getChild("cobigen.properties")).isNull();
        assertThat(property.getChild("COBIGEN.PROPERTIES")).isNull();
        assertThat(property.getVariables().asMap()).hasSize(2).containsEntry("foo", "foo").containsEntry("bar", "bar");

        TemplateFolder override = verifyChildFolder(property, property.getPath(), "override");
        verifyChildren(property, override);
        assertThat(override.getVariables().asMap()).hasSize(2).containsEntry("foo", "bar").containsEntry("bar", "bar");
        TemplateFile emptyFileInOverride = verifyChildFile(override, override.getPath(), emptyFile);
        verifyChildren(override, emptyFileInOverride);

        TemplateFolder not = verifyChildFolder(or, or.getPath(), "not");
        assertThat(not.getVariables().asMap()).isEmpty();
        TemplateFile emptyFileInNot = verifyChildFile(not, not.getPath(), emptyFile);
        verifyChildren(not, emptyFileInNot);
    }

    private static <T extends TemplatePath> T verifyPath(TemplatePath templatePath, Path path, Class<T> type) {

        assertThat(templatePath).isNotNull().isInstanceOf(type);
        assertThat(templatePath.getPath()).isEqualTo(path);
        assertThat(templatePath.getFileName()).isEqualTo(path.getFileName().toString());
        if (templatePath instanceof TemplateFolder) {
            assertThat(templatePath.isFolder()).isTrue();
            assertThat(templatePath.isFile()).isFalse();
        } else {
            assertThat(templatePath.isFolder()).isFalse();
            assertThat(templatePath.isFile()).isTrue();
        }
        return type.cast(templatePath);
    }

    private static <T extends TemplatePath> T verifyChildPath(TemplateFolder folder, Path path, String childName,
        Class<T> type) {

        TemplatePath child = folder.getChild(childName);
        T childTyped = verifyPath(child, path.resolve(childName), type);
        assertThat(child.getParent()).isSameAs(folder);
        assertThat(child.getRoot()).isSameAs(folder.getRoot()).isNotNull();
        String parentPath;
        if (folder.getParent() == null) {
            parentPath = "";
        } else {
            parentPath = folder.toString() + "/";
        }
        assertThat(child.toString()).isEqualTo(parentPath + childName);
        return childTyped;
    }

    private static void verifyFolder(TemplateFolder folder, Path path) {

        verifyPath(folder, path, TemplateFolder.class);
    }

    private static void verifyRootFolder(TemplateFolder rootFolder, Path path) {

        verifyFolder(rootFolder, path);
        assertThat(rootFolder.getParent()).isNull();
        assertThat(rootFolder.getRoot()).isSameAs(rootFolder);
        assertThat(rootFolder.toString()).isEmpty();
    }

    private static void verifyEmptyFolder(TemplateFolder folder) {

        assertThat(folder.getChildren()).isEmpty();
        assertThat(folder.getChildFiles()).isEmpty();
        assertThat(folder.getChildFolders()).isEmpty();
        assertThat(folder.getChild("filename")).isNull();
    }

    private static TemplateFolder verifyChildFolder(TemplateFolder folder, Path path, String childName) {

        TemplateFolder child = verifyChildPath(folder, path, childName, TemplateFolder.class);
        return child;
    }

    private static TemplateFile verifyChildFile(TemplateFolder folder, Path path, String childName) {

        TemplateFile child = verifyChildPath(folder, path, childName, TemplateFile.class);
        return child;
    }

    private static void verifyChildren(TemplateFolder folder, TemplatePath... children) {

        assertThat(folder.getChildren()).containsOnly(children);
        if (children.length == 0) {
            return;
        }
        List<TemplateFolder> childFolders = new ArrayList<>();
        List<TemplateFile> childFiles = new ArrayList<>();
        for (TemplatePath child : children) {
            if (child.isFolder()) {
                assertThat(child.isFile()).isFalse();
                childFolders.add((TemplateFolder) child);
            } else {
                assertThat(child.isFile()).isTrue();
                childFiles.add((TemplateFile) child);
            }
        }
    }

}
