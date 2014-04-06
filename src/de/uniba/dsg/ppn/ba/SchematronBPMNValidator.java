package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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

	private static StringBuffer error;
	private static XmlReader xmlReader;
	private static DocumentBuilderFactory documentBuilderFactory;
	private static DocumentBuilder documentBuilder;
	private static XPathFactory xPathFactory;
	private static XPath xpath;
	private static XPathExpression xPathExpr;

	static {
		error = new StringBuffer();
		xmlReader = new XmlReader();
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// ignore
		}
		xPathFactory = XPathFactory.newInstance();
		xpath = xPathFactory.newXPath();
		try {
			xPathExpr = xpath.compile("//*/@id");
		} catch (XPathExpressionException e) {
			// ignore
		}
	}

	public static boolean validateViaPureSchematron(File xmlFile)
			throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}

		error = new StringBuffer();

		String xml = xmlReader.readXmlFile(xmlFile);

		String errorMessage = checkConstraint001(xml, xmlFile.getParentFile());
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}
		errorMessage = checkConstraint002(xml, xmlFile.getParentFile());
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
		Document doc = documentBuilder.parse(new InputSource(new StringReader(
				xml)));
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

	private static String checkConstraint002(String xml, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		Document doc = documentBuilder.parse(new InputSource(new StringReader(
				xml)));

		String namespace = doc.getDocumentElement().getAttribute(
				"targetNamespace");
		NodeList importList = doc.getElementsByTagName("import");

		boolean valid = true;
		for (int i = 0; i < importList.getLength(); i++) {
			Node importFile = importList.item(i);
			NodeList result = (NodeList) xPathExpr.evaluate(doc,
					XPathConstants.NODESET);
			File f = new File(folder.getPath()
					+ File.separator
					+ importFile.getAttributes().getNamedItem("location")
							.getTextContent());
			Document importDoc = documentBuilder.parse(new InputSource(
					new StringReader(new XmlReader().readXmlFile(f))));
			NodeList importResult = (NodeList) xPathExpr.evaluate(importDoc,
					XPathConstants.NODESET);
			if (importFile.getAttributes().getNamedItem("namespace")
					.getTextContent().equals(namespace)) {
				for (int k = 1; k < result.getLength(); k++) {
					String resultId = result.item(k).getNodeValue();
					for (int l = 1; l < importResult.getLength(); l++) {
						String importResultId = importResult.item(l)
								.getNodeValue();
						if (resultId.equals(importResultId)) {
							valid = false;
						}
					}
				}
			}
			for (int j = i + 1; j < importList.getLength(); j++) {
				Node importFile2 = importList.item(j);
				if (importFile
						.getAttributes()
						.getNamedItem("namespace")
						.getTextContent()
						.equals(importFile2.getAttributes()
								.getNamedItem("namespace").getTextContent())) {
					File f2 = new File(folder.getPath()
							+ File.separator
							+ importFile.getAttributes()
									.getNamedItem("location").getTextContent());
					Document importDoc2 = documentBuilder
							.parse(new InputSource(new StringReader(
									new XmlReader().readXmlFile(f2))));
					NodeList importResult2 = (NodeList) xPathExpr.evaluate(
							importDoc2, XPathConstants.NODESET);
					for (int k = 1; k < importResult.getLength(); k++) {
						String resultId = importResult.item(k).getNodeValue();
						for (int l = 1; l < importResult2.getLength(); l++) {
							String importResultId = importResult2.item(l)
									.getNodeValue();
							if (resultId.equals(importResultId)) {
								valid = false;
							}
						}
					}
				}
			}
		}

		String message = valid ? "" : "Files have double ids";

		return message;
	}

	public static void printXpathResult(NodeList result) {
		NodeList nodes = result;
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeValue());
		}
	}

	public static String getErrors() {
		return error.toString().trim();
	}

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "002\\fail_import2.bpmn");

		boolean check = SchematronBPMNValidator.validateViaPureSchematron(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);
		if (!check) {
			System.out.println(error);
		}
	}
}
