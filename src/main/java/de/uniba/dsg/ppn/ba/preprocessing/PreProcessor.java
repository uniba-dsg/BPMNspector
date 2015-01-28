package de.uniba.dsg.ppn.ba.preprocessing;

import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Does the preprocessing step for creating only one document contenting
 * everything and a namespace table
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class PreProcessor {

    private static final Logger LOGGER;

    private final XPathFactory xPathFactory = XPathFactory.instance();

    static {
        LOGGER = LoggerFactory.getLogger(PreProcessor.class.getSimpleName());
    }

    /**
     *
     * Performs the pre-processing step for creating one document including the
     * content of all imported BPMN processes of the document
     *
     * @param process
     *          the BPMNProcess which should be used as starting point
     * @return  a cloned org.jdom2.Document representation of the BPMN process
     *          including all imported nodes
     */
    public Document preProcess(BPMNProcess process) {
        LOGGER.info("Starting to preprocess file: " + process.getBaseURI());

        Document cloneOfDoc = process.getProcessAsDoc().clone();
        // get all nodes which potentially refer to other files
        List<Attribute> refAttributes = setupXPathNamespaceIdsForAttributes().evaluate(
                cloneOfDoc);
        List<Element> refElements = setupXPathNamespaceIdsForElements().evaluate(
                cloneOfDoc);

        // for each attribute: rename IDs which can be used globally
        refAttributes.stream()
                .filter(attribute -> attribute.getValue().contains(":"))
                .forEach(attribute -> renameGlobalIds(process, attribute));

        // for each element: rename IDs which can be used globally
        refElements.stream().filter(element -> element.getText().contains(":"))
                .forEach(element -> renameGlobalIds(process, element));

        // for each import: perform further processing
        for(BPMNProcess imported : process.getChildren()){
            // rename IDs in imported file
            renameIds(imported);

            LOGGER.debug("Checking imported importedProcess for further imports.");
            Document result = preProcess(imported);

            LOGGER.debug("integration of document will be done now");

            addNodesToDocument(result, cloneOfDoc);
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
     * @param process
     *            the document which should be validated
     * @param attribute
     *            the attribute with the ID to be renamed
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

    /**
     * helper method to rename all global ids in the bpmn files to be able to
     * merge them into one file and to detect later the errors in the right
     * files. the colon is replaced by an underscore because ids in the one
     * merged file can't contain colons
     *
     * @param process
     *            the document which should be validated
     * @param element
     *            the element with the ID to be renamed
     */
    private void renameGlobalIds(BPMNProcess process, Element element) {
        String prefix = element.getText().substring(0,
                element.getText().indexOf(":"));
        String newPrefix = getNewPrefixByOldPrefix(process, prefix);
        LOGGER.debug("new prefix '{}' for ID {}", newPrefix,
                element.getText());
        element.setText(element.getText().replace(prefix + ":",
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
     * adds to all nodes new and unique prefixes in the given document for the
     * validation process and violation searching
     *
     * @param importedProcess the process in which the ids should be changed
     */
    private void renameIds(BPMNProcess importedProcess) {

        List<Attribute> attributes = setupXPathReplaceIdsForAttributes().evaluate(importedProcess.getProcessAsDoc());
        List<Element> elements = setupXPathReplaceIdsForElements().evaluate(
                importedProcess.getProcessAsDoc());

        attributes.forEach(x -> x.setValue(importedProcess.getGeneratedPrefix()+"_"+x.getValue()));
        elements.forEach(x -> x.setText(importedProcess.getGeneratedPrefix()+"_"+x.getText()));
    }

    /**
     * adds the children of importDefinitionsNode to the definitionsNode of the
     * given headFileDocument
     *
     * @param importedProcess
     *            the definitionsNode of the document, which should be added to
     *            the headFileDocument
     * @param headDocument
     *            the document, where the nodes should be added
     *
     */
    private void addNodesToDocument(Document importedProcess,
            Document headDocument) {
        Element definitionsHead = headDocument.getRootElement();

        Element definitionsImported = importedProcess.getRootElement();

        for(Element element : definitionsImported.getChildren()) {
            Element clone = element.clone();
            definitionsHead.addContent(clone);
        }
    }

    /**
     * helper method to easily set up the namespace collecting for the renaming
     * of the ids
     */
    private XPathExpression<Attribute> setupXPathNamespaceIdsForAttributes() {
        String expStr = "//bpmn:*/@sourceRef | //bpmn:*/@targetRef | "
                + "//bpmn:*/@calledElement | //bpmn:*/@processRef | "
                + "//bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef";
        return xPathFactory.compile(expStr, Filters.attribute(), null,
                getBpmnNamespace());
    }

    /**
     * helper method to easily set up the namespace collecting for the renaming
     * of the ids
     */
    private XPathExpression<Element> setupXPathNamespaceIdsForElements() {
        String expStr = "//bpmn:*/eventDefinitionRef";
        return xPathFactory.compile(expStr, Filters.element(), null,
                getBpmnNamespace());
    }
    

    /**
     * helper method to easily set up the id collecting for the renaming of the
     * ids
     */
    private XPathExpression<Attribute> setupXPathReplaceIdsForAttributes() {
        String expStr = "//bpmn:*/@id | //bpmn:*/@sourceRef | //bpmn:*/@targetRef | "
                + "//bpmn:*/@processRef | //bpmn:*/@dataStoreRef | "
                + "//bpmn:*/@categoryValueRef";

        return xPathFactory.compile(expStr, Filters.attribute(), null,
                getBpmnNamespace());
    }

    private XPathExpression<Element> setupXPathReplaceIdsForElements() {
        String expStr = "//bpmn:*/eventDefinitionRef | "
                + "//bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | "
                + "//bpmn:dataOutputRefs";

        return xPathFactory.compile(expStr, Filters.element(), null,
                getBpmnNamespace());
    }

    private Namespace getBpmnNamespace() {
        return Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE);
    }

}
