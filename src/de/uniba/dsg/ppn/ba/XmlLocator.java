package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedElement;
import org.jdom2.located.LocatedJDOMFactory;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class XmlLocator {

	private SAXBuilder saxBuilder;
	private XPathFactory xPathFactory;

	public XmlLocator() {
		saxBuilder = new SAXBuilder();
		saxBuilder.setJDOMFactory(new LocatedJDOMFactory());
		xPathFactory = XPathFactory.instance();
	}

	public int findLine(File xmlFile, String xpathExpression)
			throws JDOMException, IOException {
		Document doc = saxBuilder.build(xmlFile);
		XPathExpression<Element> xpath = xPathFactory.compile(xpathExpression,
				Filters.element(), null, Namespace.getNamespace("bpmn",
						SchematronBPMNValidator.bpmnNamespace));

		List<Element> foundElements = xpath.evaluate(doc);

		if (foundElements.size() > 0) {
			return ((LocatedElement) foundElements.get(0)).getLine();
		}
		return -1;
	}
}
