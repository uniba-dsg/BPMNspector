package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Main {

	public static void main(String[] args) {
		Logger logger = (Logger) LoggerFactory.getLogger("BpmnValidator");
		List<String> argsAsList = Arrays.asList(args);
		if (argsAsList.contains("--debug")) {
			logger.setLevel(Level.DEBUG);
			argsAsList.remove("--debug");
		}

		List<ValidationResult> results = new ArrayList<ValidationResult>();
		SchematronBPMNValidator validator = new SchematronBPMNValidator();

		if (args.length > 0) {
			for (String parameter : args) {
				try {
					results.add(validator.validate(new File(parameter)));
				} catch (BpmnValidationException e) {
					System.err.println(e.getMessage());
				}
			}
		} else {
			System.err.println("There must be files to check!");
			System.exit(-1);
		}

	}
}
