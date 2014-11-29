package de.uniba.dsg.ppn.ba.helper;

import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.uniba.dsg.bpmnspector.common.Violation;

/**
 * PrintHelper for printing some objects to system.out
 *
 * @author Philipp Neugebauer
 * @version 1.0
 */
@SuppressWarnings("PMD.SystemPrintln")
public class PrintHelper {

    private static Transformer transformer;

    static {
        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        try {
            transformer = transformerFactory.newTransformer();
            transformer
                    .setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (TransformerConfigurationException e) {
            // ignore
        }
    }

    /**
     * prints the given document to system.out
     *
     * @param document
     *            the document, which should be printed
     */
    public static void printDocument(Document document) {
        try {
            transformer.transform(new DOMSource(document), new StreamResult(
                    System.out));
        } catch (TransformerException e) {
            System.err.println("printDocument failed cause of " + e);
        }
    }

    /**
     * prints the violations list in a nice and human-readable way to system.out
     *
     * @param violations
     *            the violations list, which should be printed
     */
    public static void printViolations(List<Violation> violations) {
        System.out.println("Violations count: " + violations.size());
        System.out.println("--------------------");
        for (Violation v : violations) {
            System.out.println("Line: " + v.getLine());
            System.out.println("FileName: " + v.getFileName());
            System.out.println("Message: " + v.getMessage());
            System.out.println("XPath: " + v.getxPath());
            System.out.println("Constraint: " + v.getConstraint());
            System.out.println("--------------------");
        }
    }

}
