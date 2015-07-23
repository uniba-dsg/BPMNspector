package de.uniba.dsg.bpmnspector.refcheck;

import api.ValidationException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class loads the references form the references.xml file. The XML file
 * needs to fit the references.xsd schema. The references will be structured by
 * the classes {@link BPMNElement} and {@link Reference}.
 * 
 * @author Andreas Vorndran
 * @author Matthias Geiger
 *
 * @version 1.0
 * @see BPMNElement
 * @see Reference
 * 
 */
public class ReferenceLoader {

	private final List<SAXParseException> xsdErrorList;

	/**
	 * Constructor
	 */
	public ReferenceLoader() {
		xsdErrorList = new ArrayList<>();
	}

	/**
	 * This method loads the XML file and gives the data structure of
	 * BPMNElements and References back.
	 * 
	 * @param referencesPath
	 *            path to the XML file with the references
	 * @param xsdPath
	 *            path to the XSD file for the references
	 * @return a hash map with the element names as keys and as value the
	 *         BPMNElements which contain the references
	 * @throws ValidationException
	 *             if problems occurred while loading, traversing or checking
	 *             the 'references.xml' file against its XSD
	 */
	public Map<String, BPMNElement> load(String referencesPath,
			String xsdPath) throws ValidationException {
		try (InputStream refPathStream = getClass().getResourceAsStream(referencesPath)) {

			validateReferencesFile(referencesPath, xsdPath);

			Document document = new SAXBuilder().build(refPathStream);
			Element root = document.getRootElement();

			return createReferences(root);

		} catch (JDOMException | IOException e) {
			throw new ValidationException("Problems occurred while traversing the 'references.xml' file.", e);
		}
	}

	private Map<String, BPMNElement> createReferences(Element rootElem) {
		Map<String, BPMNElement> bpmnElements = new HashMap<>();
		// separates all BPMN elements
		List<Element> elements = rootElem.getChildren();
		for (Element element : elements) {
            String elementName = element.getAttributeValue("name");
            String parent = element.getChildText("parent");
            List<String> children = null;
            // separates possible child elements (element names of the
            // element, which inherits the references from the current
            // element)
            List<Element> childrenInFile = element.getChildren("child");
            if (!childrenInFile.isEmpty()) {
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
                if (!typesInFile.isEmpty()) {
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
                        referenceName, types, qname, attribute, special);
                references.add(bpmnReference);
            }
            BPMNElement bpmnElement = new BPMNElement(elementName, parent,
                    children, references);
            bpmnElements.put(elementName, bpmnElement);
        }

		// add not yet existing key references of the element children
		Map<String, BPMNElement> missingMap = new HashMap<>();

        bpmnElements.entrySet().stream()
                .filter(entry -> entry.getValue().getChildren() != null)
                .forEach(entry ->
                    entry.getValue().getChildren().stream()
                            .filter(childName -> !bpmnElements.containsKey(childName))
                            .forEach(childName -> missingMap.put(childName, entry.getValue())));

		bpmnElements.putAll(missingMap);

		return bpmnElements;
	}

	private void validateReferencesFile(String referencesPath, String xsdPath)
			throws ValidationException, IOException {
		try (InputStream refPathStream = getClass().getResourceAsStream(referencesPath);
				InputStream xsdPathStream = getClass().getResourceAsStream(xsdPath)) {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory
					.newSchema(new StreamSource(xsdPathStream));
			Validator validator = schema.newValidator();
			validator.setErrorHandler(new XSDValidationLoggingErrorHandler());
			validator.validate(new StreamSource(refPathStream));
			if (!xsdErrorList.isEmpty()) {
				StringBuilder xsdErrorText = new StringBuilder(200)
						.append("While the XSD validation of the 'references.xml' file the following errors occurred:")
						.append(System.lineSeparator());
				for (SAXParseException saxParseException : xsdErrorList) {
					xsdErrorText.append("line: ")
							.append(saxParseException.getLineNumber())
							.append(':')
							.append(saxParseException.getMessage())
							.append(System.lineSeparator());
				}
				throw new ValidationException(xsdErrorText.toString());
			}
		} catch (SAXException e) {
			throw new ValidationException("Problems occurred while trying to check the references XML file against the corresponding XSD file.", e);
		}

	}

	private boolean convertToBoolean(String string) {
        return "true".equals(string);
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
			xsdErrorList.add(exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			xsdErrorList.add(exception);
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			xsdErrorList.add(exception);
		}

	}
}
