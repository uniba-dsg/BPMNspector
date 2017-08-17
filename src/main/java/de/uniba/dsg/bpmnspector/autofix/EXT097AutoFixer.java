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

public class EXT097AutoFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT097AutoFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.097";
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
    public String getDescription() {
        return "creates a new StartEvent which is connected via a Parallel Gateway to all unconnected Element";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public boolean fixSingleViolation(Document processAsDoc, String xPath) {
        Optional<Element> elementOptional = bpmnXPathHelper.findSingleElementForXPath(processAsDoc, xPath);

        if (!elementOptional.isPresent()) {
            return false;
        }

        // getParent which should be process/subProcess
        Element parentProcessElement = elementOptional.get().getParentElement();

        // add EndEvent to parent
        Element newStart = new Element("startEvent", ConstantHelper.BPMN_NAMESPACE);
        newStart.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
        newStart.setAttribute("name", "Auto-created StartEvent");
        parentProcessElement.addContent(newStart);

        // add new ParallelGateway to parent
        Element parallelGateway = new Element("parallelGateway", ConstantHelper.BPMN_NAMESPACE);
        parallelGateway.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
        parallelGateway.setAttribute("name", "Auto-created StartEvent");
        parentProcessElement.addContent(parallelGateway);

        // connect new Start to parallel Gateway
        Element startParallelSeqFlow = new Element("sequenceFlow", ConstantHelper.BPMN_NAMESPACE);
        startParallelSeqFlow.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
        startParallelSeqFlow.setAttribute("sourceRef", newStart.getAttributeValue("id"));
        startParallelSeqFlow.setAttribute("targetRef", parallelGateway.getAttributeValue("id"));
        parentProcessElement.addContent(startParallelSeqFlow);

        bpmnXPathHelper.insertOutgoingElementToFlowNode(newStart, startParallelSeqFlow.getAttributeValue("id"));
        bpmnXPathHelper.insertIncomingElementToFlowNode(parallelGateway, startParallelSeqFlow.getAttributeValue("id"));


        // find all unconnected Elems in parent i.e., they have no incoming element
        List<Element> childrenWithNeededIncomingSeqFlow = bpmnXPathHelper.determineFlowNodesWithNeededIncomingFlow(parentProcessElement);
        List<Element> unconnectedElems = childrenWithNeededIncomingSeqFlow.stream()
                .filter(e -> e.getChild("incoming", Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE)) == null)
                .collect(Collectors.toList());

        for (Element unconnected : unconnectedElems) {
            Element newSeqFlow = new Element("sequenceFlow", ConstantHelper.BPMN_NAMESPACE);
            newSeqFlow.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
            newSeqFlow.setAttribute("sourceRef", parallelGateway.getAttributeValue("id"));
            newSeqFlow.setAttribute("targetRef", unconnected.getAttributeValue("id"));
            parentProcessElement.addContent(newSeqFlow);

            bpmnXPathHelper.insertOutgoingElementToFlowNode(parallelGateway, newSeqFlow.getAttributeValue("id"));

            bpmnXPathHelper.insertIncomingElementToFlowNode(unconnected, newSeqFlow.getAttributeValue("id"));
        }

        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
