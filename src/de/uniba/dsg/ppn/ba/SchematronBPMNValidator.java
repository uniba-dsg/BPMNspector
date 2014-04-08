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

	private StringBuffer error;
	private XmlReader xmlReader;
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private XPathExpression xPathExpr;

	{
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

	public boolean validate(File xmlFile) throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}

		error = new StringBuffer();

		String xmlString = xmlReader.readXmlFile(xmlFile);

		File parentFolder = xmlFile.getParentFile();
		String errorMessage = checkConstraint001(xmlString, parentFolder);
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}
		errorMessage = checkConstraint002(xmlString, parentFolder);
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}

		xmlString = doPreprocessing(xmlString, parentFolder);

		boolean valid = schematronSchema.getSchematronValidity(
				new StreamSource(new ByteArrayInputStream(xmlString
						.getBytes("UTF-8")))).isValid();

		if (!valid) {
			SchematronOutputType schematronOutputType = schematronSchema
					.applySchematronValidationToSVRL(new StreamSource(
							new ByteArrayInputStream(xmlString
									.getBytes("UTF-8"))));
			for (int i = 0; i < schematronOutputType
					.getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
				if (schematronOutputType
						.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
					FailedAssert failedAssert = (FailedAssert) schematronOutputType
							.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i);
					error.append(failedAssert.getLocation() + ": "
							+ failedAssert.getText() + "\r\n");
				}
			}
		}

		if (!error.toString().isEmpty()) {
			valid = false;
		}

		return valid;
	}

	private String checkConstraint001(String xml, File folder)
			throws ParserConfigurationException, SAXException, IOException {
		Document headFileDocument = documentBuilder.parse(new InputSource(
				new StringReader(xml)));
		NodeList importedFilesList = headFileDocument
				.getElementsByTagName("import");

		boolean valid = true;
		for (int i = 0; i < importedFilesList.getLength(); i++) {
			Node importedFileNode = importedFilesList.item(i);
			File importedFile = new File(folder.getPath()
					+ File.separator
					+ importedFileNode.getAttributes().getNamedItem("location")
							.getTextContent());
			if (!importedFile.exists()) {
				valid = false;
				break;
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	private String checkConstraint002(String xml, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		Document headFileDocument = documentBuilder.parse(new InputSource(
				new StringReader(xml)));

		String headFileNamespace = headFileDocument.getDocumentElement()
				.getAttribute("targetNamespace");
		NodeList importedFilesList = headFileDocument
				.getElementsByTagName("import");

		boolean valid = true;
		for (int i = 0; i < importedFilesList.getLength(); i++) {
			Node importedFileNode = importedFilesList.item(i);
			NodeList foundNodesHeadFile = (NodeList) xPathExpr.evaluate(
					headFileDocument, XPathConstants.NODESET);
			File importedFile = new File(folder.getPath()
					+ File.separator
					+ importedFileNode.getAttributes().getNamedItem("location")
							.getTextContent());
			if (importedFile.exists()) {
				Document importedFileDocument = documentBuilder
						.parse(new InputSource(new StringReader(new XmlReader()
								.readXmlFile(importedFile))));
				NodeList foundNodesImportedFile = (NodeList) xPathExpr
						.evaluate(importedFileDocument, XPathConstants.NODESET);
				if (importedFileNode.getAttributes().getNamedItem("namespace")
						.getTextContent().equals(headFileNamespace)) {
					for (int k = 1; k < foundNodesHeadFile.getLength(); k++) {
						String headFileId = foundNodesHeadFile.item(k)
								.getNodeValue();
						for (int l = 1; l < foundNodesImportedFile.getLength(); l++) {
							String importedFileId = foundNodesImportedFile
									.item(l).getNodeValue();
							if (headFileId.equals(importedFileId)) {
								valid = false;
							}
						}
					}
				}
				for (int j = i + 1; j < importedFilesList.getLength(); j++) {
					Node importedFile2Node = importedFilesList.item(j);
					if (importedFileNode
							.getAttributes()
							.getNamedItem("namespace")
							.getTextContent()
							.equals(importedFile2Node.getAttributes()
									.getNamedItem("namespace").getTextContent())) {
						File importedFile2 = new File(folder.getPath()
								+ File.separator
								+ importedFileNode.getAttributes()
										.getNamedItem("location")
										.getTextContent());
						if (importedFile2.exists()) {
							Document importedFile2Document = documentBuilder
									.parse(new InputSource(
											new StringReader(new XmlReader()
													.readXmlFile(importedFile2))));
							NodeList foundNodesImportedFile2 = (NodeList) xPathExpr
									.evaluate(importedFile2Document,
											XPathConstants.NODESET);
							for (int k = 1; k < foundNodesImportedFile
									.getLength(); k++) {
								String importedFile1Id = foundNodesImportedFile
										.item(k).getNodeValue();
								for (int l = 1; l < foundNodesImportedFile2
										.getLength(); l++) {
									String importedFile2Id = foundNodesImportedFile2
											.item(l).getNodeValue();
									if (importedFile1Id.equals(importedFile2Id)) {
										valid = false;
									}
								}
							}
						}
					}
				}
			}
		}

		String message = valid ? "" : "Files have double ids";

		return message;
	}

	private String doPreprocessing(String xmlString, File folder)
			throws SAXException, IOException, XPathExpressionException {
		Document headFileDocument = documentBuilder.parse(new InputSource(
				new StringReader(xmlString)));
		NodeList importedFilesList = headFileDocument
				.getElementsByTagName("import");
		File[] importedFiles = new File[importedFilesList.getLength()];

		for (int i = 0; i < importedFilesList.getLength(); i++) {
			Node importedFileNode = importedFilesList.item(i);
			importedFiles[i] = new File(folder.getPath()
					+ File.separator
					+ importedFileNode.getAttributes().getNamedItem("location")
							.getTextContent());
		}

		int lastRowStart = xmlString.lastIndexOf("</definitions");
		// TODO: add all affected attributes
		XPathExpression xPathChangeNamespaceIds = xpath
				.compile("//*/@calledElement");
		NodeList foundNodesHeadFile = (NodeList) xPathChangeNamespaceIds
				.evaluate(headFileDocument, XPathConstants.NODESET);
		for (int j = 0; j < foundNodesHeadFile.getLength(); j++) {
			Node idNode = foundNodesHeadFile.item(j);
			xmlString = xmlString.replace(idNode.toString(),
					(idNode.toString().replace(":", "_")));
		}
		String oneFilePreprocessedString = xmlString.substring(0, lastRowStart);

		for (int i = 0; i < importedFiles.length; i++) {
			if (importedFiles[i].exists()) {
				String importedXml = xmlReader
						.readImportedXmlFile(importedFiles[i]);
				if (!importedXml.isEmpty()) {
					Document importedDocument = documentBuilder
							.parse(new InputSource(
									new StringReader(importedXml)));
					// TODO: add all affected attributes
					XPathExpression xPathReplaceIds = xpath
							.compile("//*/@id | //*/@sourceRef | //*/@targetRef");
					NodeList foundNodesImportedFile = (NodeList) xPathReplaceIds
							.evaluate(importedDocument, XPathConstants.NODESET);
					for (int j = 0; j < foundNodesImportedFile.getLength(); j++) {
						Node idNode = foundNodesImportedFile.item(j);
						String newId = "ns" + (i + 1) + "_"
								+ idNode.getNodeValue();
						importedXml = importedXml.replace(
								idNode.toString(),
								(idNode.toString().replace(
										idNode.getNodeValue(), newId)));
					}
					// TODO: add all affected attributes
					XPathExpression xPathReplaceSubelements = xpath
							.compile("//incoming | //outgoing");
					foundNodesImportedFile = (NodeList) xPathReplaceSubelements
							.evaluate(importedDocument, XPathConstants.NODESET);
					for (int j = 0; j < foundNodesImportedFile.getLength(); j++) {
						Node idNode = foundNodesImportedFile.item(j);
						String newId = "ns" + (i + 1) + "_"
								+ idNode.getTextContent();
						importedXml = importedXml.replace(
								"<" + idNode.getNodeName() + ">"
										+ idNode.getTextContent(),
								"<" + idNode.getNodeName() + ">" + newId);
					}
					oneFilePreprocessedString += importedXml;
				}
			}
		}
		oneFilePreprocessedString += xmlString.substring(lastRowStart);

		return oneFilePreprocessedString;
	}

	public static void printXpathResult(NodeList result) {
		NodeList nodes = result;
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeValue());
		}
	}

	public String getErrors() {
		return error.toString().trim();
	}

	public static void main(String[] args) throws Exception {
		// File f = new File(TestHelper.getTestFilePath()
		// + "099\\fail_call_ref_process.bpmn");
		File f = new File(TestHelper.getTestFilePath() + "001" + File.separator
				+ "Fail.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		boolean check = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);
		if (!check) {
			System.out.println(validator.getErrors());
		}
	}
}
