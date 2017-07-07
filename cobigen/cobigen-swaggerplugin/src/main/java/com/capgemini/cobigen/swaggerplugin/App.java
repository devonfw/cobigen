package com.capgemini.cobigen.swaggerplugin;

import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.parser.SwaggerParser;

public class App {
    public static void main(String[] args) {
        Swagger swagger = new SwaggerParser().read(
            "D:\\Users\\rudiazma\\Documents\\bitBuckectRepos\\swagger4OASP4J\\devonfw\\core\\src\\main\\resources\\devonfw.yaml");
        System.out.println(swagger.getDefinitions().keySet());
        System.out.println(swagger.getDefinitions().containsKey("SampleData"));
        ModelImpl mod = (ModelImpl) swagger.getDefinitions().get("SampleData");
        Property name = mod.getProperties().get("name");
        if (name instanceof StringProperty) {
            System.out.println(((StringProperty) name).getRequired());
        }

        System.out.println(mod.getDescription());
        int descriptionIndex = mod.getDescription().indexOf("-/-");
        if (descriptionIndex > 0) {
            System.out.println(mod.getDescription().subSequence(0, descriptionIndex));
        }
        System.out.println(mod.getProperties().get("name"));
        // System.out.println(swagger.getDefinitions().get(swagger.getDefinitions().keySet().toArray()[1]));
    }
}
