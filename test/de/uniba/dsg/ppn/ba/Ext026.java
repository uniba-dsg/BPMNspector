package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext026 {

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
	public void testConstraintActivityFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "026" + File.separator
				+ "fail_activity.bpmn");
		boolean valid = validator.validate(f);
		assertEquals(valid, false);
		assertEquals(
				validator.getErrors(),
				"//bpmn:task[@default][0]: If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef");
	}

	@Test
	public void testConstraintGatewayFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "026" + File.separator
				+ "fail_gateway.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:exclusiveGateway[@default][0]: If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "026" + File.separator
				+ "success.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
