package de.uniba.dsg.ppn.ba.helper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * This class is a setup helper class for the document builder and xpath
 * handling
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class SetupHelper {

    /**
     * sets up the documentbuilder with the required namespace-awareness
     */
    public static DocumentBuilder setupDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // ignore
        }
        return documentBuilder;
    }

    /**
     * sets up the xpath with the custom namespace context of bpmn
     */
    public static XPath setupXPath() {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext(new BpmnNamespaceContext());
        return xpath;
    }

    /**
     * sets up the xpath expression to search for all ids in the bpmn namespace
     */
    public static XPathExpression setupXPathExpression() {
        XPath xpath = setupXPath();
        XPathExpression xPathExpression = null;
        try {
            xPathExpression = xpath.compile("//bpmn:*/@id");
        } catch (XPathExpressionException e) {
            // ignore
        }
        return xPathExpression;
    }
}
