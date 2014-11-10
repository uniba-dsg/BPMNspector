package de.uniba.dsg.ppn.ba.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
 * Does the bpmn xsd validation step
 *
 * @author Andreas Vorndran, Philipp Neugebauer
 * @version 1.0
 *
 */
public class BpmnXsdValidator extends XsdValidator {

    private Schema schema;
    private final Logger logger;

    {
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new ResourceResolver());
        logger = (Logger) LoggerFactory.getLogger(getClass().getSimpleName());
        try {
            schema = schemaFactory.newSchema(new Source[] {
                    resolveResourcePaths("DC.xsd"),
                    resolveResourcePaths("DI.xsd"),
                    resolveResourcePaths("BPMNDI.xsd"),
                    resolveResourcePaths("BPMN20.xsd") });
        } catch (FileNotFoundException | SAXException e) {
            logger.debug("schemafactory couldn't create schema, cause: {}", e);
        }
    }

    @Override
    public void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult) throws IOException, SAXException {
        logger.debug("xsd validation started: {}", xmlFile.getName());
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

}
