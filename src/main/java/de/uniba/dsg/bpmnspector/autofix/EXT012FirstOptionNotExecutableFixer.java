package de.uniba.dsg.bpmnspector.autofix;

import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implements the first potential fix for EXT.012 violations, i.e., marking the process as not executable
 */
public class EXT012FirstOptionNotExecutableFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT012FirstOptionNotExecutableFixer.class.getSimpleName());

    private static final String CONSTRAINT_ID = "EXT.012";
    private static final FixingStrategy SUPPORTED_STRATEGY = FixingStrategy.FIRST_OPTION;

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
    public boolean fixSingleViolation(Document docToFix, String xPath) {

        Optional<Element> elementOptional = bpmnXPathHelper.findSingleElementForXPath(docToFix, xPath);
        if (!elementOptional.isPresent()) {
            LOGGER.warn("Could not fix EXT.012 violation: affected Element was not found");
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
