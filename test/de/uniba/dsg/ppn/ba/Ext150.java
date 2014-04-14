package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		assertEquals(
				validator.getErrors(),
				"//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][4]: If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow\r\n//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]: If end events are used, all flow nodes must have an outgoing sequence flow");
	}

	@Test
	public void testConstraintNormalSequenceFlowFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_normal_sequence_flow_missing_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]: If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow\r\n//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][3]: If end events are used, all flow nodes must have an outgoing sequence flow");
	}

	@Test
	public void testConstraintSequenceFlowInSubProcessFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_sequence_flow_in_sub_process_missing_1.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:parallelGateway[parent::*/bpmn:startEvent][0]: If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow\r\n//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][0]: If end events are used, all flow nodes must have an outgoing sequence flow");
	}

	@Test
	public void testConstraintSequenceFlowInSubProcessFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "150" + File.separator
				+ "fail_sequence_flow_in_sub_process_missing_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:parallelGateway[0]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows\r\n//bpmn:callActivity[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][0]: If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow");
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
