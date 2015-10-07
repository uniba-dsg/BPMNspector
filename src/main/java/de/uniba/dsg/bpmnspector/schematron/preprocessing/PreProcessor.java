package de.uniba.dsg.bpmnspector.schematron.preprocessing;

import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import de.uniba.dsg.bpmnspector.refcheck.utils.JDOMUtils;
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

    private static final String ALL_QNAME_ATTRIBUTES =  "//bpmn:*/@sourceRef | //bpmn:*/@targetRef | "
            + "//bpmn:*/@calledElement | //bpmn:*/@processRef | "
            + "//bpmn:*/@dataStoreRef | //bpmn:*/@categoryValueRef | //bpmn:*/@messageRef | "
            + "//bpmn:*/@correlationKeyRef | //bpmn:*/@correlationPropertyRef | //bpmn:*/@structureRef |"
            + "//bpmn:*/@evaluatesToTypeRef | //bpmn:*/@itemRef | //bpmn:*/@type | "
            + "//bpmn:*/@definitionalCollaborationRef | //bpmn:*/@parameterRef | //bpmn:*/@operationRef |"
            + "//bpmn:*/@calledElement | //bpmn:*/@itemSubjectRef | //bpmn:*/@dataStoreRef | "
            + "//bpmn:*/@attachedToRef | //bpmn:*/@activityRef | //bpmn:*/@errorRef | //bpmn:*/@escalationRef | "
            + "//bpmn:*/@source | //bpmn:*/@target | //bpmn:*/@signalRef | //bpmn:*/@partitionElementRef";

    private static final String ALL_IDREF_ATTRIBUTES = "//bpmn:*/@sourceRef | //bpmn:*/@targetRef | //bpmn:*/@default |"
            + "//bpmn:*/@inputDataRef | //bpmn:*/@outputDataRef | //bpmn:*/@dataObjectRef";

    private static final String ALL_QNAME_ELEMENTS = "//bpmn:eventDefinitionRef | "
            + "//bpmn:incoming | //bpmn:outgoing | //bpmn:dataInputRefs | "
            + "//bpmn:dataOutputRefs | //bpmn:correlationPropertyRef | //bpmn:categoryValueRef |"
            + "//bpmn:inMessageRef | //bpmn:outMessageRef | //bpmn:errorRef | //bpmn:choreographyRef |"
            + "//bpmn:interfaceRef | //bpmn:endPointRef | //bpmn:participantRef | //bpmn:innerParticipantRef |"
            + "//bpmn:outerParticipantRef | //bpmn:supports | //bpmn:resourceRef | //bpmn:supportedInterfaceRefs |"
            + "//bpmn:loopDataInputRef | //bpmn:loopDataOutputRef | //bpmn:oneBehaviorEventRef |"
            + "//bpmn:noneBehaviorEventRef";

    private static final String ALL_IDREF_ELEMENTS = "//bpmn:dataInputRefs | "
            + "//bpmn:optionalInputRefs | //bpmn:whileExecutingInputRefs | //bpmn:outputSetRefs | "
                    + "//bpmn:dataOutputRefs | //bpmn:optionalOutputRefs | //bpmn:whileExecutingOutputRefs | "
                    + "//bpmn:inputSetRefs | //bpmn:sourceRef | //bpmn:targetRef | //bpmn:flowNodeRef";

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

        // Preprocessing can be skipped if no files are imported and there is no Prefix used for the targetNamespace
        if(process.getChildren().isEmpty() && !JDOMUtils.getUsedPrefixForTargetNamespace(cloneOfDoc).isPresent()) {
            LOGGER.info("Skipping preprocessing.");
            return cloneOfDoc;
        }

        // get all nodes which potentially refer to other files
        List<Attribute> refAttributes = createXPathExpressionForAllQNameAttributes().evaluate(cloneOfDoc);
        List<Element> refElements = createXPathExpressionForAllQNameElements().evaluate(cloneOfDoc);

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
        String newPrefix;
        // special treatment if a prefix is used for the local targetNamespace
        if(prefix.equals(JDOMUtils.getUsedPrefixForTargetNamespace(process.getProcessAsDoc()).orElse(""))) {
            // replaces usage of 'tns:ID' with 'ID'
            newPrefix = "";
        } else {
            newPrefix = getNewPrefixByOldPrefix(process, prefix)+"_";
        }
        String newId = attribute.getValue().replace(prefix + ":", newPrefix);
        LOGGER.debug("new prefix '{}' for ID '{}' - new ID: {}", newPrefix, attribute.getValue(), newId);
        attribute.setValue(newId);
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
        String newPrefix;
        // special treatment if a prefix is used for the local targetNamespace
        if(prefix.equals(JDOMUtils.getUsedPrefixForTargetNamespace(process.getProcessAsDoc()).orElse(""))) {
            // replaces usage of 'tns:ID' with 'ID'
            newPrefix = "";
        } else {
            newPrefix = getNewPrefixByOldPrefix(process, prefix)+"_";
        }
        String newId = element.getText().replace(prefix + ":", newPrefix);
        LOGGER.debug("new prefix '{}' for ID '{}' - new ID: {}", newPrefix, element.getText(), newId);
        element.setText(newId);

    }

    private String getNewPrefixByOldPrefix(BPMNProcess process,
            String oldPrefix) {
        Namespace namespace = process.getProcessAsDoc().getRootElement().getNamespace(
                oldPrefix);
        String nspURI = "";
        if(namespace!=null) {
            nspURI = namespace.getURI();
        }
        String newPrefix = "";
        for (BPMNProcess imported : process.getChildren()) {
            if (nspURI.equals(imported.getNamespace())) {
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

        List<Attribute> attributes = createXPathExpressionForAllIDsAndReferenceAttributes().evaluate(importedProcess.getProcessAsDoc());
        List<Element> elements = createXPathExpressionForAllReferenceElements().evaluate(importedProcess.getProcessAsDoc());

        attributes.stream().filter(x -> !x.getValue().contains(":"))
                .forEach(x -> {
                    LOGGER.debug("Updating attribute ID '{}' new value: {}", x.getValue(), importedProcess.getGeneratedPrefix()+"_"+x.getValue());
                    x.setValue(importedProcess.getGeneratedPrefix()+"_"+x.getValue());
                });
        elements.stream().filter(x-> !x.getText().contains(":"))
                .forEach(x -> {
                    LOGGER.debug("Updating element {} ID '{}' new value: {}", x.getName(), x.getText(), importedProcess.getGeneratedPrefix()+"_"+x.getText());
                    x.setText(importedProcess.getGeneratedPrefix()+"_"+x.getText());
                });
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
     * Creates an XPathExpression filtering all Attributes which are used to reference another BPMN element
     */
    private XPathExpression<Attribute> createXPathExpressionForAllQNameAttributes() {

        return xPathFactory.compile(ALL_QNAME_ATTRIBUTES, Filters.attribute(), null,
                getBpmnNamespace());
    }

    /**
     * Creates an XPathExpression filtering all Elements which are used to reference another BPMN element
     */
    private XPathExpression<Element> createXPathExpressionForAllQNameElements() {

        return xPathFactory.compile(ALL_QNAME_ELEMENTS, Filters.element(), null,
                getBpmnNamespace());
    }
    

    /**
     * Creates an XPathExpression filtering all Attributes which define and use IDs
     * (regardless of the used type (ID, IDREF or QName))
     */
    private XPathExpression<Attribute> createXPathExpressionForAllIDsAndReferenceAttributes() {

        String expStr = "//bpmn:*/@id | " + ALL_IDREF_ATTRIBUTES +" | "+ ALL_QNAME_ATTRIBUTES;

        return xPathFactory.compile(expStr, Filters.attribute(), null,
                getBpmnNamespace());
    }

    /**
     * Creates an XPathExpression filtering all Elements which use IDs
     * (regardless of the used type (ID, IDREF or QName))
     */
    private XPathExpression<Element> createXPathExpressionForAllReferenceElements() {

        String expStr = ALL_IDREF_ELEMENTS + " | " + ALL_QNAME_ELEMENTS;

        return xPathFactory.compile(expStr, Filters.element(), null,
                getBpmnNamespace());
    }

    private Namespace getBpmnNamespace() {
        return Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE);
    }

}
