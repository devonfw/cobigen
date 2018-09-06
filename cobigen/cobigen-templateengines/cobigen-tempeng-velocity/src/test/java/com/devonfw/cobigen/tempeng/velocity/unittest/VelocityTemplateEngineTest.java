package com.devonfw.cobigen.tempeng.velocity.unittest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.tempeng.velocity.VelocityTemplateEngine;

/**
 *
 */
public class VelocityTemplateEngineTest {

    /**
     * @throws java.lang.Exception
     *             if something unexpected happens
     */
    @Before
    public void setUpBefore() throws Exception {
        engine = new VelocityTemplateEngine();
        engine.setTemplateFolder(Paths.get("src/test/resources"));
    }

    /**
     * Test subject
     */
    private VelocityTemplateEngine engine;

    /**
     * Tests a basic velocity generation. Test design used from freemarker plugin
     */
    @Test
    public void testProcess() {
        // arrange
        final File templateFolder = new File("src/test/resources/unit/").getAbsoluteFile();
        TextTemplate template = new TextTemplate() {
            @Override
            public String getRelativeTemplatePath() {
                return "temp1.vm";
            }

            @Override
            public Path getAbsoluteTemplatePath() {
                return templateFolder.toPath().resolve("temp1.vm");
            }
        };
        HashMap<String, Object> model = new HashMap<>();
        List<Object> fields = new ArrayList<>();
        HashMap<Object, Object> fieldAttr = new HashMap<>();
        fieldAttr.put("type", "A");
        fields.add(fieldAttr);
        fieldAttr = new HashMap<>();
        fieldAttr.put("type", "B");
        fields.add(fieldAttr);
        fieldAttr = new HashMap<>();
        fieldAttr.put("type", "C");
        fields.add(fieldAttr);
        HashMap<String, Object> fieldsAccessor = new HashMap<>();
        fieldsAccessor.put("fields", fields);
        model.put("pojo", fieldsAccessor);

        // act
        StringWriter out = new StringWriter();
        engine.setTemplateFolder(templateFolder.toPath());
        engine.process(template, model, out, "UTF-8");

        // assert
        assertThat(out).hasToString("A,B,C,");
    }

}
