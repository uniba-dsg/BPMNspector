package de.uniba.dsg.ppn.ba.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.ppn.ba.helper.BpmnHelper;
import de.uniba.dsg.ppn.ba.helper.ImportedFilesCrawler;
import de.uniba.dsg.ppn.ba.helper.SetupHelper;

/**
 * Does the preprocessing step for creating only one document contenting
 * everything and a namespace table
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class PreProcessor {

    private final DocumentBuilder documentBuilder;
    private final XPath xpath;
    private static final Logger LOGGER;

    static {
        LOGGER = (Logger) LoggerFactory.getLogger(PreProcessor.class
                .getSimpleName());
    }

    {
        documentBuilder = SetupHelper.setupDocumentBuilder();
        xpath = SetupHelper.setupXPath();
    }

    /**
     *
     * does the preprocess step for creating one document including the content
     * of all imported files of the document and a table with the namespaces and
     * unique prefixes of all imports
     *
     * @param headFileDocument
     *            the head document, where all nodes will be added then
     * @param folder
     *            the folder of the headFileDocument
     * @param namespaceTable
     *            the list with all already found namespaces and their new
     *            unique prefixes
     * @return the preprocess result with the preprocessed one document having
     *         all content and the namespace table with all found namespaces and
     *         their unique prefixes
     * @throws XPathExpressionException
     *             if a xpath expression is invalid
     */
    public PreProcessResult preProcess(Document headFileDocument, File folder,
            List<String[]> namespaceTable) throws XPathExpressionException {
        List<ImportedFile> importedFiles = ImportedFilesCrawler
                .selectImportedFiles(headFileDocument, folder,
                        namespaceTable.size(), true);
        if(importedFiles.isEmpty()) {
            LOGGER.debug("Skipping preprocessing for '{}' as there are no imports.", headFileDocument.getBaseURI());
        } else {
            LOGGER.info("Starting to preprocess file.");

            BpmnHelper.removeBPMNDINode(headFileDocument);

            XPathExpression xPathChangeNamespaceIds = xpath
                    .compile(
                            "//bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@calledElement | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef");
            NodeList foundNodesHeadFile = (NodeList) xPathChangeNamespaceIds
                    .evaluate(headFileDocument, XPathConstants.NODESET);

            for (int j = 0; j < foundNodesHeadFile.getLength(); j++) {
                Node idNode = foundNodesHeadFile.item(j);
                if (idNode.getTextContent().contains(":")) {
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
                    LOGGER.debug("new prefix '{}' for ID {}", newPrefix, idNode.getTextContent());
                    idNode.setTextContent(idNode.getTextContent().replace(
                            prefix + ":", newPrefix + "_"));
                }
            }

            for (ImportedFile importedFile : importedFiles) {
                if (importedFile.getFile().exists()) {
                    addNamespacesAndRenameIds(headFileDocument,
                            importedFile, namespaceTable, folder);
                }
            }
            LOGGER.info("Preprocessing completed.");
        }

        return new PreProcessResult(headFileDocument, namespaceTable);
    }

    private void addNamespacesAndRenameIds(Document headFileDocument,
            ImportedFile file, List<String[]> namespaceTable, File folder)
            throws XPathExpressionException {
        try {
            Document importedDocument = documentBuilder.parse(file.getFile());

            Element importDefinitionsNode = importedDocument
                    .getDocumentElement();
            BpmnHelper.removeBPMNDINode(importedDocument);

            boolean exists = false;
            for (String[] s : namespaceTable) {
                if (s[1].equals(file.getNamespace())) {
                    exists = true;
                }
            }
            LOGGER.debug("namespace of file read: {}", file.getNamespace());
            if (!exists) {
                namespaceTable.add(new String[] { file.getPrefix(),
                        file.getNamespace() });
            }
            XPathExpression xPathReplaceIds = xpath
                    .compile("//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef | //bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | //bpmn:dataOutputRefs");
            renameIds(xPathReplaceIds, importedDocument, file.getPrefix());

            LOGGER.debug("Checking imported file for further imports.");
            preProcess(importedDocument, folder, namespaceTable);

            LOGGER.debug("integration of document will be done now");

            addNodesToDocument(importDefinitionsNode, headFileDocument);
        } catch (SAXException | IOException e) {
            LOGGER.debug("imported file {} couldn't be read. Cause: {}", file
                    .getFile().getName(), e);
            LOGGER.error("imported file {} couldn't be read.", file.getFile()
                    .getName());
        }
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
     * adds the childs of importDefinitionsNode to the definitionsNode of the
     * given headFileDocument
     *
     * @param importDefinitionsNode
     *            the definitionsNode of the document, which should be added to
     *            the headFileDocument
     * @param headFileDocument
     *            the document, where the nodes should be added
     *
     */
    private void addNodesToDocument(Node importDefinitionsNode,
            Document headFileDocument) {
        Element definitionsNode = headFileDocument.getDocumentElement();

        for (int j = 0; j < importDefinitionsNode.getChildNodes().getLength(); j++) {
            Node importedNode = headFileDocument.importNode(
                    importDefinitionsNode.getChildNodes().item(j), true);
            definitionsNode.appendChild(importedNode);
        }
    }
}
