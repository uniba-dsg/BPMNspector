package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;

public class Ext009 {

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
	public void testConstraintAssociationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "009" + File.separator
				+ "Fail_association.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("An Artifact MUST NOT be a source for a Message Flow",
				v.getMessage());
		assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "009" + File.separator
				+ "Fail_group.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("An Artifact MUST NOT be a source for a Message Flow",
				v.getMessage());
		assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "009" + File.separator
				+ "Fail_text_annotation.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("An Artifact MUST NOT be a source for a Message Flow",
				v.getMessage());
		assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
	}
}
