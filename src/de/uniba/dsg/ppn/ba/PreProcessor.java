package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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

	public PreProcessResult preProcess(Document headFileDocument, File folder,
			List<String[]> namespaceTable) throws XPathExpressionException,
			SAXException, IOException, TransformerException {
		Object[][] importedFiles = selectImportedFiles(headFileDocument,
				folder, namespaceTable.size());
		removeBPMNDINode(headFileDocument);

		XPathExpression xPathChangeNamespaceIds = xpath
				.compile("//bpmn:*/@calledElement | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryRef | //bpmn:*/eventDefinitionRef");
		NodeList foundNodesHeadFile = (NodeList) xPathChangeNamespaceIds
				.evaluate(headFileDocument, XPathConstants.NODESET);

		for (int j = 0; j < foundNodesHeadFile.getLength(); j++) {
			Node idNode = foundNodesHeadFile.item(j);
			if (idNode.getTextContent().indexOf(":") != -1) {
				String prefix = idNode.getTextContent().substring(0,
						idNode.getTextContent().indexOf(":"));
				String namespace = headFileDocument.getDocumentElement()
						.lookupNamespaceURI(prefix);
				String newPrefix = "";
				for (Object[] o : importedFiles) {
					if (namespace.equals(o[2])) {
						newPrefix = (String) o[1];
					}
				}
				idNode.setTextContent(idNode.getTextContent().replace(
						prefix + ":", newPrefix + "_"));
			}
		}

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				Element importDefinitionsNode = importedDocument
						.getDocumentElement();
				removeBPMNDINode(importedDocument);
				namespaceTable.add(new String[] { (String) importedFiles[i][1],
						(String) importedFiles[i][2] });
				XPathExpression xPathReplaceIds = xpath
						.compile("//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryRef | //bpmn:*/eventDefinitionRef | //bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | //bpmn:dataOutputRefs");
				renameIds(xPathReplaceIds, importedDocument,
						(String) importedFiles[i][1]);

				namespaceTable.addAll(preProcess(importedDocument, folder,
						namespaceTable).getNamespaceTable());

				headFileDocument = addNodesToDocument(importDefinitionsNode,
						headFileDocument);
			}
		}

		PreProcessResult result = new PreProcessResult(headFileDocument,
				namespaceTable);

		return result;
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

	public Object[][] selectImportedFiles(Document document, File folder,
			int size) {
		NodeList importedFilesList = document.getElementsByTagNameNS(
				SchematronBPMNValidator.bpmnNamespace, "import");
		Object[][] importedFiles = new Object[importedFilesList.getLength()][3];

		for (int i = 0; i < importedFilesList.getLength(); i++) {
			Node importedFileNode = importedFilesList.item(i);
			importedFiles[i][0] = new File(folder.getPath()
					+ File.separator
					+ importedFileNode.getAttributes().getNamedItem("location")
							.getTextContent());
			importedFiles[i][1] = "ns" + (i + size);
			importedFiles[i][2] = importedFileNode.getAttributes()
					.getNamedItem("namespace").getTextContent();
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
		// TODO: remove whitespacenodes?
	}
}
