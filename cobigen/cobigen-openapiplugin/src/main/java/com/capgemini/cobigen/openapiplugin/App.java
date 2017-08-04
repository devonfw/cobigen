package com.capgemini.cobigen.openapiplugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

import com.capgemini.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.capgemini.cobigen.openapiplugin.model.OpenAPIFile;
import com.reprezen.kaizen.oasparser.OpenApi3Parser;

import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;

public class App {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String content = new Scanner(new File("G:\\Devon-dist_2.2.0\\workspaces_vs\\default\\devonfw30.yaml"))
            .useDelimiter("\\Z").next();
        Swagger swagger = new Swagger20Parser().parse(content);

        OpenAPIInputReader inp = new OpenAPIInputReader();
        File g = new File("G:\\Devon-dist_2.2.0\\workspaces_vs\\default\\devonfw30.yaml");
        OpenApi3Parser parser = new OpenApi3Parser().parse(g);
        OpenAPIFile file = new OpenAPIFile(g.toPath(), swagger);
        if (inp.isValidInput(file)) {
            List<Object> inputs = inp.getInputObjects(file, Charset.forName("UTF-8"));
            for (Object input : inputs) {
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
