package com.devonfw.cobigen.templates.oasp4js.utils.test.java;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.mmm.util.reflect.base.GenericTypeBuilder;

import org.junit.Test;

import com.devonfw.cobigen.templates.oasp4js.utils.java.CobiGenMacroJavaHelper;
import com.devonfw.cobigen.templates.oasp4js.utils.java.JavaBean;

import io.oasp.module.basic.common.api.to.AbstractEto;
import io.oasp.module.test.common.base.ModuleTest;

/**
 * Test of {@link CobiGenMacroJavaHelper}.
 */
public class CobiGenMacroJavaHelperTest extends ModuleTest {

  /** Test of {@link CobiGenMacroJavaHelper#getImportStatements(java.util.Collection)} using standard types. */
  @Test
  public void testGetImportStatements4StandardTypes() {

    // given
    Collection<Type> types = new HashSet<>();
    types.add(String.class);
    types.add(Boolean.class);
    types.add(int.class);
    types.add(Object[].class);
    types.add(Double.class);
    types.add(Appendable.class);
    types.add(Throwable.class);
    // when
    List<String> importStatements = CobiGenMacroJavaHelper.getImportStatements(types);
    // then
    assertThat(importStatements).isEmpty();
  }

  /** Test of {@link CobiGenMacroJavaHelper#getImportStatements(Collection)} using custom types. */
  @Test
  public void testGetImportStatements4CustomTypes() {

    // given
    Collection<Type> types = new HashSet<>();
    types.add(BigDecimal.class);
    types.add(new GenericTypeBuilder<List<Date>>() {
    }.build());
    // when
    List<String> importStatements = CobiGenMacroJavaHelper.getImportStatements(types);
    // then
    assertThat(importStatements).containsExactlyInAnyOrder("import java.math.BigDecimal;", "import java.util.Date;",
        "import java.util.List;");
  }

  /** Test of {@link CobiGenMacroJavaHelper#createBean(Class)}. */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void testCreateBean() {

    JavaBean bean = CobiGenMacroJavaHelper.createBean(BarEto.class);
    assertThat(bean.getBeanClass()).isSameAs(BarEto.class);
    assertThat(bean.getAllProperties().stream().map(x -> x.getName()).collect(Collectors.toList()))
        .containsExactlyInAnyOrder("class", "id", "modificationCounter", "revision", "name", "creation");
    assertThat((Set) bean.getAllPropertyTypes().stream().map(x -> x.getRetrievalClass()).collect(Collectors.toSet()))
        .containsExactlyInAnyOrder(Class.class, Long.class, int.class, Number.class, String.class, Date.class);
  }

  @SuppressWarnings({ "serial", "javadoc" })
  public static class FooEto extends AbstractEto {

    private String name;

    public String getName() {

      return this.name;
    }

    public void setName(String name) {

      this.name = name;
    }
  }

  @SuppressWarnings({ "serial", "javadoc" })
  public static class BarEto extends FooEto {

    private Date creation;

    public Date getCreation() {

      return this.creation;
    }

    public void setCreation(Date creation) {

      this.creation = creation;
    }
  }

}
