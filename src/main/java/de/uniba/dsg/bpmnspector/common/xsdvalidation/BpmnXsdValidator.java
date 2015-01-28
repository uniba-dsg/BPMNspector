package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.Violation;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Does the bpmn xsd validation step
 *
 * @author Andreas Vorndran, Philipp Neugebauer
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
            throws IOException, SAXException, ValidatorException {
        LOGGER.debug("xsd validation started: {}", xmlFile.getName());
        List<SAXParseException> xsdErrorList = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
        try {
            validator.validate(new StreamSource(xmlFile));
            for (SAXParseException saxParseException : xsdErrorList) {
                Violation violation = new Violation("XSD-Check", xmlFile.getName(),
                        saxParseException.getLineNumber(), "",
                        saxParseException.getMessage());
                if(!validationResult.getViolations().contains(violation)) {
                    validationResult.getViolations().add(violation);
                    validationResult.setValid(false);
                     LOGGER.info("xsd violation in {} at {} found", xmlFile.getName(),
                         saxParseException.getLineNumber());
                }
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
