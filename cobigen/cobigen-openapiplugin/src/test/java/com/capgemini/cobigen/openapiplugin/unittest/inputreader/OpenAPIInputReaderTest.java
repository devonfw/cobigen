package com.capgemini.cobigen.openapiplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.capgemini.cobigen.openapiplugin.model.ComponentDef;
import com.capgemini.cobigen.openapiplugin.model.EntityDef;
import com.capgemini.cobigen.openapiplugin.model.PathDef;
import com.capgemini.cobigen.openapiplugin.model.PropertyDef;
import com.capgemini.cobigen.openapiplugin.util.TestConstants;

/** Test suite for {@link OpenAPIInputReader}. */
public class OpenAPIInputReaderTest {

    /** Testdata root path */
    private static final String testdataRoot = "src/test/resources/testdata/unittest/OpenAPIInputReaderTest";

    /**
     * Test {@link InputReader#getInputObjects(Object, Charset)} extracting two components
     * @throws Exception
     *             test fails
     */
    @Test
    public void testRetrieveAllInputs() throws Exception {

        List<Object> inputObjects = getInputs();

        assertThat(inputObjects).hasSize(2);
        assertThat(inputObjects).extracting("name").containsExactly("Table", "Sale");
    }

    @Test
    public void testRetrieveAllComponentNames() throws Exception {

        List<Object> inputObjects = getInputs();

        assertThat(inputObjects).hasSize(2);
        assertThat(inputObjects).extracting("componentName").containsExactly("tablemanagement", "salemanagement");
    }

    @Test
    public void testRetrieveAllPropertiesOfEntity() throws Exception {

        List<Object> inputObjects = getInputs();
        List<PropertyDef> properties = new LinkedList<>();
        for (Object o : inputObjects) {
            properties.addAll(((EntityDef) o).getProperties());
        }
        assertThat(properties).hasSize(2);
        assertThat(properties).extracting("name").containsExactly("tableExample", "saleExample");
    }

    @Test
    public void testRetrieveTypesAndFormatsOfPropertiesOfEntity() throws Exception {

        List<Object> inputObjects = getInputs();
        List<PropertyDef> properties = new LinkedList<>();
        for (Object o : inputObjects) {
            properties.addAll(((EntityDef) o).getProperties());
        }
        List<String> types = new LinkedList<>();
        List<String> formats = new LinkedList<>();
        for (PropertyDef property : properties) {
            types.add(property.getType());
            formats.add(property.getFormat());
        }
        assertThat(types).hasSize(2);
        assertThat(formats).hasSize(2);
        assertThat(types).containsExactly("string", "number");
        assertThat(formats).containsExactly(null, "int64");
    }

    @Test
    public void testRetrieveConstraintsOfPropertiesOfEntity() throws Exception {

        List<Object> inputObjects = getInputs();
        List<PropertyDef> properties = new LinkedList<>();
        for (Object o : inputObjects) {
            properties.addAll(((EntityDef) o).getProperties());
        }
        List<Map<String, Object>> constraints = new LinkedList<>();
        for (PropertyDef property : properties) {
            constraints.add(property.getConstraints());
        }
        assertThat(constraints).hasSize(2);
        assertThat(constraints).extracting("maximum").containsExactly(null, 100);
        assertThat(constraints).extracting("minimum").containsExactly(null, 0);
        assertThat(constraints).extracting("maxLength").containsExactly(100, null);
        assertThat(constraints).extracting("minLength").containsExactly(5, null);
    }

    @Test
    public void testRetrievePathsOfComponent() throws Exception {

        List<Object> inputObjects = getInputs();
        List<ComponentDef> cmps = new LinkedList<>();
        for (Object o : inputObjects) {
            cmps.add(((EntityDef) o).getComponent());
        }
        assertThat(cmps).extracting("paths").hasSize(2);
        List<String> pathURIs = new LinkedList<>();
        for (ComponentDef cmp : cmps) {
            for (PathDef path : cmp.getPaths()) {
                pathURIs.add(path.getPathURI());
            }
        }
        assertThat(pathURIs).hasSize(4);
        assertThat(pathURIs).containsExactly("/table/{id}/", "/sale/{id}/", "/sale/", "/new/");
    }

    private List<Object> getInputs() throws Exception {

        OpenAPIInputReader inputReader = new OpenAPIInputReader();
        Object inputObject = inputReader.read(Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
        return inputReader.getInputObjects(inputObject, TestConstants.UTF_8);
    }
}
