package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext096 {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\096\\Fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[0]: A Start Event must not have an incoming sequence flow");
	}
}
