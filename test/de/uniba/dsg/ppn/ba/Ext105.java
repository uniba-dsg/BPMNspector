package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext105 {

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
	public void testConstraintFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "105" + File.separator
				+ "fail_end_without_sub-events.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[0]: An end event must be present when a start event is used in the same process level");
	}

	@Test
	public void testConstraintSubFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "105" + File.separator
				+ "fail_with_sub-startevent.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[1]: An end event must be present when a start event is used in the same process level");
	}

	@Test
	public void testConstraintSubSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "105" + File.separator
				+ "success_with_sub-events.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "105" + File.separator
				+ "success_without_sub-events.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
