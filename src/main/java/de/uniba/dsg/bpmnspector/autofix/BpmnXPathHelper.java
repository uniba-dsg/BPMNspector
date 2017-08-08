package de.uniba.dsg.bpmnspector.autofix;

import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class BpmnXPathHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmnXPathHelper.class.getSimpleName());

    private final XPathFactory xPathFactory = XPathFactory.instance();

    private final List<String> FLOW_NODES_LIST_IN_OUT = Arrays.asList(
            "exclusiveGateway", "inclusiveGateway", "parallelGateway", "complexGateway", "eventBasedGateway",
            "choreographyActivity", "choreographyTask", "subChoreography", "callChoreography",
            "task", "serviceTask", "sendTask", "receiveTask", "userTask", "manualTask", "scriptTask", "businessRuleTask", "subProcess", "adHocSubProcess", "transaction", "callActivity",
            "intermediateCatchEvent", "intermediateThrowEvent"
    );
    private final List<String> FLOW_NODES_OUTGOING_ONLY = Arrays.asList("startEvent", "boundaryEvent");
    private final List<String> FLOW_NODES_INCOMING_ONLY = Collections.singletonList("endEvent");


    public Optional<Element> findSingleElementForXPath(Document document, String xPathExp) {
        XPathExpression<Element> expression = xPathFactory.compile(xPathExp, Filters.element(), null,
                Namespace.getNamespace("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
        List<Element> foundElements = expression.evaluate(document);
        if (foundElements.isEmpty()) {
            LOGGER.warn("XPath expression '" + xPathExp + "' returned an empty list.");
            return Optional.empty();
        } else if (foundElements.size() > 1) {
            LOGGER.warn("XPath expression '" + xPathExp + "' returned more than one hit - return the first one.");
        }
        return Optional.of(foundElements.get(0));
    }

    public List<Element> findElementsForXPath(Document document, String xPathExp) {
        XPathExpression<Element> expression = xPathFactory.compile(xPathExp, Filters.element(), null,
                Namespace.getNamespace("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
        return expression.evaluate(document);
    }

    public Optional<Element> findParentOfElementByNameRecursively(Element element, String nameOfParent) {
        Objects.requireNonNull(element, "Given element must not be null");
        Objects.requireNonNull(nameOfParent, "Given name of parent Element must not be null");

        while (element.getParentElement() != null) {
            if (nameOfParent.equals(element.getParentElement().getName())) {
                return Optional.of(element.getParentElement());
            } else {
                element = element.getParentElement();
            }
        }

        return Optional.empty();
    }

    public String createRandomUniqueId() {
        return "id_"+UUID.randomUUID().toString();
    }

    public List<Element> determineFlowNodesWithNeededIncomingFlow(Element baseElem) {
        Objects.requireNonNull(baseElem);

        List<String> relevantElemNames = new ArrayList<>(FLOW_NODES_LIST_IN_OUT);
        relevantElemNames.addAll(FLOW_NODES_INCOMING_ONLY);

        return findAllElementsByNameList(baseElem, relevantElemNames);
    }

    public List<Element> determineFlowNodesWithNeededOutgoingFlow(Element baseElem) {
        Objects.requireNonNull(baseElem);

        List<String> relevantElemNames = new ArrayList<>(FLOW_NODES_LIST_IN_OUT);
        relevantElemNames.addAll(FLOW_NODES_OUTGOING_ONLY);

        return findAllElementsByNameList(baseElem, relevantElemNames);
    }

    private List<Element> findAllElementsByNameList(Element baseElem, List<String> names) {
        return baseElem.getChildren().stream().filter(e -> names.contains(e.getName())).collect(Collectors.toList());
    }

    public void insertOutgoingElementToFlowNode(Element flownode, String outgoingIDREF) {
        Element newSubElem = new Element("outgoing", ConstantHelper.BPMN_NAMESPACE);
        newSubElem.setText(outgoingIDREF);

        int index = determineIndexForElementInsertion(flownode);
        if (index > flownode.getChildren().size()) {
            flownode.addContent(newSubElem);
        } else {
            flownode.addContent(index, newSubElem);
        }
    }

    public void insertIncomingElementToFlowNode(Element flownode, String incomingIDREF) {
        Element newSubElem = new Element("incoming", ConstantHelper.BPMN_NAMESPACE);
        newSubElem.setText(incomingIDREF);

        int index = determineIndexForElementInsertion(flownode);
        if (index > flownode.getChildren().size()) {
            flownode.addContent(newSubElem);
        } else {
            flownode.addContent(index, newSubElem);
        }
    }

    private int determineIndexForElementInsertion(Element parent) {
        // Caution: Order in list is important: new Elem must be placed after last incoming element - if not present:
        // after last categoryValueRef, etc.
        List<String> allowedElementsBefore = Arrays.asList("incoming", "categoryValueRef", "monitoring", "auditing", "extensionElements", "documentation");

        List<Element> children = parent.getChildren();

        for (String str : allowedElementsBefore) {
            List<Element> relevantElements = children.stream().filter(c -> c.getName().equals(str)).collect(Collectors.toList());
            if (!relevantElements.isEmpty()) {
                // use last element in list to determine index
                return parent.indexOf(relevantElements.get(relevantElements.size() - 1)) + 1;
            }
        }
        //No Elem found - returning 0 - newElem can be placed at first place.
        return 0;
    }


}
