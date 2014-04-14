package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext098 {

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
	public void testConstraintCancelFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_cancel.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events\r\n//bpmn:cancelEventDefinition[0]: A cancel EndEvent is only allowed in a transaction sub-process");
	}

	@Test
	public void testConstraintCompensateFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_compensate.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintErrorFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_error.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintEscalationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_escalation.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintEscalationRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_escalation_ref.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintLinkFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_link.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintMultipleFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_multiple.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintTerminateFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_terminate.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintConditionalSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_conditional.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintMessageSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_message.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_multiple.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintNoneSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_none.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintSignalSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_signal.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintTimerSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_timer.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
