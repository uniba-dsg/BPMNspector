package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext007 {

	@Test
	public void testConstraintAssociationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "\\testprocesses\\007\\Fail_association.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: An Artifact MUST NOT be a source for a Sequence Flow");
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "\\testprocesses\\007\\Fail_group.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: An Artifact MUST NOT be a source for a Sequence Flow");
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "\\testprocesses\\007\\Fail_text_annotation.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: An Artifact MUST NOT be a source for a Sequence Flow");
	}
}
