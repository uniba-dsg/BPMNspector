package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext023 {

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
	public void testConstraintNoIncomingFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "023" + File.separator
				+ "fail_no_incoming.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: The target element of the sequence flow must reference the SequenceFlow definition using their incoming attributes.");
	}

	@Test
	public void testConstraintNoOutgoingFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "023" + File.separator
				+ "fail_no_outgoing.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: The source element of the sequence flow must reference the SequenceFlow definition using their outcoming attribute.");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "023" + File.separator
				+ "success.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
