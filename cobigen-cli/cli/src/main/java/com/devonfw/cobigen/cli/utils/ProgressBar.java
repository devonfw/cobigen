package com.devonfw.cobigen.cli.utils;

/**
 * This class is implementing process bar for when user first time running the CobiGen CLI because first time
 * downloading the dependency it is taking time .
 */

public class ProgressBar implements Runnable {

  private static void printProgress() {

    System.out.print(".");
  }

  @Override
  public void run() {

    System.out.println("");
    while (!Thread.interrupted()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        System.out.println("");
        return;
      }
      printProgress();
    }
  }
}
