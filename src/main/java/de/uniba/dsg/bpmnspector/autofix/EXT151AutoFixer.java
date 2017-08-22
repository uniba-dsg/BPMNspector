package de.uniba.dsg.bpmnspector.autofix;

import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class EXT151AutoFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT151AutoFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.151";
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
        return "Connects all elements not having an outgoing SequenceFlow to a newly created EndEvent";
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

        Element unconnectedElem = elementOptional.get();

        // getParent which should be process/subProcess
        Element parentProcessElement = unconnectedElem.getParentElement();

        // add EndEvent to parent
        Element newEnd = new Element("endEvent", ConstantHelper.BPMN_NAMESPACE);
        newEnd.setAttribute("id", bpmnXPathHelper.createRandomUniqueId());
        newEnd.setAttribute("name", "Auto-created EndEvent");
        parentProcessElement.addContent(newEnd);

        // connect unconnected Element to newEnd
        bpmnXPathHelper.createAndAddSequenceFlow(parentProcessElement, unconnectedElem, newEnd);

        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
