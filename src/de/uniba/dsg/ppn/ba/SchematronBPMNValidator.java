package de.uniba.dsg.ppn.ba;

import java.io.File;

import javax.xml.transform.stream.StreamSource;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

public class SchematronBPMNValidator {

	public static boolean validateViaPureSchematron(File xmlFile)
			throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}
		return schematronSchema
				.getSchematronValidity(new StreamSource(xmlFile)).isValid();
	}

	public static void main(String[] args) throws Exception {
		boolean check = SchematronBPMNValidator
				.validateViaPureSchematron(new File(
						"E:\\Philipp\\BA\\testprocesses\\101\\fail.bpmn"));
		System.out.println("File is valid? " + check);
	}
}
