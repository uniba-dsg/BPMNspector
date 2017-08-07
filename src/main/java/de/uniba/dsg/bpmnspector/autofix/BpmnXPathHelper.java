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
import java.util.Objects;
import java.util.Optional;

public class BpmnXPathHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmnXPathHelper.class.getSimpleName());

    private final XPathFactory xPathFactory = XPathFactory.instance();

    public Optional<Element> findSingleElementForXPath(Document document, String xPathExp) {
        XPathExpression<Element> expression = xPathFactory.compile(xPathExp, Filters.element(), null,
                Namespace.getNamespace("bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
        List<Element> foundElements = expression.evaluate(document);
        if(foundElements.isEmpty()) {
            LOGGER.warn("XPath expression '"+xPathExp+"' returned an empty list.");
            return Optional.empty();
        } else if (foundElements.size() > 1) {
            LOGGER.warn("XPath expression '"+xPathExp+"' returned more than one hit - return the first one.");
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

        while (element.getParentElement()!=null) {
            if(nameOfParent.equals(element.getParentElement().getName())) {
                return Optional.of(element.getParentElement());
            } else {
                element = element.getParentElement();
            }
        }

        return Optional.empty();
    }

}
