package de.uniba.dsg.bpmnspector.autofix;

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
 * Implements the first potential fix for EXT.012 violations, i.e., marking the process as not executable
 */
public class EXT012FirstOptionNotExecutableFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT012FirstOptionNotExecutableFixer.class.getSimpleName());

    private static final String CONSTRAINT_ID = "EXT.012";
    private static final FixingStrategy SUPPORTED_STRATEGY = FixingStrategy.FIRST_OPTION;

    private final XPathFactory xPathFactory = XPathFactory.instance();

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
        XPathExpression<Element> expression = xPathFactory.compile(xPath, Filters.element(), null,
                Namespace.getNamespace("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
        List<Element> foundElements = expression.evaluate(docToFix);
        if(foundElements.isEmpty()) {
            LOGGER.warn("Could not fix EXT.012 violation: affected Element was not found");
            return false;
        }
        Element elem = foundElements.get(0);
        while (elem.getParentElement()!=null) {
            if("process".equals(elem.getParentElement().getName())) {
                Element processElem = elem.getParentElement();
                if (processElem.getAttribute("isExecutable")!=null) {
                    processElem.getAttribute("isExecutable").setValue("false");
                    LOGGER.info("Set isExecutable attribute of element "+processElem+" to false");
                    return true;
                }
            } else {
                elem = elem.getParentElement();
            }
        }
        return false;
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }
}
