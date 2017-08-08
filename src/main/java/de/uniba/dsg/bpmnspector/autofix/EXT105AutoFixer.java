package de.uniba.dsg.bpmnspector.autofix;

import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EXT105AutoFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT105AutoFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.105";
    private static final FixingStrategy SUPPORTED_STRATEGY = FixingStrategy.AUTO_FIX;

    private final BpmnXPathHelper bpmnXPathHelper = new BpmnXPathHelper();

    @Override
    public String getConstraintId() {
        return CONSTRAINT_ID;
    }

    @Override
    public FixingStrategy getSupportedStrategy() {
        return SUPPORTED_STRATEGY;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public boolean fixSingleViolation(Document processAsDoc, String xPath) {
        Optional<Element> elementOptional = bpmnXPathHelper.findSingleElementForXPath(processAsDoc, xPath);

        if(!elementOptional.isPresent()) {
            return false;
        }

        // getParent which should be process/subProcess
        Element parentProcessElement = elementOptional.get().getParentElement();

        // add EndEvent to parent
        Element newEnd = new Element("endEvent", ConstantHelper.BPMN_NAMESPACE);
        newEnd.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
        newEnd.setAttribute("name", "Auto-created EndEvent");
        parentProcessElement.addContent(newEnd);

        // find all unconnected Elems in parent i.e., they have no outgoing element
        List<Element> childrenWithNeededOutgoingSeqFlow = bpmnXPathHelper.determineFlowNodesWithNeededOutgoingFlow(parentProcessElement);
        List<Element> unconnectedElems = childrenWithNeededOutgoingSeqFlow.stream()
                .filter(e -> e.getChild("outgoing", Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE)) == null)
                .collect(Collectors.toList());

        for(Element unconnected : unconnectedElems) {
            Element newSeqFlow = new Element("sequenceFlow", ConstantHelper.BPMN_NAMESPACE);
            newSeqFlow.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
            newSeqFlow.setAttribute("sourceRef", unconnected.getAttributeValue("id"));
            newSeqFlow.setAttribute("targetRef", newEnd.getAttributeValue("id"));
            parentProcessElement.addContent(newSeqFlow);

            bpmnXPathHelper.insertOutgoingElementToFlowNode(unconnected, newSeqFlow.getAttributeValue("id"));

            bpmnXPathHelper.insertIncomingElementToFlowNode(newEnd, newSeqFlow.getAttributeValue("id"));
        }

        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
