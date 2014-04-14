package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PreProcessor {

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;

	{
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// ignore
		}
		xPathFactory = XPathFactory.newInstance();
		xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext(new BpmnNamespaceContext());
	}

	public ByteArrayInputStream preProcess(Document headFileDocument,
			File folder) throws SAXException, IOException,
			XPathExpressionException, TransformerException {
		headFileDocument = integrateImports(headFileDocument, folder);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer
				.transform(new DOMSource(headFileDocument), new StreamResult(
						new OutputStreamWriter(outputStream, "UTF-8")));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				outputStream.toByteArray());

		return inputStream;
	}

	private Document integrateImports(Document headFileDocument, File folder)
			throws XPathExpressionException, SAXException, IOException,
			TransformerException {
		Object[][] importedFiles = selectImportedFiles(headFileDocument, folder);
		removeBPMNDINode(headFileDocument);

		XPathExpression xPathChangeNamespaceIds = xpath
				.compile("//bpmn:*/@calledElement | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryRef | //bpmn:*/eventDefinitionRef");
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
				removeBPMNDINode(importedDocument);

				XPathExpression xPathReplaceIds = xpath
						.compile("//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryRef | //bpmn:*/eventDefinitionRef | //bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | //bpmn:dataOutputRefs");
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

	public Object[][] selectImportedFiles(Document document, File folder) {
		Element definitionsNode = document.getDocumentElement();
		NodeList importedFilesList = document.getElementsByTagNameNS(
				SchematronBPMNValidator.bpmnNamespace, "import");
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

	public void removeBPMNDINode(Document headFileDocument) {
		Element definitionsNode = headFileDocument.getDocumentElement();
		NodeList bpmnDiagramNode = headFileDocument.getElementsByTagNameNS(
				SchematronBPMNValidator.bpmndiNamespace, "BPMNDiagram");
		if (bpmnDiagramNode.getLength() > 0) {
			definitionsNode.removeChild(bpmnDiagramNode.item(0));
		}
	}
}
