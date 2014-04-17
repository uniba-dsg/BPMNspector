package de.uniba.dsg.ppn.ba.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;

/**
 * 
 * @author Andreas Vorndran, Philipp Neugebauer
 * 
 */
public class XsdValidator {

	private SchemaFactory schemaFactory;
	private Schema schema;
	private Logger logger;

	{
		schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver(new ResourceResolver());
		logger = (Logger) LoggerFactory.getLogger("BpmnValidator");
		try {
			schema = schemaFactory.newSchema(new Source[] {
					resolveResourcePaths("DC.xsd"),
					resolveResourcePaths("DI.xsd"),
					resolveResourcePaths("BPMNDI.xsd"),
					resolveResourcePaths("BPMN20.xsd") });
		} catch (FileNotFoundException | SAXException e) {
			// TODO logging
			// won't happen normally
			System.out.println(e.getClass());
			System.out.println(e.getMessage());
		}
	}

	public void validateAgainstXsd(File xmlFile,
			ValidationResult validationResult) throws IOException, SAXException {
		logger.info("xsd validation started: {}", xmlFile.getName());
		List<SAXParseException> xsdErrorList = new ArrayList<>();
		Validator validator = schema.newValidator();
		validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
		validator.validate(new StreamSource(xmlFile));
		for (SAXParseException saxParseException : xsdErrorList) {
			validationResult.getViolations().add(
					new Violation("XSD-Check", xmlFile.getName(),
							saxParseException.getLineNumber(), "",
							saxParseException.getMessage()));
			logger.info("xsd violation in {} at {} found", xmlFile.getName(),
					saxParseException.getLineNumber());
		}
	}

	private StreamSource resolveResourcePaths(String resourceName)
			throws FileNotFoundException {
		return new StreamSource(new FileInputStream(Paths.get("src")
				.resolve("main").resolve("resources").resolve(resourceName)
				.toFile()));
	}
}
