package com.devonfw.cobigen.cli.utils;

/**
 * This class is implementing process bar for when user first time running the
 * CobiGen CLI because first time downloading the dependency it is taking time .
 */

public class ProgressBar implements Runnable {

	private static void printProgress(long startTime, long total) {
		StringBuilder string = new StringBuilder(140);
		string.append("...");
		System.out.print(".");

	}

	@Override
	public void run() {
		long total = 100;
		long startTime = System.currentTimeMillis();
		System.out.println("");
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

				System.out.println("");
				return;
			}
			printProgress(startTime, total);

		}

	}

}
