package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext106 {

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
	public void testConstraintEventFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
				+ "fail_cancel_end_event.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:cancelEventDefinition[0]: A cancel EndEvent is only allowed in a transaction sub-process");
	}

	@Test
	public void testConstraintEventRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
				+ "fail_sub_process.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:cancelEventDefinition[0]: A cancel EndEvent is only allowed in a transaction sub-process");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
				+ "success.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
