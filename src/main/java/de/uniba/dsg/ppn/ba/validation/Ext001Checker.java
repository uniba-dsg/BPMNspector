package de.uniba.dsg.ppn.ba.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.WsdlValidator;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.XmlValidator;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
import de.uniba.dsg.ppn.ba.helper.ImportedFilesCrawler;
import de.uniba.dsg.ppn.ba.helper.PrintHelper;
import de.uniba.dsg.ppn.ba.helper.SetupHelper;
import de.uniba.dsg.ppn.ba.preprocessing.ImportedFile;
import org.xml.sax.SAXParseException;

public class Ext001Checker {

    private WsdlValidator wsdlValidator;
    private XmlValidator xmlValidator;
    private final DocumentBuilder documentBuilder;
    private final static Logger LOGGER = LoggerFactory.getLogger(Ext001Checker.class.getSimpleName());
    private final BpmnXsdValidator bpmnXsdValidator;
    private final XmlLocator xmlLocator;
    private static final String CONSTRAINTNUMBER = "EXT.001";

    {
        documentBuilder = SetupHelper.setupDocumentBuilder();
        bpmnXsdValidator = new BpmnXsdValidator();
        xmlLocator = new XmlLocator();
    }

    /**
     * checks, if there are violations of the EXT.001 constraint
     *
     * @param headFile
     *            the file which should be checked
     * @param folder
     *            the parent folder of the file
     * @param validationResult
     *            the current validation result of validating process for adding
     *            found violations
     */
    public void checkConstraint001(File headFile, File folder,
            ValidationResult validationResult) {
        try {
            bpmnXsdValidator.validateAgainstXsd(headFile, validationResult);
            Document headFileDocument = documentBuilder.parse(headFile);

            List<ImportedFile> importedFiles = ImportedFilesCrawler
                    .selectImportedFiles(headFileDocument, folder, 0, false);

            for (ImportedFile importedFile : importedFiles) {
                checkConstraintsinFile(importedFile, headFile, folder,
                        validationResult);
            }
        } catch (SAXParseException e) {
            createAndLogWellFormednesViolation(e, headFile, validationResult);
        } catch (SAXException | IOException e) {
            PrintHelper.printLogstatements(LOGGER, e, headFile.getName());
        }
    }

    private void checkConstraintsinFile(ImportedFile importedFile,
            File headFile, File folder, ValidationResult validationResult)
            throws IOException, SAXException {
        File file = importedFile.getFile();
        if (!file.exists()) { // NOPMD
            String xpathLocation = createImportString(file.getName());
            String fileName = file.getName();
            int line = xmlLocator.findLine(headFile, xpathLocation);
            validationResult.getViolations().add(
                    new Violation(CONSTRAINTNUMBER, fileName, line,
                            xpathLocation + "[0]",
                            "The imported file does not exist"));
            LOGGER.info("violation of constraint {} in {} found.",
                    CONSTRAINTNUMBER, fileName);
        } else if (ConstantHelper.BPMNNAMESPACE.equals(importedFile
                .getImportType())) {
            checkConstraint001(file, folder, validationResult);
        } else if ("http://www.w3.org/TR/wsdl20/".equals(importedFile
                .getImportType())) {
            if (wsdlValidator == null) {
                wsdlValidator = new WsdlValidator();
            }
            try {
                wsdlValidator.validateAgainstXsd(file, validationResult);
            } catch (SAXParseException e) {
                createAndLogWellFormednesViolation(e, file, validationResult);
            }
        } else if ("http://www.w3.org/2001/XMLSchema".equals(importedFile
                .getImportType())) {
            if (xmlValidator == null) {
                xmlValidator = new XmlValidator();
            }
            try {
                xmlValidator.validateAgainstXsd(file, validationResult);
            } catch (SAXParseException e) {
                createAndLogWellFormednesViolation(e, file, validationResult);
            }
        }
    }

    private String createImportString(String fileName) {
        return String.format("//bpmn:import[@location = '%s']", fileName);
    }

    private void createAndLogWellFormednesViolation(SAXParseException e, File affectedFile, ValidationResult validationResult) {
        validationResult.getViolations().add(
                new Violation("XSD-Check", affectedFile.getName(),
                        e.getLineNumber(), "",
                        e.getMessage()));
        validationResult.getCheckedFiles().add(affectedFile.getName());
        LOGGER.info("XML not well-formed in {} at line {}", affectedFile.getName(),
                e.getLineNumber());
    }
}
