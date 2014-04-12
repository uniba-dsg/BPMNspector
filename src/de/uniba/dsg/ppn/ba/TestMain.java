package de.uniba.dsg.ppn.ba;

import java.io.File;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
				+ "fail_import.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		boolean check = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);
		if (!check) {
			System.out.println(validator.getErrors());
		}
	}

}
