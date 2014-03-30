package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext023 {

	@Test
	public void testConstraintNoIncomingFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "023\\fail_no_incoming.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: The target element of the sequence flow must reference the SequenceFlow definition using their incoming attributes.");
	}

	@Test
	public void testConstraintNoOutgoingFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "023\\fail_no_outgoing.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: The source element of the sequence flow must reference the SequenceFlow definition using their outcoming attribute.");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "023\\success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
