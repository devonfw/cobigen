package com.cobigen.picocli;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobigen.picocli.utils.CreateJarFile;

public class CreateJarFileTest {
	private static Logger logger = LoggerFactory.getLogger(CreateJarFileTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	/**
	 * Test case to check user file is valid or not
	 * 
	 **/
	@Test
	public void testvalidateFile() {
		logger.info("Please enter userInput");
		Scanner inputReader = new Scanner(System.in);
		String userInput = inputReader.nextLine();
		File inputFile = new File(userInput);
		CreateJarFile createjarFile = new CreateJarFile();
		createjarFile.validateFile(inputFile);
		assertTrue(true);
	}

}
