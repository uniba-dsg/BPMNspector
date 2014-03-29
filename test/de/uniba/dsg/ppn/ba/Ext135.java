package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext135 {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "135\\fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:parallelGateway[0]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows\r\n//bpmn:parallelGateway[1]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows");
	}

	@Test
	public void testConstraintSubFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "135\\fail_no_connection.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:parallelGateway[0]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows");
	}

	@Test
	public void testConstraintBothMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "135\\success_multiple_in_and_out.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintOutMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "135\\success_multiple_out.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
