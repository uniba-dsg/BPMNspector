package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext021 {

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
		File f = new File(TestHelper.getTestFilePath() + "021" + File.separator
				+ "fail_event_sub_process.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source");
	}

	@Test
	public void testConstraintEventsSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "021" + File.separator
				+ "success_events.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}

	@Test
	public void testConstraintGatewaysSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "021" + File.separator
				+ "success_gateways.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}

	@Test
	public void testConstraintTasksSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "021" + File.separator
				+ "success_tasks.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
