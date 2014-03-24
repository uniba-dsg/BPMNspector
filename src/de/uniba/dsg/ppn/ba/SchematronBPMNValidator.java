package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

import de.uniba.dsg.ppn.ba.xml.XmlReader;
import de.uniba.dsg.ppn.ba.xml.XmlWriter;

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
		File f = new File("E:\\Philipp\\BA\\testprocesses\\101\\fail.bpmn");

		XmlReader r = new XmlReader();
		List<String> lineList = r.readXmlFile(f);

		f = new File("temp.xml");

		XmlWriter w = new XmlWriter();
		w.writeXml(f, lineList);

		boolean check = SchematronBPMNValidator.validateViaPureSchematron(f);
		System.out.println("File is valid? " + check);
	}
}
