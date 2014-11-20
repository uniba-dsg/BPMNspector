package de.uniba.dsg.bpmnspector.refcheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class loads the references form the references.xml file. The XML file
 * needs to fit the references.xsd schema. The references will be structured by
 * the classes {@link BPMNElement} and {@link Reference}.
 * 
 * @author Andreas Vorndran
 * @version 1.0
 * @see BPMNElement
 * @see Reference
 * 
 */
public class ReferenceLoader {

	private final List<SAXParseException> XSDErrorList;
	private final Logger LOGGER;
	private final Properties language;

	/**
	 * Constructor
	 * 
	 * @param language
	 *            the reference to the language properties
	 * @param LOGGER
	 *            the reference to the logger
	 */
	public ReferenceLoader(Properties language, Logger LOGGER) {
		this.language = language;
		this.LOGGER = LOGGER;
		XSDErrorList = new ArrayList<>();
	}

	/**
	 * This method loads the XML file and gives the data structure of
	 * BPMNElements and References back.
	 * 
	 * @param referencesPath
	 *            path to the XML file with the references
	 * @param XSDPath
	 *            path to the XSD file for the references
	 * @return a hash map with the element names as keys and as value the
	 *         BPMNElements which contain the references
	 * @throws ValidatorException
	 *             if problems occurred while loading, traversing or checking
	 *             the 'references.xml' file against its XSD
	 */
	public HashMap<String, BPMNElement> load(String referencesPath,
			String XSDPath) throws ValidatorException {
		HashMap<String, BPMNElement> bpmnElements = new HashMap<>();
		SAXBuilder builder = new SAXBuilder();
		try {
			// schema validation
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new StreamSource(getClass()
					.getResourceAsStream(XSDPath)));
			Validator validator = schema.newValidator();
			validator.setErrorHandler(new XSDValidationLoggingErrorHandler());
			validator.validate(new StreamSource(getClass().getResourceAsStream(
					referencesPath)));
			if (XSDErrorList.size() > 0) {
				String xsdErrorText = language.getProperty("loader.xsd.general")
						+ System.lineSeparator();
				for (SAXParseException saxParseException : XSDErrorList) {
					xsdErrorText = xsdErrorText
							+ language.getProperty("loader.xsd.error.part1")
							+ saxParseException.getLineNumber() + " "
							+ language.getProperty("loader.xsd.error.part2")
							+ saxParseException.getMessage()
							+ System.lineSeparator();
				}
				LOGGER.severe(xsdErrorText);
				throw new ValidatorException(xsdErrorText);
			}

			Document document = builder.build(getClass()
					.getResourceAsStream(referencesPath));
			Element root = document.getRootElement();
			// separates all BPMN elements
			List<Element> elements = root.getChildren();
			for (Element element : elements) {
				String elementName = element.getAttributeValue("name");
				String parent = element.getChildText("parent");
				List<String> children = null;
				// separates possible child elements (element names of the
				// element, which inherits the references from the current
				// element)
				List<Element> childrenInFile = element.getChildren("child");
				if (childrenInFile.size() > 0) {
					children = new ArrayList<>();
					for (Element child : childrenInFile) {
						children.add(child.getText());
					}
				}
				// separates the references for the current element
				List<Reference> references = new ArrayList<>();
				List<Element> referencesInFile = element
						.getChildren("reference");
				for (Element reference : referencesInFile) {
					int number = Integer.parseInt(reference
                            .getAttributeValue("number"));
					String referenceName = reference.getChild("name").getText();
					ArrayList<String> types = null;
					List<Element> typesInFile = reference.getChildren("type");
					if (typesInFile.size() > 0) {
						types = new ArrayList<>();
						for (Element type : typesInFile) {
							types.add(type.getText());
						}
					}
					boolean qname = convertToBoolean(reference
							.getAttributeValue("qname"));
					boolean attribute = convertToBoolean(reference
							.getAttributeValue("attribute"));
					boolean special = false;
					String specialAttribute = reference
							.getAttributeValue("special");
					if (specialAttribute != null) {
						special = convertToBoolean(specialAttribute);
					}
					Reference bpmnReference = new Reference(number,
							referenceName, types, qname, attribute, special,
							language);
					references.add(bpmnReference);
				}
				BPMNElement bpmnElement = new BPMNElement(elementName, parent,
						children, references, language);
				bpmnElements.put(elementName, bpmnElement);
			}
		} catch (JDOMException e) {
			LOGGER.severe(language.getProperty("loader.jdom"));
			throw new ValidatorException(language.getProperty("loader.jdom"), e);
		} catch (IOException e) {
			LOGGER.severe(language.getProperty("loader.io"));
			throw new ValidatorException(language.getProperty("loader.io"), e);
		} catch (SAXException e) {
			LOGGER.severe(language.getProperty("loader.sax"));
			throw new ValidatorException(language.getProperty("loader.sax"), e);
		}
		// add not jet existing key references of the element children
		Map<String, BPMNElement> missingMap = new HashMap<>();
		for (String key : bpmnElements.keySet()) {
			BPMNElement bpmnElement = bpmnElements.get(key);
			if (bpmnElement.getChildren() != null) {
				for (String childName : bpmnElement.getChildren()) {
					if (!bpmnElements.containsKey(childName)) {
						missingMap.put(childName, bpmnElement);
					}
				}
			}
		}
		bpmnElements.putAll(missingMap);
		return bpmnElements;
	}

	private boolean convertToBoolean(String string) {
        return string.equals("true");
	}

	/**
	 * Inner class for getting the XSD violations.
	 * 
	 * @author Andreas Vorndran
	 * 
	 */
	private class XSDValidationLoggingErrorHandler implements ErrorHandler {

		@Override
		public void error(SAXParseException exception) throws SAXException {
			XSDErrorList.add(exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			XSDErrorList.add(exception);
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			XSDErrorList.add(exception);
		}

	}
}
