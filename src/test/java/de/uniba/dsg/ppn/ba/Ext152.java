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
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext152 {

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
	public void testConstraintFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "152" + File.separator
				+ "fail_1.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("A Sequence Flow must not cross the border of a Pool",
				v.getMessage());
		assertEquals("//bpmn:sequenceFlow[0]", v.getxPath());
		assertEquals(16, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
				v.getMessage());
		assertEquals(
				"//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
				v.getxPath());
		assertEquals(7, v.getLine());
	}

	@Test
	public void testConstraintFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "152" + File.separator
				+ "fail_2.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("A Sequence Flow must not cross the border of a Pool",
				v.getMessage());
		assertEquals("//bpmn:sequenceFlow[1]", v.getxPath());
		assertEquals(17, v.getLine());
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
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "152" + File.separator
				+ "success.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
