package de.uniba.dsg.ppn.ba.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
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
    private final Logger logger;

    {
        documentBuilder = SetupHelper.setupDocumentBuilder();
        xpath = SetupHelper.setupXPath();
        logger = (Logger) LoggerFactory.getLogger(getClass().getSimpleName());
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
        List<ImportedFile> importedFiles = selectImportedFiles(
                headFileDocument, folder, namespaceTable.size(), true);
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

        for (int i = 0; i < importedFiles.size(); i++) {
            if (importedFiles.get(i).getFile().exists()) {
                try {
                    Document importedDocument = documentBuilder
                            .parse(importedFiles.get(i).getFile());

                    Element importDefinitionsNode = importedDocument
                            .getDocumentElement();
                    removeBPMNDINode(importedDocument);

                    boolean exists = false;
                    for (String[] s : namespaceTable) {
                        if (s[1].equals(importedFiles.get(i).getNamespace())) {
                            exists = true;
                        }
                    }
                    logger.debug("namespace of file read: {}", importedFiles
                            .get(i).getNamespace());
                    if (!exists) {
                        namespaceTable.add(new String[] {
                                importedFiles.get(i).getPrefix(),
                                importedFiles.get(i).getNamespace() });
                    }
                    XPathExpression xPathReplaceIds = xpath
                            .compile("//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@processRef | //bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/eventDefinitionRef | //bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | //bpmn:dataOutputRefs");
                    renameIds(xPathReplaceIds, importedDocument, importedFiles
                            .get(i).getPrefix());

                    preProcess(importedDocument, folder, namespaceTable);

                    logger.info("integration of document will be done now");

                    addNodesToDocument(importDefinitionsNode, headFileDocument);
                } catch (SAXException | IOException e) {
                    logger.debug(
                            "imported file {} couldn't be read. Cause: {}",
                            importedFiles.get(i).getFile().getName(), e);
                    logger.error("imported file {} couldn't be read.",
                            importedFiles.get(i).getFile().getName());
                }
            }
        }

        return new PreProcessResult(headFileDocument, namespaceTable);
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
     * @param onlyBpmnFiles
     *            if true, just imports with the import type of the bpmn
     *            namespace are returned
     * @return a list of importedFile including all bpmn imports with the
     *         absolute path, the new namespace prefix and the namespace
     */
    public List<ImportedFile> selectImportedFiles(Document document,
            File folder, int size, boolean onlyBpmnFiles) {
        NodeList importedFilesList = document.getElementsByTagNameNS(
                ConstantHelper.BPMNNAMESPACE, "import");
        List<ImportedFile> importedFiles = new ArrayList<>();

        for (int i = 0; i < importedFilesList.getLength(); i++) {
            Node importedFileNode = importedFilesList.item(i);
            String importType = importedFileNode.getAttributes()
                    .getNamedItem("importType").getTextContent();
            if (!onlyBpmnFiles
                    || "http://www.omg.org/spec/BPMN/20100524/MODEL"
                            .equals(importType)) {
                File file = new File(importedFileNode.getAttributes()
                        .getNamedItem("location").getTextContent());
                if (!file.isAbsolute()) {
                    file = new File(folder.getPath()
                            + File.separator
                            + importedFileNode.getAttributes()
                                    .getNamedItem("location").getTextContent());
                }
                String prefix = ("ns" + (i + size));
                String namespace = importedFileNode.getAttributes()
                        .getNamedItem("namespace").getTextContent();
                importedFiles.add(new ImportedFile(file, prefix, namespace,
                        importType));
            }
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
     * removes all BPMNDiagram Nodes from the given file
     *
     * @param headFileDocument
     *            the document, where the BPMNDiagram Nodes should be deleted
     *            from
     */
    public void removeBPMNDINode(Document headFileDocument) {
        Element definitionsNode = headFileDocument.getDocumentElement();
        NodeList bpmnDiagramNode = headFileDocument.getElementsByTagNameNS(
                ConstantHelper.BPMNDINAMESPACE, "BPMNDiagram");
        if (bpmnDiagramNode.getLength() > 0) {
            definitionsNode.removeChild(bpmnDiagramNode.item(0));
        }
    }
}
