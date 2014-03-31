package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext099 {

	@Test
	public void testConstraintEventFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "fail_event.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	}

	@Test
	public void testConstraintEventRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "fail_eventref.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	}

	@Test
	public void testConstraintImportedProcessFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "fail_call_ref_process.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintGlobalSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "success_global.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
