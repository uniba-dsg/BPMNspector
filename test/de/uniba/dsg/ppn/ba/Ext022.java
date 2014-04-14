package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext022 {

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
	public void testConstraintEventSubProcessFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
				+ "fail_event_sub_process.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target");
	}

	@Test
	public void testConstraintEventsSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
				+ "success_events.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintGatewaysSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
				+ "success_gateways.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintTasksSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
				+ "success_tasks.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
