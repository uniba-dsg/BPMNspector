package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import de.uniba.dsg.ppn.ba.xml.XmlWriter;

public class Main {

	public static void main(String[] args) {
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		ArrayList<String> argsAsList = new ArrayList<>(Arrays.asList(args));
		if (argsAsList.contains("--debug")) {
			validator.setLogLevel(Level.DEBUG);
			argsAsList.remove("--debug");
		}

		XmlWriter xmlWriter = new XmlWriter();

		// TODO: parallelization with executor framework?
		if (argsAsList.size() > 0) {
			for (String parameter : argsAsList) {
				try {
					File file = new File(parameter);
					ValidationResult result = validator.validate(file);
					xmlWriter.writeResult(result,
							new File(file.getParentFile() + File.separator
									+ "validation_result_" + file.getName()
									+ ".xml"));
				} catch (BpmnValidationException e) {
					System.err.println(e.getMessage());
				} catch (JAXBException e) {
					System.err.println("result couldn't be written in xml!");
				}
			}
		} else {
			System.err.println("There must be files to check!");
			System.exit(-1);
		}

	}
}
