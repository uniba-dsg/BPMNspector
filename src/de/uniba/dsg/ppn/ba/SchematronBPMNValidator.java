package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.transform.stream.StreamSource;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

import de.uniba.dsg.ppn.ba.xml.XmlReader;

public class SchematronBPMNValidator {

	public static boolean validateViaPureSchematron(String xml)
			throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}
		return schematronSchema.getSchematronValidity(
				new StreamSource(
						new ByteArrayInputStream(xml.getBytes("UTF-8"))))
				.isValid();
	}

	public static void main(String[] args) throws Exception {
		File f = new File("E:\\Philipp\\BA\\testprocesses\\101\\fail.bpmn");

		XmlReader r = new XmlReader();
		String text = r.readXmlFile(f);

		boolean check = SchematronBPMNValidator.validateViaPureSchematron(text);
		System.out.println("File is valid? " + check);
	}
}
