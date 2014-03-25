package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext101Test {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\101\\fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[0]: A startEvent must have a outgoing subelement");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\101\\success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
