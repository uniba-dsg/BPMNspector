package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

		error = new StringBuffer();

		XmlReader r = new XmlReader();
		String xml = r.readXmlFile(xmlFile);

		String errorMessage = checkConstraint001(xml, xmlFile.getParentFile());
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}
		errorMessage = checkConstraint002(xml);
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}

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

		if (!error.toString().isEmpty()) {
			valid = false;
		}

		return valid;
	}

	private static String checkConstraint001(String xml, File folder)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		NodeList importList = doc.getElementsByTagName("import");

		boolean valid = true;
		for (int i = 0; i < importList.getLength(); i++) {
			Node importFile = importList.item(i);
			File f = new File(folder.getPath()
					+ File.separator
					+ importFile.getAttributes().getNamedItem("location")
							.getTextContent());
			if (!f.exists()) {
				valid = false;
				break;
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	private static String checkConstraint002(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

		String namespace = doc.getDocumentElement().getAttribute("xmlns:ns1");
		NodeList importList = doc.getElementsByTagName("import");

		boolean valid = true;
		for (int i = 0; i < importList.getLength(); i++) {
			Node importFile = importList.item(i);
			if (importFile.getAttributes().getNamedItem("namespace")
					.getTextContent().equals(namespace)) {
				// TODO: id duplicate check
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	public static String getErrors() {
		return error.toString().trim();
	}

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "001\\success.bpmn");

		boolean check = SchematronBPMNValidator.validateViaPureSchematron(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);
		if (!check) {
			System.out.println(error);
		}
	}
}
