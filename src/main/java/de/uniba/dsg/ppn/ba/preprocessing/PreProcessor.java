package de.uniba.dsg.ppn.ba.preprocessing;

import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.ppn.ba.helper.BpmnHelper;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
import de.uniba.dsg.ppn.ba.helper.ImportedFilesCrawler;
import de.uniba.dsg.ppn.ba.helper.SetupHelper;
import org.jdom2.Attribute;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    private final XPathFactory xPathFactory = XPathFactory.instance();

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

    public org.jdom2.Document preProcess(BPMNProcess process) {
        LOGGER.info("Starting to preprocess file: "+process.getBaseURI());
        LOGGER.debug("Found children: "+process.getChildren().size());
        org.jdom2.Document cloneOfDoc = process.getProcessAsDoc().clone();
        // get all nodes which potentially refer to other files
        List<Attribute> refAttributes = setupXPathNamespaceIdsForAttributes().evaluate(
                cloneOfDoc);
        List<org.jdom2.Element> refElements = setupXPathNamespaceIdsForElements().evaluate(
                cloneOfDoc);

        // for each attribute:
        for(Attribute attribute : refAttributes) {
            if(attribute.getValue().contains(":")) {
                renameGlobalIds(process, attribute);
            }
        }
        for(org.jdom2.Element element : refElements) {
            if(element.getText().contains(":")) {
                renameGlobalIds(process, element);
            }
        }

        // for each import:
        for(BPMNProcess imported : process.getChildren()){
            addNamespacesAndRenameIds(cloneOfDoc, imported);
        }

        LOGGER.info("Preprocessing of file {} completed.", process.getBaseURI());

        return cloneOfDoc;
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
     * helper method to rename all global ids in the bpmn files to be able to
     * merge them into one file and to detect later the errors in the right
     * files. the colon is replaced by an underscore because ids in the one
     * merged file can't contain colons
     *
     * @param process
     *            the document which should be validated
     * @param attribute
     *            the id node which should be renamed
     */
    private void renameGlobalIds(BPMNProcess process, Attribute attribute) {
        String prefix = attribute.getValue().substring(0,
                attribute.getValue().indexOf(":"));
        String newPrefix = getNewPrefixByOldPrefix(process, prefix);
        LOGGER.debug("new prefix '{}' for ID {}", newPrefix,
                attribute.getValue());
        attribute.setValue(attribute.getValue().replace(prefix + ":",
                newPrefix + "_"));
    }

    private String getNewPrefixByOldPrefix(BPMNProcess process,
            String oldPrefix) {
        String namespace = process.getProcessAsDoc().getRootElement().getNamespace(
                oldPrefix).getURI();
        String newPrefix = "";
        for (BPMNProcess imported : process.getChildren()) {
            if (namespace.equals(imported.getNamespace())) {
                newPrefix = imported.getGeneratedPrefix();
            }
        }
        return newPrefix;
    }

    /**
     * helper method to rename all global ids in the bpmn files to be able to
     * merge them into one file and to detect later the errors in the right
     * files. the colon is replaced by an underscore because ids in the one
     * merged file can't contain colons
     *
     * @param process
     *            the document which should be validated
     * @param element
     *            the id node which should be renamed
     */
    private void renameGlobalIds(BPMNProcess process, org.jdom2.Element element) {
        String prefix = element.getText().substring(0,
                element.getText().indexOf(":"));
        String newPrefix = getNewPrefixByOldPrefix(process, prefix);
        LOGGER.debug("new prefix '{}' for ID {}", newPrefix,
                element.getText());
        element.setText(element.getText().replace(prefix + ":",
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
     *
     * collects the namespaces of every imported process and starts the renaming
     * process of the ids and the merging into one process
     *
     * @param headDocument
     *            the document which should be validated
     * @param importedProcess
     *            the imported process to be analyzed and added
     */
    private void addNamespacesAndRenameIds(org.jdom2.Document headDocument,
            BPMNProcess importedProcess) {

            LOGGER.debug("namespace of importedProcess: {}", importedProcess.getNamespace());

            renameIds(importedProcess);

            LOGGER.debug("Checking imported importedProcess for further imports.");
            org.jdom2.Document result = preProcess(importedProcess);

            LOGGER.debug("integration of document will be done now");

            addNodesToDocument(result, headDocument);
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
     * adds to all nodes new and unique prefixes in the given document for the
     * validation process and violation searching
     *
     * @param importedProcess the process in which the ids should be changed
     */
    private void renameIds(BPMNProcess importedProcess) {

        List<Attribute> attributes = setupXPathReplaceIdsForAttributes().evaluate(importedProcess.getProcessAsDoc());
        List<org.jdom2.Element> elements = setupXPathReplaceIdsForElements().evaluate(
                importedProcess.getProcessAsDoc());

        attributes.forEach(x -> x.setValue(importedProcess.getGeneratedPrefix()+"_"+x.getValue()));
        elements.forEach(x -> x.setText(importedProcess.getGeneratedPrefix()+"_"+x.getText()));
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
     * adds the childs of importDefinitionsNode to the definitionsNode of the
     * given headFileDocument
     *
     * @param importedProcess
     *            the definitionsNode of the document, which should be added to
     *            the headFileDocument
     * @param headDocument
     *            the document, where the nodes should be added
     *
     */
    private void addNodesToDocument(org.jdom2.Document importedProcess,
            org.jdom2.Document headDocument) {
        org.jdom2.Element definitionsHead = headDocument.getRootElement();

        org.jdom2.Element definitionsImported = importedProcess.getRootElement();

        for(org.jdom2.Element element : definitionsImported.getChildren()) {
            org.jdom2.Element clone = element.clone();
            definitionsHead.addContent(clone);
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
     * helper method to easily set up the namespace collecting for the renaming
     * of the ids
     */
    private org.jdom2.xpath.XPathExpression<Attribute> setupXPathNamespaceIdsForAttributes() {
        String expStr = "//bpmn:*/@sourceRef | //bpmn:*/@targetRef | "
                + "//bpmn:*/@calledElement | //bpmn:*/@processRef | "
                + "//bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef";
        return xPathFactory.compile(expStr, Filters.attribute(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE));
    }

    /**
     * helper method to easily set up the namespace collecting for the renaming
     * of the ids
     */
    private org.jdom2.xpath.XPathExpression<org.jdom2.Element> setupXPathNamespaceIdsForElements() {
        String expStr = "//bpmn:*/eventDefinitionRef";
        return xPathFactory.compile(expStr, Filters.element(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE));
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

    /**
     * helper method to easily set up the id collecting for the renaming of the
     * ids
     */
    private org.jdom2.xpath.XPathExpression<Attribute> setupXPathReplaceIdsForAttributes() {
        String expStr = "//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | "
                + "//bpmn:*/@processRef | //bpmn:*/@dataStoreRef | "
                + "//bpmn:*/@categoryValueRef";

        return xPathFactory.compile(expStr, Filters.attribute(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE));
    }

    private org.jdom2.xpath.XPathExpression<org.jdom2.Element> setupXPathReplaceIdsForElements() {
        String expStr = "//bpmn:*/eventDefinitionRef | "
                + "//bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | "
                + "//bpmn:dataOutputRefs";

        return xPathFactory.compile(expStr, Filters.element(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE));
    }

}
