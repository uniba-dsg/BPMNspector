package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

public class Ext102 {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "102" + File.separator
				+ "Fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@sourceRef][0]: A Start Event MUST NOT be a source for a message flow");
	}
}
