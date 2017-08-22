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

public class EXT150AutoFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT150AutoFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.150";
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
        return "Connects all elements not having an incoming SequenceFlow to the StartEvents of the Process (via a ParallelGateway)";
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

        Element unconnectedElement = elementOptional.get();

        // getParent which should be process/subProcess
        Element parentProcessElement = unconnectedElement.getParentElement();

        // find all StartEvents in parent
        List<Element> startEvents = parentProcessElement.getChildren().stream()
                .filter(e -> e.getName().equals("startEvent"))
                .collect(Collectors.toList());

        for (Element startEvent : startEvents) {
            List<String> outgoingSeqFlowIds = startEvent
                    .getChildren("outgoing", Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE))
                    .stream().map(Element::getText).collect(Collectors.toList());

            // Remove old outgoing elements
            startEvent.removeChildren("outgoing", Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE));

            // add parallel gateway to process
            Element parallelGateway = new Element("parallelGateway", ConstantHelper.BPMN_NAMESPACE);
            parallelGateway.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
            parallelGateway.setAttribute("gatewayDirection", "Diverging");
            parallelGateway.setAttribute("name", "Auto-created Parallel Gateway");
            parentProcessElement.addContent(parallelGateway);

            // add connection from start to gateway
            bpmnXPathHelper.createAndAddSequenceFlow(parentProcessElement, startEvent, parallelGateway);

            // add connection from gateway to unconnectedElem
            bpmnXPathHelper.createAndAddSequenceFlow(parentProcessElement, parallelGateway, unconnectedElement);

            // replace existing connections
            for (String seqFlowId : outgoingSeqFlowIds) {
                Element oldSeqFlow = parentProcessElement.getChildren("sequenceFlow", Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE)).stream()
                        .filter(e -> seqFlowId.equals(e.getAttributeValue("id")))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("ID '"+seqFlowId+"' does not exist."));

                // Reconnect oldSeqFlow to new gateway
                oldSeqFlow.setAttribute("sourceRef", parallelGateway.getAttributeValue("id"));
                bpmnXPathHelper.insertOutgoingElementToFlowNode(parallelGateway, seqFlowId);
            }
        }

        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
