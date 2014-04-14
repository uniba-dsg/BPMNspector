package de.uniba.dsg.ppn.ba;

import java.io.File;

import de.uniba.dsg.bpmn.ValidationResult;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "preprocessing"
				+ File.separator + "fail_call_ref_process_call.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		ValidationResult result = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? "
				+ result.isValid());

		if (!result.isValid()) {
			Helper.printViolations(result.getViolations());
		}

	}

}
