package com.capgemini.cobigen.swaggerplugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.capgemini.cobigen.swaggerplugin.inputreader.SwaggerInputReader;
import com.capgemini.cobigen.swaggerplugin.inputreader.to.SwaggerFile;

import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;

public class App {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String content = new Scanner(new File("D:\\Users\\rudiazma\\Desktop\\devonfw.yaml")).useDelimiter("\\Z").next();
        Swagger swagger = new Swagger20Parser().parse(content);

        ModelImpl mod = (ModelImpl) swagger.getDefinitions().get("SampleData");
        SwaggerInputReader inp = new SwaggerInputReader();
        File g = new File("D:\\Users\\rudiazma\\Desktop\\devonfw.yaml");
        SwaggerFile file = new SwaggerFile(g.toURI(), "loqsea.yaml");
        file.setSwaggerFile(swagger);
        if (inp.isValidInput(file)) {
            for (Object input : inp.getInputObjects(swagger, Charset.forName("UTF-8"))) {
                System.out.println(inp.createModel(input));
            }
        }
        // System.out.println(inp.createModel(mod));

        //
        //
        // System.out.println(mod.getDescription());
        // int descriptionIndex = mod.getDescription().indexOf("-/-");
        // if (descriptionIndex > 0) {
        // System.out.println(mod.getDescription().subSequence(0, descriptionIndex));
        // }
        // System.out.println(mod.getProperties().get("name"));
        // System.out.println(swagger.getDefinitions().get(swagger.getDefinitions().keySet().toArray()[1]));
    }
}
