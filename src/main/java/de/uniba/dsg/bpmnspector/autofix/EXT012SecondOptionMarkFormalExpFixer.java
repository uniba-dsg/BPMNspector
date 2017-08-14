package de.uniba.dsg.bpmnspector.autofix;

import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implements the second potential fix for EXT.012 violations, i.e., marking the a Condition as a "FormalExpression"
 */
public class EXT012SecondOptionMarkFormalExpFixer implements ViolationFixer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EXT012SecondOptionMarkFormalExpFixer.class.getSimpleName());

    private static final String CONSTRAINT_ID = "EXT.012";
    private static final FixingStrategy SUPPORTED_STRATEGY = FixingStrategy.SECOND_OPTION;

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
        return "Marks the affected expression as a 'FormalExpression'";
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

        Element affectedElement = elementOptional.get();
        String elementName = affectedElement.getName();
        String usedBpmnNspPrefix = affectedElement.getNamespacePrefix();
        switch (elementName) {
            case "adHocSubProcess":
                addAttributeToChild(affectedElement, "completionCondition");
                break;
            case "assignment":
                addAttributeToChild(affectedElement, "from");
                addAttributeToChild(affectedElement, "to");
                break;
            case "complexGateway":
                addAttributeToChild(affectedElement, "activationCondition");
                break;
            case "multiInstanceLoopCharacteristics":
                addAttributeToMultiLoopCharacteristics(affectedElement);
                break;
            case "sequenceFlow":
                addAttributeToChild(affectedElement, "conditionExpression");
                break;
            case "standardLoopCharacteristics":
                addAttributeToChild(affectedElement, "loopCondition");
                break;
            case "timerEventDefinition":
                addAttributeToTimerEventDef(affectedElement);
                break;
        }

        // timerEventDefinition -> timeCycle/timeDate/timeDuration

        return true;
    }

    private void addAttributeToTimerEventDef(Element affectedElement) {
        List<Element> children = affectedElement.getChildren();
        for (Element child : children) {
            if ("timeCycle".equals(child.getName())) {
                addAttributeToChild(affectedElement, "timeCycle");
            } else if ("timeDate".equals(child.getName())) {
                addAttributeToChild(affectedElement, "timeDate");
            } else if ("timeDuration".equals(child.getName())) {
                addAttributeToChild(affectedElement, "timeDuration");
            }
        }
    }

    private void addAttributeToMultiLoopCharacteristics(Element affectedElement) {
        List<Element> children = affectedElement.getChildren();
        for (Element child : children) {
            if ("completionCondition".equals(child.getName())) {
                addAttributeToChild(affectedElement, "loopCardinality");
            }
            if ("loopCardinality".equals(child.getName())) {
                addAttributeToChild(affectedElement, "completionCondition");
            }

        }
    }

    public static FixerIdentifier getFixerIdentifier() {
        return new FixerIdentifier(CONSTRAINT_ID, SUPPORTED_STRATEGY);
    }

    private void addAttributeToChild(Element elementToUse, String nameOfChild) {
        Element childToUse = elementToUse.getChild(nameOfChild, Namespace.getNamespace(ConstantHelper.BPMN_NAMESPACE));
        if (childToUse != null) {
            childToUse.setAttribute("type",
                    childToUse.getNamespacePrefix() + "tFormalExpression",
                    getXSINamespace(childToUse));
        }
    }

    private Namespace getXSINamespace(Element element) {
        List<Namespace> allNamespaces = element.getAdditionalNamespaces();
        for (Namespace nsp : allNamespaces) {
            if (ConstantHelper.XSI_NAMESPACE.equals(nsp.getURI())) {
                return nsp;
            }
        }
        return Namespace.getNamespace("xsi", ConstantHelper.XSI_NAMESPACE);
    }
}
