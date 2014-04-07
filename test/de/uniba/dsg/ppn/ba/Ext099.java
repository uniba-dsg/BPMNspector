package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext099 {

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
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "fail_event.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	}

	@Test
	public void testConstraintEventRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "fail_eventref.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	}

	// FIXME: should work somewhen
	// @Test
	// public void testConstraintImportedProcessFail() throws Exception {
	// File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
	// + "fail_call_ref_process.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertFalse(valid);
	// assertEquals(
	// validator.getErrors(),
	// "//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	// }

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "success.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}

	@Test
	public void testConstraintGlobalSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "success_global.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
