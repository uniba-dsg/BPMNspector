package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext100 {

	// FIXME: every subprocess element must be evaluated

	@Test
	public void testConstraintEventFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "fail_event.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:subProcess/bpmn:startEvent | bpmn:transaction/bpmn:startEvent | bpmn:adHocSubProcess/bpmn:startEvent[0]: No EventDefinition is allowed for Start Events in Sub-Process definitions");
	}

	// @Test
	// public void testConstraintTransactionEventFail() throws Exception {
	// File f = new File(TestHelper.getTestFilePath()
	// + "100" + File.separator + "fail_event_transaction.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertFalse(valid);
	// assertEquals(
	// SchematronBPMNValidator.getErrors(),
	// "//bpmn:subProcess/bpmn:startEvent | bpmn:transaction/bpmn:startEvent | bpmn:adHocSubProcess/bpmn:startEvent[0]: No EventDefinition is allowed for Start Events in Sub-Process definitions");
	// }

	@Test
	public void testConstraintEventRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "fail_event_ref.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:subProcess/bpmn:startEvent | bpmn:transaction/bpmn:startEvent | bpmn:adHocSubProcess/bpmn:startEvent[0]: No EventDefinition is allowed for Start Events in Sub-Process definitions");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
