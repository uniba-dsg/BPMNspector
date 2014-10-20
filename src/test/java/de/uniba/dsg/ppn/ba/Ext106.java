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

public class Ext106 {

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
		File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
				+ "fail_cancel_end_event.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"A cancel EndEvent is only allowed in a transaction sub-process",
				v.getMessage());
		assertEquals("//bpmn:cancelEventDefinition[0]", v.getxPath());
		assertEquals(9, v.getLine());
	}

	@Test
	public void testConstraintEventRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
				+ "fail_sub_process.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"A cancel EndEvent is only allowed in a transaction sub-process",
				v.getMessage());
		assertEquals("//bpmn:cancelEventDefinition[0]", v.getxPath());
		assertEquals(24, v.getLine());
	}

    @Test
    public void testConstraintCancelEventSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "success_cancel_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintCancelBoundaryEventSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "success_cancel_boundary_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

}
