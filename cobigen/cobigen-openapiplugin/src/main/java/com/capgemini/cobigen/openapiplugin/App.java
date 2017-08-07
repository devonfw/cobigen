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
import com.reprezen.kaizen.oasparser.model3.OpenApi3;

public class App {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String content = new Scanner(
            new File("C:\\Users\\rudiazma\\Documents\\Devon-dist_2.2.0\\workspaces_vs\\default\\devonfw30.yaml"))
                .useDelimiter("\\Z").next();
        // Swagger swagger = new Swagger20Parser().parse(content);

        OpenAPIInputReader inp = new OpenAPIInputReader();
        File g = new File("C:\\Users\\rudiazma\\Documents\\Devon-dist_2.2.0\\workspaces_vs\\default\\devonfw30.yaml");
        OpenApi3 parser = (OpenApi3) new OpenApi3Parser().parse(g.toURI());

        OpenAPIFile file = new OpenAPIFile(g.toPath(), parser);
        if (inp.isValidInput(file)) {
            List<Object> inputs = inp.getInputObjects(file, Charset.forName("UTF-8"));
            for (Object input : inputs) {
                // System.out.println(inp.createModel(input));
            }
        }
    }
}
