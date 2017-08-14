package de.uniba.dsg.bpmnspector.autofix;

import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class EXT128AutoFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT128AutoFixer.class.getSimpleName());
    private static final String CONSTRAINT_ID = "EXT.128";
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
        return "sets the 'isExecutable' attribute of the process to false";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public boolean fixSingleViolation(Document processAsDoc, String xPath) {
        Optional<Element> elementOptional = bpmnXPathHelper.findSingleElementForXPath(processAsDoc, xPath);
        if (!elementOptional.isPresent()) {
            LOGGER.warn("Could not fix EXT.128 violation: affected MessageEventDefiniton was not found");
            return false;
        }
        Element elem = elementOptional.get();

        Optional<Element> optionalParent = bpmnXPathHelper.findParentOfElementByNameRecursively(elem, "process");

        if (!optionalParent.isPresent()) {
            return false;
        }
        Element processElem = optionalParent.get();
        processElem.setAttribute("isExecutable", "false");
        LOGGER.info("Set isExecutable attribute of element " + processElem + " to false");
        return true;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
