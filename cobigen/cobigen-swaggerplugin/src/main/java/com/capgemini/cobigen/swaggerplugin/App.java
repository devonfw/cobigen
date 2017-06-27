package com.capgemini.cobigen.swaggerplugin;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Swagger swagger = new SwaggerParser().read(
            "D:\\Users\\rudiazma\\Documents\\bitBuckectRepos\\swagger4OASP4J\\devonfw\\core\\src\\main\\resources\\devonfw.yaml");
        System.out.println(swagger.getDefinitions());
    }
}
