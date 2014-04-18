package de.uniba.dsg.ppn.ba.preprocessing;

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

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.ppn.ba.helper.BpmnNamespaceContext;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

/**
 * 
 * @author Philipp Neugebauer
 * 
 */
// TODO: javadoc
public class PreProcessor {

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private Logger logger;

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
		logger = (Logger) LoggerFactory.getLogger("BpmnValidator");
	}

	public PreProcessResult preProcess(Document headFileDocument, File folder,
			List<String[]> namespaceTable) throws XPathExpressionException,
			TransformerException {
		ImportedFile[] importedFiles = selectImportedFiles(headFileDocument,
				folder, namespaceTable.size());
		removeBPMNDINode(headFileDocument);
		logger.info("preprocessing step started");

		XPathExpression xPathChangeNamespaceIds = xpath
				.compile("//bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@calledElement | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef");
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
				for (ImportedFile importedFile : importedFiles) {
					if (namespace.equals(importedFile.getNamespace())) {
						newPrefix = importedFile.getPrefix();
					}
				}
				logger.debug("new prefix will be set {}", newPrefix);
				idNode.setTextContent(idNode.getTextContent().replace(
						prefix + ":", newPrefix + "_"));
			}
		}

		for (int i = 0; i < importedFiles.length; i++) {
			if (importedFiles[i].getFile().exists()) {
				try {
					Document importedDocument = documentBuilder
							.parse(importedFiles[i].getFile());

					Element importDefinitionsNode = importedDocument
							.getDocumentElement();
					removeBPMNDINode(importedDocument);

					boolean exists = false;
					for (String[] s : namespaceTable) {
						if (s[1].equals(importedFiles[i].getNamespace())) {
							exists = true;
						}
					}
					logger.debug("namespace of file read: {}",
							importedFiles[i].getNamespace());
					if (!exists) {
						namespaceTable.add(new String[] {
								importedFiles[i].getPrefix(),
								importedFiles[i].getNamespace() });
					}
					XPathExpression xPathReplaceIds = xpath
							.compile("//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef | //bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | //bpmn:dataOutputRefs");
					renameIds(xPathReplaceIds, importedDocument,
							importedFiles[i].getPrefix());

					preProcess(importedDocument, folder, namespaceTable);

					logger.info("integration of document will be done now");

					headFileDocument = addNodesToDocument(
							importDefinitionsNode, headFileDocument);
				} catch (SAXException | IOException e) {
					logger.error("imported file couldn't be read. Cause: {}",
							e.getCause());
				}
			}
		}

		PreProcessResult result = new PreProcessResult(headFileDocument,
				namespaceTable);

		return result;
	}

	/**
	 * adds to all nodes new and unique prefixes in the given document for the
	 * validation process and violation searching
	 * 
	 * @param xpathExpression
	 * @param document
	 * @param namespacePrefix
	 * @throws XPathExpressionException
	 */
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

	/**
	 * collects all imported files with bpmn namespace in the given document
	 * 
	 * @param document
	 *            the document, from which the imports are collected
	 * @param folder
	 *            the parent folder of the document
	 * @param size
	 *            the number of already collected imports for ensuring unique
	 *            namespace prefixes
	 * @return an array of importedFile including all bpmn imports with the
	 *         absolute path, the new namespace prefix and the namespace
	 */
	public ImportedFile[] selectImportedFiles(Document document, File folder,
			int size) {
		NodeList importedFilesList = document.getElementsByTagNameNS(
				SchematronBPMNValidator.bpmnNamespace, "import");
		ImportedFile[] importedFiles = new ImportedFile[importedFilesList
				.getLength()];

		for (int i = 0; i < importedFilesList.getLength(); i++) {
			Node importedFileNode = importedFilesList.item(i);
			ImportedFile importedFile = new ImportedFile();
			File file = new File(importedFileNode.getAttributes()
					.getNamedItem("location").getTextContent());
			if (file.isAbsolute()) {
				importedFile.setFile(file);
			} else {
				importedFile.setFile(new File(folder.getPath()
						+ File.separator
						+ importedFileNode.getAttributes()
								.getNamedItem("location").getTextContent()));
			}
			importedFile.setPrefix("ns" + (i + size));
			importedFile.setNamespace(importedFileNode.getAttributes()
					.getNamedItem("namespace").getTextContent());
			importedFiles[i] = importedFile;
		}

		return importedFiles;
	}

	/**
	 * adds the childs of importDefinitionsNode to the definitionsNode of the
	 * given headFileDocument
	 * 
	 * @param importDefinitionsNode
	 *            the definitionsNode of the document, which should be added to
	 *            the headFileDocument
	 * @param headFileDocument
	 *            the document, where the nodes should be added
	 * @return the headFileDocument
	 */
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

	/**
	 * removes all BPMNDiagram Nodes from the given file
	 * 
	 * @param headFileDocument
	 *            the document, where the BPMNDiagram Nodes should be deleted
	 *            from
	 */
	public void removeBPMNDINode(Document headFileDocument) {
		Element definitionsNode = headFileDocument.getDocumentElement();
		NodeList bpmnDiagramNode = headFileDocument.getElementsByTagNameNS(
				SchematronBPMNValidator.bpmndiNamespace, "BPMNDiagram");
		if (bpmnDiagramNode.getLength() > 0) {
			definitionsNode.removeChild(bpmnDiagramNode.item(0));
		}
	}
}
