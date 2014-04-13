package de.uniba.dsg.ppn.ba;

import java.io.File;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "preprocessing"
				+ File.separator + "fail_call_ref_process_call.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		boolean check = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);

		if (!check) {
			Helper.printViolations(validator.getValidationResult()
					.getViolations());
		}

	}

}
