package com.capgemini.cobigen.openapiplugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

import com.capgemini.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.capgemini.cobigen.openapiplugin.inputreader.to.OpenAPIFile;

import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;

public class App {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String content = new Scanner(new File("C:\\Users\\rudiazma\\Desktop\\devonfw.yaml")).useDelimiter("\\Z").next();
        Swagger swagger = new Swagger20Parser().parse(content);

        ModelImpl mod = (ModelImpl) swagger.getDefinitions().get("SampleData");
        OpenAPIInputReader inp = new OpenAPIInputReader();
        File g = new File("D:\\Users\\rudiazma\\Desktop\\devonfw.yaml");
        OpenAPIFile file = new OpenAPIFile(g.toPath(), swagger);
        if (inp.isValidInput(file)) {
            List<Object> inputs = inp.getInputObjects(swagger, Charset.forName("UTF-8"));
            for (Object input : inputs) {
                // System.out.println(inp.createModel(input));
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
