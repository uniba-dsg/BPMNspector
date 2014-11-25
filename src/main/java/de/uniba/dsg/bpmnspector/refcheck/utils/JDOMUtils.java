package de.uniba.dsg.bpmnspector.refcheck.utils;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDOMUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDOMUtils.class.getSimpleName());

    /**
     * This method puts all elements with an id found in the given document into
     * a hash map. The key is the id, the value the element.
     *
     * @param document
     *            the document to get the elements from
     * @return the hash map with elements reachable through their id
     */
    public static Map<String, Element> getAllElements(Document document) {
        Map<String, Element> elements = new HashMap<>();
        Element rootNode = document.getRootElement();
        Filter<Element> filter = Filters.element();
        IteratorIterable<Element> list = rootNode.getDescendants(filter);

        while (list.hasNext()) {
            Element element = list.next();
            Attribute id = element.getAttribute("id");
            // put the element if it has an id
            if (id != null) {
                String id_value = id.getValue();
                if (id_value != null && !id_value.equals("")) {
                    elements.put(id_value, element);
                }
            }
        }

        return elements;
    }

    /**
     * Creates a HashMap which uses the namespace-URI as an ID and another
     * HashMap as value. The inner HashMap contains all Elements accessible via
     * the ID as key {@see getAllElements()}
     *
     * @param bpmnFiles
     *            the files to be analyzed
     * @return the grouped elements
     */
    public static Map<String, Map<String, Element>> getAllElementsGroupedByNamespace(
            List<Document> bpmnFiles) {
        Map<String, Map<String, Element>> groupedElements = new HashMap<>();

        for (Document doc : bpmnFiles) {
            String targetNamespace = doc.getRootElement().getAttributeValue(
                    "targetNamespace");
            Map<String, Element> docElements = getAllElements(doc);

            if (groupedElements.containsKey(targetNamespace)) {
                Map<String, Element> previousElems = groupedElements
                        .get(targetNamespace);
                previousElems.putAll(docElements);
            } else {
                groupedElements.put(targetNamespace, docElements);
            }
        }

        return groupedElements;
    }

    /**
     * Gets the baseURI String from the given element
     * (i.e., determines the location of the file containing the element)
     *
     * @param element
     *            the element
     * @return the URI string - null if the base URI cannot be restored
     */
    public static String getUriFromElement(Element element) {
        try {
            return element.getXMLBaseURI().toString();

        } catch (URISyntaxException e) {
            LOGGER.error("Base URI of current element " + element.getName()
                    + " could not be restored.");
            return null;
        }
    }
}
