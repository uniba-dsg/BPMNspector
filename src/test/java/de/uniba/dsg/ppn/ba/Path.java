package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Path {

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
	public void testConstraintSuccess1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "path"
				+ File.separator + "folder" + File.separator
				+ "success_import.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
		;
	}

	@Test
	public void testConstraintSuccess2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "path"
				+ File.separator + "success_import.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

}
