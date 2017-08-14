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

public class EXT106RemoveInvalidTypeFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT106RemoveInvalidTypeFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.106";
    private static final FixingStrategy SUPPORTED_STRATEGY = FixingStrategy.REMOVE;

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
        return "Removes the invalid cancel event definition - resulting in a 'None EndEvent'";
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
        elementOptional.get().removeChild("cancelEventDefinition", Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE));

        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
