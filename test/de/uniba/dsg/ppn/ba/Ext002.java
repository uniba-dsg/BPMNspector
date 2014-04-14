package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext002 {

	SchematronBPMNValidator validator = null;

	@Before
	public void setUp() throws Exception {
		validator = new SchematronBPMNValidator();
	}

	@After
	public void tearDown() throws Exception {
		validator = null;
	}

	@Test
	public void testConstraintImport1Fail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
				+ "fail_import.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(validator.getErrors(), "Files have id duplicates");
	}

	@Test
	public void testConstraintImport2Fail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
				+ "fail_import2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(validator.getErrors(), "Files have id duplicates");
	}

	@Test
	public void testConstraintImport3Fail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
				+ "fail_import3.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(validator.getErrors(), "Files have id duplicates");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
				+ "success_import.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
