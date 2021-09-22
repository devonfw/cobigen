package com.devonfw.cobigen.templates.devon4j.test.utils.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.utils.documentation.DocumentationUtil;

/**
 *
 */
public class DocumentationUtilTest {

  @Test
  public void testGettingColourGetsCorrectColour() {

    assertThat(new DocumentationUtil().getTypeWithAsciidocColour("GET")).isEqualTo("[aqua]#GET#");
    assertThat(new DocumentationUtil().getTypeWithAsciidocColour("POST")).isEqualTo("[lime]#POST#");
    assertThat(new DocumentationUtil().getTypeWithAsciidocColour("PUT")).isEqualTo("[yellow]#PUT#");
    assertThat(new DocumentationUtil().getTypeWithAsciidocColour("DELETE")).isEqualTo("[red]#DELETE#");
    assertThat(new DocumentationUtil().getTypeWithAsciidocColour("PATCH")).isEqualTo("[fuchsia]#PATCH#");
    assertThat(new DocumentationUtil().getTypeWithAsciidocColour("nothing")).isEqualTo("");
  }

}
