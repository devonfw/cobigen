package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

/**
 * Support for Java8 specific code. Extracted here as it can fail if JVM version is less than 8.
 */
class Java8Support {

  static {
    CobiGenMacroJavaScriptHelper.registerTypeMapping(Instant.class, JavaScriptComplexType.DATE);
    CobiGenMacroJavaScriptHelper.registerTypeMapping(LocalDateTime.class, JavaScriptComplexType.DATE);
    CobiGenMacroJavaScriptHelper.registerTypeMapping(Year.class, JavaScriptBasicType.NUMBER);
    CobiGenMacroJavaScriptHelper.registerTypeMapping(Month.class, JavaScriptBasicType.NUMBER);
    CobiGenMacroJavaScriptHelper.registerTypeMapping(YearMonth.class, JavaScriptComplexType.YEAR_MONTH);
  }

  /**
   * Initializes Java8 support.
   */
  public static void init() {

    // so far all is already setup on class-loading so nothing to do here.
  }

}
