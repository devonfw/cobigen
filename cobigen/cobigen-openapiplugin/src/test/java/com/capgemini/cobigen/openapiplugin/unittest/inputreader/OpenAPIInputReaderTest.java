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
import com.capgemini.cobigen.openapiplugin.model.OperationDef;
import com.capgemini.cobigen.openapiplugin.model.ParameterDef;
import com.capgemini.cobigen.openapiplugin.model.PathDef;
import com.capgemini.cobigen.openapiplugin.model.PropertyDef;
import com.capgemini.cobigen.openapiplugin.model.RelationShip;
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
        assertThat(constraints).extracting("unique").containsExactly(true, false);
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
        assertThat(pathURIs).containsExactly("/table/{id}/", "/table/new/", "/sale/{id}/", "/sale/");
    }

    @Test
    public void testRetrieveOperationsOfPath() throws Exception {

        List<Object> inputObjects = getInputs();
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

        List<ParameterDef> parameters = getParametersOfOperations();
        assertThat(parameters).extracting("name").hasSize(4);
        assertThat(parameters).extracting("name").containsExactly("id", "table", "amount", "criteria");

    }

    @Test
    public void testRetrieveConstraintsOfParameter() throws Exception {

        List<ParameterDef> parameters = getParametersOfOperations();
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

    @Test
    public void testRetrieveRelationShips() throws Exception {
        List<Object> inputObjects = getInputs();
        List<RelationShip> relationships = new LinkedList<>();
        for (Object o : inputObjects) {
            relationships.addAll(((EntityDef) o).getRelationShips());
        }

        assertThat(relationships).hasSize(4);
        assertThat(relationships).extracting("type").containsExactly("manytomany", "onetoone", "onetoone", "onetomany");
        assertThat(relationships).extracting("entity").containsExactly("Table", "Sale", "Table", "Table");
        assertThat(relationships).extracting("unidirectional").containsExactly(false, false, false, true);
        assertThat(relationships).extracting("sameComponent").containsExactly(true, false, false, false);
    }

    private List<Object> getInputs() throws Exception {

        OpenAPIInputReader inputReader = new OpenAPIInputReader();
        Object inputObject = inputReader.read(Paths.get(testdataRoot, "two-components.yaml"), TestConstants.UTF_8);
        return inputReader.getInputObjects(inputObject, TestConstants.UTF_8);
    }

    private List<ParameterDef> getParametersOfOperations() throws Exception {
        List<Object> inputObjects = getInputs();
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
