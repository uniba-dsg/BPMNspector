package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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

		error.append(checkConstraint001(headFileDocument, parentFolder));
		error.append(checkConstraint002(headFileDocument, parentFolder));

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
		Object[][] importedFiles = selectImportedFiles(headFileDocument, folder);

		boolean valid = true;
		for (int i = 0; i < importedFiles.length; i++) {
			if (!((File) importedFiles[i][0]).exists()) {
				valid = false;
			} else {
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				String error = checkConstraint001(importedDocument, folder);
				if (!error.isEmpty()) {
					valid = false;
				}
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	private String checkConstraint002(Document headFileDocument, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		List<Document> importedFileList = searchForImports(headFileDocument,
				folder);

		boolean valid = true;
		for (int i = 0; i < importedFileList.size(); i++) {
			Document document1 = importedFileList.get(i);
			String namespace1 = document1.getDocumentElement().getAttribute(
					"targetNamespace");
			for (int j = i + 1; j < importedFileList.size(); j++) {
				Document document2 = importedFileList.get(j);
				String namespace2 = document2.getDocumentElement()
						.getAttribute("targetNamespace");
				if (namespace1.equals(namespace2)) {
					if (!checkNamespacesAndIdDuplicates(document1, document2)) {
						valid = false;
					}
				}
			}
		}

		String message = valid ? "" : "Files have double ids";

		return message;
	}

	private List<Document> searchForImports(Document document, File folder)
			throws SAXException, IOException {
		Object[][] importedFiles = selectImportedFiles(document, folder);
		List<Document> importedFileList = new ArrayList<>();
		importedFileList.add(document);

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				importedFileList.addAll(searchForImports(importedDocument,
						folder));
			}
		}

		return importedFileList;
	}

	private boolean checkNamespacesAndIdDuplicates(Document document1,
			Document document2) throws XPathExpressionException, SAXException,
			IOException {
		NodeList foundNodes1 = (NodeList) xPathExpr.evaluate(document1,
				XPathConstants.NODESET);
		NodeList foundNodes2 = (NodeList) xPathExpr.evaluate(document2,
				XPathConstants.NODESET);
		boolean valid = true;
		for (int k = 1; k < foundNodes1.getLength(); k++) {
			String importedFile1Id = foundNodes1.item(k).getNodeValue();
			for (int l = 1; l < foundNodes2.getLength(); l++) {
				String importedFile2Id = foundNodes2.item(l).getNodeValue();
				if (importedFile1Id.equals(importedFile2Id)) {
					valid = false;
				}
			}
		}

		return valid;
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

		return os.toString("UTF-8");
	}

	private Document integrateImports(Document headFileDocument, File folder)
			throws XPathExpressionException, SAXException, IOException,
			TransformerException {
		Object[][] importedFiles = selectImportedFiles(headFileDocument, folder);
		removeBPMNNode(headFileDocument);

		XPathExpression xPathChangeNamespaceIds = xpath
				.compile("//*/@calledElement | //*/@processRef | //*/@dataStoreRef | //*/@categoryRef | //*/eventDefinitionRef");
		NodeList foundNodesHeadFile = (NodeList) xPathChangeNamespaceIds
				.evaluate(headFileDocument, XPathConstants.NODESET);

		for (int j = 0; j < foundNodesHeadFile.getLength(); j++) {
			Node idNode = foundNodesHeadFile.item(j);
			idNode.setTextContent(idNode.getTextContent().replace(":", "_"));
		}

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				Element importDefinitionsNode = importedDocument
						.getDocumentElement();
				removeBPMNNode(importedDocument);

				XPathExpression xPathReplaceIds = xpath
						.compile("//*/@id | //*/@sourceRef | //*/@targetRef | //*/@processRef | //*/@dataStoreRef | //*/@categoryRef | //*/eventDefinitionRef | //*[local-name()='incoming'] | //*[local-name()='outgoing'] | //*[local-name()='dataInputRefs'] | //*[local-name()='dataOutputRefs']");
				renameIds(xPathReplaceIds, importedDocument,
						(String) importedFiles[i][1]);

				Object[][] importedFiles2 = selectImportedFiles(
						importedDocument, folder);
				for (int j = 0; j < importedFiles2.length; j++) {
					importedDocument = integrateImports(importedDocument,
							folder);
				}

				headFileDocument = addNodesToDocument(importDefinitionsNode,
						headFileDocument);
			}
		}

		return headFileDocument;
	}

	private void renameIds(XPathExpression xpathExpression, Document document,
			String namespacePrefix) throws XPathExpressionException {
		NodeList foundNodesImportedFile = (NodeList) xpathExpression.evaluate(
				document, XPathConstants.NODESET);
		for (int j = 0; j < foundNodesImportedFile.getLength(); j++) {
			Node idNode = foundNodesImportedFile.item(j);
			String newId = namespacePrefix + "_" + idNode.getTextContent();
			idNode.setTextContent(newId);
		}
	}

	private Object[][] selectImportedFiles(Document document, File folder) {
		Element definitionsNode = document.getDocumentElement();
		NodeList importedFilesList = document.getElementsByTagNameNS(
				bpmnNamespace, "import");
		Object[][] importedFiles = new Object[importedFilesList.getLength()][2];

		for (int i = 0; i < importedFilesList.getLength(); i++) {
			Node importedFileNode = importedFilesList.item(i);
			importedFiles[i][0] = new File(folder.getPath()
					+ File.separator
					+ importedFileNode.getAttributes().getNamedItem("location")
							.getTextContent());
			importedFiles[i][1] = definitionsNode
					.lookupPrefix(importedFileNode.getAttributes()
							.getNamedItem("namespace").getTextContent());
		}

		return importedFiles;
	}

	private Document addNodesToDocument(Node importDefinitionsNode,
			Document headFileDocument) {
		Element definitionsNode = headFileDocument.getDocumentElement();

		for (int j = 0; j < importDefinitionsNode.getChildNodes().getLength(); j++) {
			Node importedNode = headFileDocument.importNode(
					importDefinitionsNode.getChildNodes().item(j), true);
			definitionsNode.appendChild(importedNode);
		}

		return headFileDocument;
	}

	private void removeBPMNNode(Document headFileDocument) {
		Element definitionsNode = headFileDocument.getDocumentElement();
		NodeList bpmnDiagramNode = headFileDocument
				.getElementsByTagName("bpmndi:BPMNDiagram");
		if (bpmnDiagramNode.getLength() > 0) {
			definitionsNode.removeChild(bpmnDiagramNode.item(0));
		}
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

}
