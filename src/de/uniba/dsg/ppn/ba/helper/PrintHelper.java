package de.uniba.dsg.ppn.ba.helper;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.uniba.dsg.bpmn.Violation;

public class PrintHelper {

	public static void printDocument(Document doc) throws IOException,
			TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc), new StreamResult(System.out));
	}

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
