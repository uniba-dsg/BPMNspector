package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext076 {

	SchematronBPMNValidator validator = null;

	@Before
	public void setUp() throws Exception {
		validator = new SchematronBPMNValidator();
		validator.setLogLevel(Level.OFF);
	}

	@After
	public void tearDown() throws Exception {
		validator = null;
	}

	@Test
	public void testConstraintFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_1.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"Naming Convention: name = Data Object Name [Data Object Reference State]",
				v.getMessage());
		assertEquals("//bpmn:dataObjectReference[@name][0]", v.getxPath());
		assertEquals(5, v.getLine());
	}

	@Test
	public void testConstraintFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"Naming Convention: name = Data Object Name [Data Object Reference State]",
				v.getMessage());
		assertEquals("//bpmn:dataObjectReference[@name][0]", v.getxPath());
		assertEquals(5, v.getLine());
	}

	@Test
	public void testConstraintFail3() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_3.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"Naming Convention: name = Data Object Name [Data Object Reference State]",
				v.getMessage());
		assertEquals("//bpmn:dataObjectReference[@name][0]", v.getxPath());
		assertEquals(5, v.getLine());
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Success.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
