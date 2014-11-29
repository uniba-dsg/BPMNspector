package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;

/**
 * validator for the schema validation of xml files
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class XmlValidator extends XsdValidator {

    private Schema schema;
    private static final Logger LOGGER;

    static {
        LOGGER = (Logger) LoggerFactory.getLogger(XmlValidator.class
                .getSimpleName());
    }

    {
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory
                    .newSchema(resolveResourcePaths("XMLSchema.xsd"));
        } catch (FileNotFoundException | SAXException e) {
            LOGGER.debug("schemafactory couldn't create schema, cause: {}", e);
        }
    }

    @Override
    public void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult) throws IOException, SAXException {
        LOGGER.debug("xml validation started: {}", xmlFile.getName());
        List<SAXParseException> xsdErrorList = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
        validator.validate(new StreamSource(xmlFile));
        for (SAXParseException saxParseException : xsdErrorList) {
            validationResult.getViolations().add(
                    new Violation("XML-Check", xmlFile.getName(),
                            saxParseException.getLineNumber(), "",
                            saxParseException.getMessage()));
            LOGGER.info("xml violation in {} at {} found", xmlFile.getName(),
                    saxParseException.getLineNumber());
        }
    }
}
