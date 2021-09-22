package com.devonfw.cobigen.api.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util to determine JVM version
 */
public class JvmUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(JvmUtil.class);

  /** Holds the information whether the currently executed JVM is running java 9 or later */
  private static boolean runningJava9OrLater = determineRunningJava9OrLater();

  /**
   * @return <code>true</code> if the currently executed JVM is running java 9 or later, <code>false</code> otherwise
   */
  public static boolean isRunningJava9OrLater() {

    return runningJava9OrLater;
  }

  /**
   * @return whether the currently running JVM is of version 9 or later
   */
  private static boolean determineRunningJava9OrLater() {

    Runtime runtime = Runtime.getRuntime();
    try {
      Method getVersion = Runtime.class.getMethod("version");
      Object version = getVersion.invoke(runtime);
      Method getMajorVersion = version.getClass().getMethod("major");
      int majorVersion = (int) getMajorVersion.invoke(version);
      runningJava9OrLater = majorVersion >= 9;
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e1) {
      LOG.info("Detected a JVM < 9");
      LOG.debug(
          "Interpreted the running JVM as a version less than 9 as we could not find/execute Runtime.version() method.",
          LOG.isDebugEnabled() ? e1 : null);
      runningJava9OrLater = false;
    }
    return runningJava9OrLater;
  }
}
