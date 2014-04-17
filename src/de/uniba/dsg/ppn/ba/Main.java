package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Main {

	public static void main(String[] args) {
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		ArrayList<String> argsAsList = new ArrayList<>(Arrays.asList(args));
		if (argsAsList.contains("--debug")) {
			validator.setLogLevel(Level.DEBUG);
			argsAsList.remove("--debug");
		}

		List<ValidationResult> results = new ArrayList<ValidationResult>();

		// TODO: parallelization with executor framework?
		if (argsAsList.size() > 0) {
			for (String parameter : argsAsList) {
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
