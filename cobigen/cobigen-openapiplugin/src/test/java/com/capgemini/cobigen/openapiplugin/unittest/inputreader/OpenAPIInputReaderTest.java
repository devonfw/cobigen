package com.capgemini.cobigen.openapiplugin.unittest.inputreader;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.capgemini.cobigen.openapiplugin.model.ComponentDef;
import com.capgemini.cobigen.openapiplugin.model.EntityDef;
import com.capgemini.cobigen.openapiplugin.model.OperationDef;
import com.capgemini.cobigen.openapiplugin.model.ParameterDef;
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

        List<Object> inputObjects = getInputs("two-components.yaml");

        assertThat(inputObjects).hasSize(2);
        assertThat(inputObjects).extracting("name").containsExactly("Table", "Sale");
    }

    /**
     * Tests the resolution of paths definitions
     * @throws Exception
     *             test fails
     */
    @Test
    public void testPathResolution() throws Exception {

        List<Object> inputObjects = getInputs("paths-resolution.yaml");

        assertThat(inputObjects).isNotNull();
        List<EntityDef> collect = inputObjects.stream().map(e -> (EntityDef) e).filter(e -> e.getName().equals("Table"))
            .collect(Collectors.toList());
        assertThat(collect).hasSize(1);
        assertThat(collect.get(0).getComponent().getPaths()).hasSize(2).flatExtracting(e -> e.getOperations())
            .extracting(e -> e.getOperationId()).containsExactlyInAnyOrder("findTable", null);
    }

    @Test
    public void testRetrieveAllComponentNames() throws Exception {

        List<Object> inputObjects = getInputs("two-components.yaml");

        assertThat(inputObjects).hasSize(2);
        assertThat(inputObjects).extracting("componentName").containsExactly("tablemanagement", "salemanagement");
    }

    @Test
    public void testRetrieveAllPropertiesOfEntity() throws Exception {

        List<Object> inputObjects = getInputs("two-components.yaml");
        List<PropertyDef> properties = new LinkedList<>();
        for (Object o : inputObjects) {
            properties.addAll(((EntityDef) o).getProperties());
        }
        assertThat(properties).hasSize(2);
        assertThat(properties).extracting("name").containsExactly("tableExample", "saleExample");
    }

    @Test
    public void testRetrieveTypesAndFormatsOfPropertiesOfEntity() throws Exception {

        List<Object> inputObjects = getInputs("two-components.yaml");
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

        List<Object> inputObjects = getInputs("two-components.yaml");
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
        assertThat(constraints).extracting("unique").containsExactly(true, false);
    }

    @Test
    public void testRetrievePathsOfComponent() throws Exception {

        List<Object> inputObjects = getInputs("two-components.yaml");
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
        assertThat(pathURIs).containsExactly("/table/{id}/", "/table/new/", "/sale/{id}/", "/sale/");
    }

    @Test
    public void testRetrieveOperationsOfPath() throws Exception {

        List<Object> inputObjects = getInputs("two-components.yaml");
        List<ComponentDef> cmps = new LinkedList<>();
        for (Object o : inputObjects) {
            cmps.add(((EntityDef) o).getComponent());
        }

        List<OperationDef> operations = new LinkedList<>();
        for (ComponentDef cmp : cmps) {
            for (PathDef path : cmp.getPaths()) {
                for (OperationDef op : path.getOperations()) {
                    operations.add(op);
                }
            }
        }
        assertThat(operations).extracting("type").hasSize(4);
        assertThat(operations).extracting("type").containsExactly("get", "post", "get", "post");
    }

    @Test
    public void testRetrieveParametersOfOperation() throws Exception {

        List<ParameterDef> parameters = getParametersOfOperations("two-components.yaml");
        assertThat(parameters).extracting("name").hasSize(4);
        assertThat(parameters).extracting("name").containsExactly("id", "table", "amount", "criteria");

    }

    @Test
    public void testRetrieveConstraintsOfParameter() throws Exception {

        List<ParameterDef> parameters = getParametersOfOperations("two-components.yaml");
        List<Map<String, Object>> constraints = new LinkedList<>();
        for (ParameterDef param : parameters) {
            constraints.add(param.getConstraints());
        }
        assertThat(constraints).extracting("minimum").hasSize(4);
        assertThat(constraints).extracting("maximum").hasSize(4);
        assertThat(constraints).extracting("notNull").hasSize(4);
        assertThat(constraints).extracting("minimum").contains(0, 10);
        assertThat(constraints).extracting("maximum").contains(50, 200);
        assertThat(constraints).extracting("notNull").containsExactly(true, true, false, true);

    }

    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidPath() throws Exception {
        List<Object> inputObjects = getInputs("invalidPath.yaml");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testInvalidXComponent() throws Exception {
        List<Object> inputObjects = getInputs("invalidXComponent.yaml");
    }

    @Test
    public void testPropertyRefOneToOne() throws Exception {
        List<Object> inputObjects = getInputs("property-ref-one-to-one.yaml");
        boolean found = false;
        for (Object o : inputObjects) {
            EntityDef entityDef = (EntityDef) o;
            if (entityDef.getName().equals("SampleData")) {
                assertThat(entityDef.getProperties()).hasSize(1);
                PropertyDef prop = entityDef.getProperties().get(0);
                assertThat(prop.getType()).isEqualTo("MoreData");
                assertThat(prop.getName()).isEqualTo("mainData");
                assertThat(prop.getSameComponent()).isTrue();
                // The description of the property will be ignored in compliance with the JSON specification:
                // https://github.com/RepreZen/KaiZen-OpenApi-Parser/issues/148
                assertThat(prop.getDescription()).isEqualTo("MoreData Desc");
                found = true;
            }
        }
        assertThat(found).as("SampleData component schema not found!").isTrue();
    }

    @Test
    public void testPropertyRefManyToOne() throws Exception {
        List<Object> inputObjects = getInputs("property-ref-many-to-one.yaml");
        boolean found = false;
        for (Object o : inputObjects) {
            EntityDef entityDef = (EntityDef) o;
            if (entityDef.getName().equals("SampleData")) {
                assertThat(entityDef.getProperties()).hasSize(1);
                PropertyDef prop = entityDef.getProperties().get(0);
                assertThat(prop.getType()).isEqualTo("MoreData");
                assertThat(prop.getName()).isEqualTo("mainData");
                assertThat(prop.getSameComponent()).isTrue();
                assertThat(prop.getDescription()).isEqualTo("a single ref to MoreData (many-to-one)");
                found = true;
            }
        }
        assertThat(found).as("SampleData component schema not found!").isTrue();
    }

    private List<Object> getInputs(String testInputFilename) throws Exception {
        OpenAPIInputReader inputReader = new OpenAPIInputReader();
        Object inputObject = inputReader.read(Paths.get(testdataRoot, testInputFilename), TestConstants.UTF_8);
        return inputReader.getInputObjects(inputObject, TestConstants.UTF_8);
    }

    private List<ParameterDef> getParametersOfOperations(String testInputFilename) throws Exception {
        List<Object> inputObjects = getInputs(testInputFilename);
        List<ComponentDef> cmps = new LinkedList<>();
        for (Object o : inputObjects) {
            cmps.add(((EntityDef) o).getComponent());
        }

        List<ParameterDef> parameters = new LinkedList<>();
        for (ComponentDef cmp : cmps) {
            for (PathDef path : cmp.getPaths()) {
                for (OperationDef op : path.getOperations()) {
                    for (ParameterDef param : op.getParameters()) {
                        parameters.add(param);

                    }
                }
            }
        }
        return parameters;

    }

}
