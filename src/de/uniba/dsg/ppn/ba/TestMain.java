package de.uniba.dsg.ppn.ba;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.Helper;

public class TestMain {

	public static void main(String[] args) throws Exception {
		Logger logger = (Logger) LoggerFactory.getLogger("BpmnValidator");
		// logger.setLevel(Level.DEBUG);
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
				+ "success_import.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		ValidationResult result = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? "
				+ result.isValid());

		if (!result.isValid()) {
			Helper.printViolations(result.getViolations());
		}

	}
}
