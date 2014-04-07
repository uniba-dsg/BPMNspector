package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext146 {

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
	public void testConstraintLinkFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_link.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintTimerFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_timer.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintTimerRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_timer_ref.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintMultipleFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_multiple.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintConditionalFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
				+ "fail_conditional.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:endEvent[0]: Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_message.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}

	@Test
	public void testConstraintMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_multiple.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}

}
