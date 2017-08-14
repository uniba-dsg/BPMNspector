package de.uniba.dsg.bpmnspector.autofix;

import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EXT098RemoveInvalidTypeFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT098RemoveInvalidTypeFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.098";
    private static final FixingStrategy SUPPORTED_STRATEGY = FixingStrategy.REMOVE;

    private final BpmnXPathHelper bpmnXPathHelper = new BpmnXPathHelper();

    private final List<String> INVALID_START_EVENT_DEFINITIONS = Arrays.asList("cancelEventDefinition",
            "escalationEventDefinition", "errorEventDefinition", "compensateEventDefinition",
            "linkEventDefinition", "terminateEventDefinition");

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
        return "Removes the invalid EventDefinition - resulting in a 'None StartEvent'";
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

        Element affectedStartEvent = elementOptional.get();
        List<Element> elemsToRemove = affectedStartEvent.getChildren().stream()
                .filter(e -> INVALID_START_EVENT_DEFINITIONS.contains(e.getName()))
                .collect(Collectors.toList());

        elemsToRemove.forEach(e -> affectedStartEvent.removeChild(e.getName(), Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE)));

        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
