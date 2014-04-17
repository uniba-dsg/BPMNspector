package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext100 {

	SchematronBPMNValidator validator = null;

	@Before
	public void setUp() throws Exception {
		validator = new SchematronBPMNValidator();
		validator.setLogLevel(Level.OFF);
	}

	@After
	public void tearDown() throws Exception {
		validator = null;
	}

	@Test
	public void testConstraintEventFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "fail_event.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"No EventDefinition is allowed for Start Events in Sub-Process definitions",
				v.getMessage());
		assertEquals(
				"//bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent[0]",
				v.getxPath());
		assertEquals(10, v.getLine());
	}

	@Test
	public void testConstraintTransactionEventFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "fail_event_transaction.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"No EventDefinition is allowed for Start Events in Sub-Process definitions",
				v.getMessage());
		assertEquals("//bpmn:transaction/bpmn:startEvent[0]", v.getxPath());
		assertEquals(10, v.getLine());
	}

	@Test
	public void testConstraintEventRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "fail_event_ref.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"No EventDefinition is allowed for Start Events in Sub-Process definitions",
				v.getMessage());
		assertEquals(
				"//bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent[0]",
				v.getxPath());
		assertEquals(11, v.getLine());
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "success.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}

	@Test
	public void testConstraintEventSubProcessSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "100" + File.separator
				+ "success_event_sub.bpmn");
		ValidationResult result = validator.validate(f);
		assertTrue(result.isValid());
		assertTrue(result.getViolations().isEmpty());
	}
}
