package de.uniba.dsg.ppn.ba.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
    private final XPathExpression xPathChangeNamespaceIds;
    private final XPathExpression xPathReplaceIds;
    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(PreProcessor.class.getSimpleName());
    }

    {
        documentBuilder = SetupHelper.setupDocumentBuilder();
        xpath = SetupHelper.setupXPath();
        xPathChangeNamespaceIds = setupXPathNamespaceIds();
        xPathReplaceIds = setupXPathReplaceIds();
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
            Map<String, String> namespaceTable) throws XPathExpressionException {
        List<ImportedFile> importedFiles = ImportedFilesCrawler
                .selectImportedFiles(headFileDocument, folder,
                        namespaceTable.size(), true);

        BpmnHelper.removeBPMNDINode(headFileDocument);

        if (importedFiles.isEmpty()) {
            LOGGER.debug(
                    "Skipping preprocessing for '{}' as there are no imports.",
                    headFileDocument.getBaseURI());
        } else {
            LOGGER.info("Starting to preprocess file.");

            NodeList foundNodesHeadFile = (NodeList) xPathChangeNamespaceIds
                    .evaluate(headFileDocument, XPathConstants.NODESET);

            for (int j = 0; j < foundNodesHeadFile.getLength(); j++) {
                Node idNode = foundNodesHeadFile.item(j);
                if (idNode.getTextContent().contains(":")) {
                    renameGlobalIds(headFileDocument, importedFiles, idNode);
                }
            }

            for (ImportedFile importedFile : importedFiles) {
                if (importedFile.getFile().exists()) {
                    addNamespacesAndRenameIds(headFileDocument, importedFile,
                            namespaceTable, folder);
                }
            }
            LOGGER.info("Preprocessing completed.");
        }

        return new PreProcessResult(headFileDocument, namespaceTable);
    }

    /**
     * helper method to rename all global ids in the bpmn files to be able to
     * merge them into one file and to detect later the errors in the right
     * files. the colon is replaced by an underscore because ids in the one
     * merged file can't contain colons
     *
     * @param headFileDocument
     *            the document which should be validated
     * @param importedFiles
     *            all imported files of the headFileDocument
     * @param idNode
     *            the id node which should be renamed
     */
    private void renameGlobalIds(Document headFileDocument,
            List<ImportedFile> importedFiles, Node idNode) {
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
        LOGGER.debug("new prefix '{}' for ID {}", newPrefix,
                idNode.getTextContent());
        idNode.setTextContent(idNode.getTextContent().replace(prefix + ":",
                newPrefix + "_"));
    }

    /**
     *
     * collects the namespaces of every imported file and starts the renaming
     * process of the ids and the merging into one file process
     *
     * @param headFileDocument
     *            the document which should be validated
     * @param file
     *            the imported file to be analyzed and added
     * @param namespaceTable
     *            the table with all namespaces of the imported files
     * @param folder
     *            the folder of the headFileDocument
     * @throws XPathExpressionException
     *             if the xpath is not valid
     */
    private void addNamespacesAndRenameIds(Document headFileDocument,
            ImportedFile file, Map<String, String> namespaceTable, File folder)
                    throws XPathExpressionException {
        try {
            Document importedDocument = documentBuilder.parse(file.getFile());

            Element importDefinitionsNode = importedDocument
                    .getDocumentElement();
            BpmnHelper.removeBPMNDINode(importedDocument);

            LOGGER.debug("namespace of file read: {}", file.getNamespace());
            if (!namespaceTable.containsKey(file.getNamespace())) {
                namespaceTable.put(file.getNamespace(), file.getPrefix());
            }
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
     *            the expression to be evaluated in the document
     * @param document
     *            the document to be checked
     * @param namespacePrefix
     *            the new prefix to be set for the node
     * @throws XPathExpressionException
     *             if the xpath is not valid
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

    /**
     * helper method to easily set up the namespace collecting for the renaming
     * of the ids
     */
    private XPathExpression setupXPathNamespaceIds() {
        XPathExpression xPathChangeNamespaceIds = null;
        try {
            xPathChangeNamespaceIds = xpath
                    .compile("//bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@calledElement | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef");
        } catch (XPathExpressionException e) {
            // won't happen, developer's responsibility
        }

        return xPathChangeNamespaceIds;
    }

    /**
     * helper method to easily set up the id collecting for the renaming of the
     * ids
     */
    private XPathExpression setupXPathReplaceIds() {
        XPathExpression xPathReplaceIds = null;
        try {
            xPathReplaceIds = xpath
                    .compile("//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef | //bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | //bpmn:dataOutputRefs");
        } catch (XPathExpressionException e) {
            // won't happen, developer's responsibility
        }

        return xPathReplaceIds;
    }
}
