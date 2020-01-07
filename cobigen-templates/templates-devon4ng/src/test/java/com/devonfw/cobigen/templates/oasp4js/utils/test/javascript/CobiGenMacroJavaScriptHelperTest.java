package com.devonfw.cobigen.templates.oasp4js.utils.test.javascript;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.mmm.util.lang.api.Password;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.base.GenericTypeBuilder;
import net.sf.mmm.util.reflect.impl.SimpleGenericTypeImpl;

import org.junit.Test;

import com.devonfw.cobigen.templates.oasp4js.utils.java.JavaBean;
import com.devonfw.cobigen.templates.oasp4js.utils.javascript.CobiGenMacroJavaScriptHelper;
import com.devonfw.cobigen.templates.oasp4js.utils.javascript.JavaScriptArrayType;
import com.devonfw.cobigen.templates.oasp4js.utils.javascript.JavaScriptBasicType;
import com.devonfw.cobigen.templates.oasp4js.utils.javascript.JavaScriptComplexType;
import com.devonfw.cobigen.templates.oasp4js.utils.javascript.JavaScriptType;

import io.oasp.module.basic.common.api.to.AbstractCto;
import io.oasp.module.basic.common.api.to.AbstractEto;
import io.oasp.module.basic.common.api.to.AbstractTo;
import io.oasp.module.test.common.base.ModuleTest;

/**
 * {@link ModuleTest} of {@link CobiGenMacroJavaScriptHelper}.
 */
public class CobiGenMacroJavaScriptHelperTest extends ModuleTest {

  /** Test of {@link CobiGenMacroJavaScriptHelper#getImportStatements(Collection, Class)} using standard types. */
  @Test
  public void testGetImportStatements4StandardTypes() {

    // given
    Collection<Type> types = new HashSet<>();
    types.add(Date.class);
    types.add(LocalDateTime.class);
    types.add(new SimpleGenericTypeImpl<>(LocalDateTime.class));
    types.add(Password.class);
    // when
    List<String> importStatements = CobiGenMacroJavaScriptHelper.getImportStatements(types, null);
    // then
    assertThat(importStatements).isEmpty();
  }

  /** Test of {@link CobiGenMacroJavaScriptHelper#getImportStatements(Collection, Class)} using custom types. */
  @Test
  public void testGetImportStatements4CustomTypes() {

    // given
    Collection<Type> types = new HashSet<>();
    types.add(YearMonth.class);
    types.add(JavaBean.class);
    // when
    List<String> importStatements =
        CobiGenMacroJavaScriptHelper.getImportStatements(types, CobiGenMacroJavaScriptHelper.class);
    // then
    assertThat(importStatements).containsExactlyInAnyOrder("import {YearMonth} from 'oasp/common/datatype/YearMonth';",
        "import {JavaBean} from '../java/JavaBean';");
  }

  /** Test of {@link CobiGenMacroJavaScriptHelper#toJavaScriptType(Class, Class)} */
  @Test
  public void testToJavaScriptType() {

    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(int.class, null)).isEqualTo(JavaScriptBasicType.NUMBER);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(Integer.class, null))
        .isEqualTo(JavaScriptBasicType.NUMBER);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(boolean.class, null))
        .isEqualTo(JavaScriptBasicType.BOOLEAN);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(String.class, null)).isEqualTo(JavaScriptBasicType.STRING);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(Object.class, null)).isEqualTo(JavaScriptBasicType.OBJECT);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(Map.class, null)).isEqualTo(JavaScriptBasicType.OBJECT);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(AbstractTo.class, null))
        .isEqualTo(JavaScriptComplexType.ABSTRACT_TO);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(AbstractEto.class, null))
        .isEqualTo(JavaScriptComplexType.ABSTRACT_ETO);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(AbstractCto.class, null))
        .isEqualTo(JavaScriptComplexType.ABSTRACT_CTO);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(Password.class, null))
        .isEqualTo(JavaScriptBasicType.STRING); // bad use-case example - never send passwords via JSON !!!
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(Object[].class, null))
        .isEqualTo(JavaScriptBasicType.ARRAY);
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(List.class, null)).isEqualTo(JavaScriptBasicType.ARRAY);
    GenericType<List<String>> listOfStrings = new GenericTypeBuilder<List<String>>() {
    }.build();
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptType(listOfStrings, null))
        .isEqualTo(new JavaScriptArrayType(JavaScriptBasicType.STRING));
  }

  /** Test of {@link CobiGenMacroJavaScriptHelper#toJavaScriptTypeString(String, Class)} */
  @Test
  public void testToJavaScriptTypeString() {

    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptTypeString("java.lang.String", null))
        .isEqualTo(JavaScriptBasicType.STRING.getSimpleName());
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptTypeString("UndefinedClass", null)).isEqualTo("UndefinedClass");
    assertThat(CobiGenMacroJavaScriptHelper.toJavaScriptTypeString("some.UndefinedClass", null))
        .isEqualTo("UndefinedClass");
  }

  /** Test of {@link CobiGenMacroJavaScriptHelper#toJavaScriptType(Type, Class)} */
  @Test
  public void testToJavaScriptTypeWithGenrics() {

    JavaScriptType jsType = CobiGenMacroJavaScriptHelper.toJavaScriptType(DummyCto.class.getGenericSuperclass(), null);
    assertThat(jsType.getSimpleName()).isEqualTo(MasterCto.class.getSimpleName());
    assertThat(jsType.getGenericTypes())
        .containsExactlyInAnyOrder(CobiGenMacroJavaScriptHelper.toJavaScriptType(DummyEto.class, null));
  }

  @SuppressWarnings({ "javadoc", "serial" })
  public static class DummyEto extends AbstractEto {

    private String foo;

    public String getFoo() {

      return this.foo;
    }

    public void setFoo(String foo) {

      this.foo = foo;
    }
  }

  @SuppressWarnings({ "javadoc" })
  public abstract static class MasterCto<E extends AbstractEto> {

    private E master;

    public E getMaster() {

      return this.master;
    }

    public void setMaster(E master) {

      this.master = master;
    }

  }

  @SuppressWarnings({ "javadoc" })
  public static class DummyCto extends MasterCto<DummyEto> {

  }

}
