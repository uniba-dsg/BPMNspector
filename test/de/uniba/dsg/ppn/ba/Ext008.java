package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext008 {

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
		File f = new File(TestHelper.getTestFilePath() + "008" + File.separator
				+ "Fail_association.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Message Flow");
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "008" + File.separator
				+ "Fail_group.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Message Flow");
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "008" + File.separator
				+ "Fail_text_annotation.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Message Flow");
	}
}
