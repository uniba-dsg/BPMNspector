package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;

public class Ext150 {

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
	public void testConstraintNormalSequenceFlowFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_normal_sequence_flow_missing_1.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][4]",
				v.getxPath());
		assertEquals(55, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"If end events are used, all flow nodes must have an outgoing sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]",
				v.getxPath());
		assertEquals(7, v.getLine());
	}

	@Test
	public void testConstraintNormalSequenceFlowFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_normal_sequence_flow_missing_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
				v.getxPath());
		assertEquals(8, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"If end events are used, all flow nodes must have an outgoing sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][3]",
				v.getxPath());
		assertEquals(49, v.getLine());
	}

	@Test
	public void testConstraintSequenceFlowInSubProcessFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_sequence_flow_in_sub_process_missing_1.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
				v.getMessage());
		assertEquals("//bpmn:parallelGateway[parent::*/bpmn:startEvent][0]",
				v.getxPath());
		assertEquals(13, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"If end events are used, all flow nodes must have an outgoing sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][0]",
				v.getxPath());
		assertEquals(10, v.getLine());
	}

	@Test
	public void testConstraintSequenceFlowInSubProcessFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_sequence_flow_in_sub_process_missing_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
				v.getMessage());
		assertEquals("//bpmn:parallelGateway[0]", v.getxPath());
		assertEquals(14, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:callActivity[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][0]",
				v.getxPath());
		assertEquals(38, v.getLine());
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "success.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintSuccess2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "success_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

}
