package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext107 {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\107\\fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent[0]: An End Event MUST have at least one incoming Sequence Flow");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\107\\success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
