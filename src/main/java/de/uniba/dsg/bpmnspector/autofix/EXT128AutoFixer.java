package de.uniba.dsg.bpmnspector.autofix;

import api.Violation;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EXT128AutoFixer extends AbstractFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT128AutoFixer.class.getSimpleName());
    public static final String CONSTRAINT_ID_EXT128 = "EXT.128";
    private static final FixingStrategy EXT_128_STRATEGY = FixingStrategy.AUTO_FIX;

    private final XPathFactory xPathFactory = XPathFactory.instance();

    public EXT128AutoFixer() {
        super(CONSTRAINT_ID_EXT128, EXT_128_STRATEGY);
    }

    @Override
    public FixReport fixIssues(Document docToFix, List<Violation> violationList) {
        if (violationList.isEmpty()) {
            return FixReport.createUnchangedFixReport();
        }

        FixReport report = new FixReport();
        for (Violation singleViolation : violationList) {
            if(!CONSTRAINT_ID_EXT128.equals(singleViolation.getConstraint())) {
                LOGGER.warn("Invalid violation for EXT.128 fixer, constraint ID is: "+singleViolation.getConstraint());
            }
            if(!singleViolation.getLocation().getXpath().isPresent()) {
                LOGGER.warn("Could not fix EXT.128 violation "+singleViolation+": no XPath present.");
                continue;
            }
            if(fixSingleExt128Violation(docToFix, singleViolation.getLocation().getXpath().get())) {
                report.addFixedViolation(singleViolation);
            }
        }
        return report;
    }

    private boolean fixSingleExt128Violation(Document processAsDoc, String xPath) {
        XPathExpression<Element> expression = xPathFactory.compile(xPath, Filters.element(), null,
                Namespace.getNamespace("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
        List<Element> foundElements = expression.evaluate(processAsDoc);
        if(foundElements.isEmpty()) {
            LOGGER.warn("Could not fix EXT.128 violation: affected MessageEventDefiniton was not found");
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
}
