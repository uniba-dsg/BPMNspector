package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

public class SchematronBPMNValidator {

	private StringBuffer error;
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private XPathExpression xPathExpr;
	private static String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	{
		error = new StringBuffer();
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
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

	// TODO: REFACTOR!!
	public boolean validate(File xmlFile) throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}

		Document headFileDocument = documentBuilder.parse(xmlFile);

		File parentFolder = xmlFile.getParentFile();
		String errorMessage = checkConstraint001(headFileDocument, parentFolder);
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}
		errorMessage = checkConstraint002(headFileDocument, parentFolder);
		if (!errorMessage.isEmpty()) {
			error.append(errorMessage);
		}

		String xmlString = doPreprocessing(headFileDocument, parentFolder);

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

	private String checkConstraint001(Document headFileDocument, File folder)
			throws ParserConfigurationException, SAXException, IOException {
		NodeList importedFilesList = headFileDocument.getElementsByTagNameNS(
				bpmnNamespace, "import");

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

	// TODO: REFACTOR
	private String checkConstraint002(Document headFileDocument, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		NodeList importedFilesList = headFileDocument.getElementsByTagNameNS(
				bpmnNamespace, "import");
		String headFileNamespace = headFileDocument.getDocumentElement()
				.getAttribute("targetNamespace");

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
						.parse(importedFile);
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
									.parse(importedFile2);
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

	private String doPreprocessing(Document headFileDocument, File folder)
			throws SAXException, IOException, XPathExpressionException,
			TransformerException {
		headFileDocument = integrateImports(headFileDocument, folder);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(headFileDocument),
				new StreamResult(new OutputStreamWriter(os, "UTF-8")));

		NodeList bpmnDiagramNode = headFileDocument
				.getElementsByTagName("bpmndi:BPMNDiagram");
		definitionsNode.removeChild(bpmnDiagramNode.item(0));

		// TODO: add all affected attributes
		XPathExpression xPathChangeNamespaceIds = xpath
				.compile("//*/@calledElement");
		NodeList foundNodesHeadFile = (NodeList) xPathChangeNamespaceIds
				.evaluate(headFileDocument, XPathConstants.NODESET);

		for (int j = 0; j < foundNodesHeadFile.getLength(); j++) {
			Node idNode = foundNodesHeadFile.item(j);
			idNode.setNodeValue(idNode.getNodeValue().replace(":", "_"));
		}

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				Element importDefinitionsNode = importedDocument
						.getDocumentElement();
				NodeList bpmnDiagramNodes = importedDocument
						.getElementsByTagName("bpmndi:BPMNDiagram");
				importDefinitionsNode.removeChild(bpmnDiagramNodes.item(0));
				// TODO: add all affected attributes
				XPathExpression xPathReplaceIds = xpath
						.compile("//*/@id | //*/@sourceRef | //*/@targetRef");
				NodeList foundNodesImportedFile = (NodeList) xPathReplaceIds
						.evaluate(importedDocument, XPathConstants.NODESET);
				for (int j = 0; j < foundNodesImportedFile.getLength(); j++) {
					Node idNode = foundNodesImportedFile.item(j);
					String newId = importedFiles[i][1] + "_"
							+ idNode.getNodeValue();
					idNode.setNodeValue(newId);
				}

				// TODO: add all affected attributes
				XPathExpression xPathReplaceSubelements = xpath
						.compile("//*[local-name()='incoming'] | //*[local-name()='outgoing']");
				foundNodesImportedFile = (NodeList) xPathReplaceSubelements
						.evaluate(importedDocument, XPathConstants.NODESET);
				for (int j = 0; j < foundNodesImportedFile.getLength(); j++) {
					Node idNode = foundNodesImportedFile.item(j);
					String newId = importedFiles[i][1] + "_"
							+ idNode.getTextContent();
					idNode.setTextContent(newId);
				}

				for (int j = 0; j < importDefinitionsNode.getChildNodes()
						.getLength(); j++) {
					Node importedNode = headFileDocument
							.importNode(importDefinitionsNode.getChildNodes()
									.item(j), true);
					definitionsNode.appendChild(importedNode);
				}
				printDocument(importedDocument, System.out);
			}
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(headFileDocument),
				new StreamResult(new OutputStreamWriter(os, "UTF-8")));

		return os.toString("UTF-8");
	}

	private void integrateImports(File f) {

	}

	public static void printDocument(Document doc, OutputStream out)
			throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc), new StreamResult(
				new OutputStreamWriter(out, "UTF-8")));
	}

	public String getErrors() {
		return error.toString().trim();
	}

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
				+ "fail_call_ref_process.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		boolean check = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? " + check);
		if (!check) {
			System.out.println(validator.getErrors());
		}
	}
}
