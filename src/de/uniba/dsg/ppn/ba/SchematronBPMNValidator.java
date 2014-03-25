package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.transform.stream.StreamSource;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

import de.uniba.dsg.ppn.ba.xml.XmlReader;

public class SchematronBPMNValidator {

	private static StringBuffer error = new StringBuffer();

	public static boolean validateViaPureSchematron(File xmlFile)
			throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}

		XmlReader r = new XmlReader();
		String xml = r.readXmlFile(xmlFile);

		boolean valid = schematronSchema.getSchematronValidity(
				new StreamSource(
						new ByteArrayInputStream(xml.getBytes("UTF-8"))))
				.isValid();

		if (!valid) {
			SchematronOutputType sot = schematronSchema
					.applySchematronValidationToSVRL(new StreamSource(
							new ByteArrayInputStream(xml.getBytes("UTF-8"))));

			for (int i = 0; i < sot
					.getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
				if (sot.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
					FailedAssert f = (FailedAssert) sot
							.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i);
					error.append(f.getLocation() + ": " + f.getText() + "\r\n");
				}
			}
		}

		return valid;
	}

	public static void main(String[] args) throws Exception {
		File f = new File("D:\\Philipp\\BA\\testprocesses\\101\\fail.bpmn");
		// File f = new File(
		// "E:\\Philipp\\BA\\dump\\bpmn-by-example\\eMail Voting\\Email Voting 2.bpmn");
		// File f = new
		// File("E:\\Philipp\\BA\\testprocesses\\101\\success.bpmn");

		boolean check = SchematronBPMNValidator.validateViaPureSchematron(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);
		if (!check) {
			System.out.println(error);
		}
	}
}
