package de.uniba.dsg.ppn.ba;

import java.io.File;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.Helper;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
				+ "fail_end.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		ValidationResult result = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? "
				+ result.isValid());

		if (!result.isValid()) {
			Helper.printViolations(result.getViolations());
		}

	}
}
