package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext102 {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\102\\Fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[@sourceRef][0]: A Start Event MUST NOT be a source for a message flow");
	}
}
