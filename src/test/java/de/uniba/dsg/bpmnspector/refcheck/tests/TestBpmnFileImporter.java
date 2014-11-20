package de.uniba.dsg.bpmnspector.refcheck.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;
import de.uniba.dsg.bpmnspector.refcheck.importer.FileImporter;
import de.uniba.dsg.bpmnspector.refcheck.importer.ProcessFileSet;

public class TestBpmnFileImporter {
	
	private static FileImporter importer;
	
	@BeforeClass
	public static void setup() {
		Properties language = new Properties();
		try {
			language.load(new FileInputStream(new File("src/main/resources/en.lang")));
			importer = new FileImporter(language);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	@Test
	public void testSingleImport() {
		try {
			ProcessFileSet fileSet = importer.loadAllFiles("src/test/resources/test-18-referenz-6-teil-2.bpmn", false);
			assertNotNull(fileSet.getBpmnBaseFile());
		} catch (ValidatorException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBpmnImportChaining() {
		try {
			ProcessFileSet fileSet = importer.loadAllFiles("src/test/resources/test-import-2steps.bpmn", true);
			assertNotNull(fileSet.getBpmnBaseFile());
			assertTrue(fileSet.getReferencedBpmnFiles().size()==2);
		} catch (ValidatorException e) {
			e.printStackTrace();
		}
	}

}
