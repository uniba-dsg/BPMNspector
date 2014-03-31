package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext146 {

	@Test
	public void testConstraintLinkFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_link.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintTimerFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_timer.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintTimerRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_timer_ref.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintMultipleFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_multiple.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintConditionalFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_conditional.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_message.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_multiple.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

}
