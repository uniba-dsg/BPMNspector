package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.CharConversionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Does the bpmn xsd validation step
 *
 * @author Andreas Vorndran
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class BpmnXsdValidator extends AbstractXsdValidator {

    private Schema schema;
    private static final Logger LOGGER = LoggerFactory.getLogger(BpmnXsdValidator.class.getSimpleName());

    {
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new ResourceResolver());
        try {
            schema = schemaFactory.newSchema(new Source[] {
                    resolveResourcePaths("DC.xsd"),
                    resolveResourcePaths("DI.xsd"),
                    resolveResourcePaths("BPMNDI.xsd"),
                    resolveResourcePaths("BPMN20.xsd") });
        } catch (FileNotFoundException | SAXException e) {
            LOGGER.debug("schemafactory couldn't create schema, cause: {}", e);
        }
    }

    @Override
    public void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult)
            throws IOException, SAXException, ValidationException {
        LOGGER.debug("xsd validation started: {}", xmlFile.getName());
        List<SAXParseException> xsdErrorList = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
        try {
            StreamSource src = new StreamSource(xmlFile);
            validator.validate(src);
            for (SAXParseException saxParseException : xsdErrorList) {
                Location location = new Location(xmlFile.toPath().toAbsolutePath(),
                        new LocationCoordinate(saxParseException.getLineNumber(),
                                saxParseException.getColumnNumber()));
                Violation violation = new Violation(location, saxParseException.getMessage(), "XSD-Check");
                if(!validationResult.getViolations().contains(violation)) {
                    validationResult.addViolation(violation);
                     LOGGER.info("xsd violation in {} at {} found", xmlFile.getName(),
                         saxParseException.getLineNumber());
                }
            }
        } catch (CharConversionException e) {
            // Thrown if file encoding is not valid
            String msg = "File "+xmlFile.toString()+" does not have claimed encoding - further processing is not possible.";
            Location location = new Location(xmlFile.toPath().toAbsolutePath(),
                    new LocationCoordinate(1,1));
            Violation violation = new Violation(location, msg, "XSD-Check");
            if(!validationResult.getViolations().contains(violation)) {
                validationResult.addViolation(violation);
                LOGGER.info(msg);
            }
            throw new ValidationException("Cancel Validation as checked File does not have claimed encoding.");
        } catch (SAXParseException e) {
            // if process is not well-formed exception is not processed via the error handler
            Location location = new Location(xmlFile.toPath().toAbsolutePath(),
                    new LocationCoordinate(e.getLineNumber(),
                            e.getColumnNumber()));
            Violation violation = new Violation(location, e.getMessage(), "XSD-Check");
            validationResult.addViolation(violation);
            String msg = String.format("File %s is not well-formed at line %d: %s", xmlFile.getName(),
                    e.getLineNumber(), e.getMessage());
            LOGGER.info(msg);
            throw new ValidationException("Cancel Validation as checked File is not well-formed.");
        }

    }

}
