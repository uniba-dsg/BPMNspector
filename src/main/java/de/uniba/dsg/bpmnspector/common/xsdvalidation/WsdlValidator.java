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

import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;

/**
 * validator for the schema validation of wsdl files
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class WsdlValidator extends XsdValidator {
    private Schema schema;
    private static final Logger LOGGER = LoggerFactory.getLogger(
            WsdlValidator.class
                    .getSimpleName());


    {
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory
                    .newSchema(resolveResourcePaths("wsdl20.xsd"));
        } catch (FileNotFoundException | SAXException e) {
            LOGGER.debug("schemafactory couldn't create schema, cause: {}", e);
        }
    }

    @Override
    public void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult)
            throws IOException, SAXException, ValidatorException {
        LOGGER.debug("xml xsd validation started: {}", xmlFile.getName());
        List<SAXParseException> xsdErrorList = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
        try {
            validator.validate(new StreamSource(xmlFile));
            for (SAXParseException saxParseException : xsdErrorList) {
                validationResult.getViolations().add(
                        new Violation("XML-XSD-Check", xmlFile.getName(),
                                saxParseException.getLineNumber(), "",
                                saxParseException.getMessage()));
                validationResult.setValid(false);
                LOGGER.info("xml xsd violation in {} at {} found",
                        xmlFile.getName(), saxParseException.getLineNumber());
            }
        } catch (SAXParseException e) {
            // if process is not well-formed exception is not processed via the error handler
            validationResult.getViolations().add(
                    new Violation("XSD-Check", xmlFile.getName(),
                            e.getLineNumber(), "",
                            e.getMessage()));
            validationResult.setValid(false);
            String msg = String.format("File %s is not well-formed at line %d: %s", xmlFile.getName(),
                    e.getLineNumber(), e.getMessage());
            LOGGER.info(msg);
            throw new ValidatorException("Cancel Validation as checked File is not well-formed.");
        }
    }
}
