package de.uniba.dsg.ppn.ba.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.stream.StreamSource;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnHelper;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.helper.PrintHelper;
import de.uniba.dsg.ppn.ba.helper.SetupHelper;
import de.uniba.dsg.ppn.ba.preprocessing.PreProcessResult;
import de.uniba.dsg.ppn.ba.preprocessing.PreProcessor;

/**
 * Implementation of BpmnValidator
 * <p>
 * More information : {@link BpmnValidator}
 * <p>
 * Does the validation process of the xsd and the schematron validation and
 * returns the results of the validation
 *
 * @author Philipp Neugebauer
 * @version 1.0
 */
public class SchematronBPMNValidator implements BpmnValidator {

    private final DocumentBuilder documentBuilder;
    private final PreProcessor preProcessor;
    private final XmlLocator xmlLocator;
    private final Ext001Checker ext001Checker;
    private final Ext002Checker ext002Checker;
    private final static Logger LOGGER;

    static {
        LOGGER = (Logger) LoggerFactory.getLogger(SchematronBPMNValidator.class
                .getSimpleName());
    }

    {
        documentBuilder = SetupHelper.setupDocumentBuilder();
        preProcessor = new PreProcessor();
        xmlLocator = new XmlLocator();
        ext001Checker = new Ext001Checker();
        ext002Checker = new Ext002Checker();
    }

    @Override
    public Level getLogLevel() {
        return LOGGER.getLevel();
    }

    @Override
    public void setLogLevel(Level logLevel) {
        // FIXME: without phloc libraries
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(logLevel);
    }

    @Override
    public List<ValidationResult> validateFiles(List<File> xmlFiles)
            throws BpmnValidationException {
        List<ValidationResult> validationResults = new ArrayList<>();
        for (File xmlFile : xmlFiles) {
            validationResults.add(validate(xmlFile));
        }
        return validationResults;
    }

    @Override
    public ValidationResult validate(File xmlFile)
            throws BpmnValidationException {
        final ISchematronResource schematronSchema = SchematronResourcePure
                .fromClassPath("validation.sch");
        if (!schematronSchema.isValidSchematron()) {
            LOGGER.debug("schematron file is invalid");
            throw new BpmnValidationException("Invalid Schematron file!");
        }

        LOGGER.info("Validating {}", xmlFile.getName());

        ValidationResult validationResult = new ValidationResult();

        try {
            Document headFileDocument = documentBuilder.parse(xmlFile);
            validationResult.getCheckedFiles().add(xmlFile.getAbsolutePath());
            File parentFolder = xmlFile.getParentFile();

            ext001Checker.checkConstraint001(xmlFile, parentFolder,
                    validationResult);
            ext002Checker.checkConstraint002(xmlFile, parentFolder,
                    validationResult);

            Map<String, String> namespaceTable = new HashMap<>();
            PreProcessResult preProcessResult = preProcessor.preProcess(
                    headFileDocument, parentFolder, namespaceTable);

            SchematronOutputType schematronOutputType = schematronSchema
                    .applySchematronValidationToSVRL(new StreamSource(
                            DocumentTransformer
                                    .transformToInputStream(headFileDocument)));
            for (int i = 0; i < schematronOutputType
                    .getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
                if (schematronOutputType
                        .getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
                    handleSchematronErrors(
                            xmlFile,
                            validationResult,
                            preProcessResult,
                            (FailedAssert) schematronOutputType
                                    .getActivePatternAndFiredRuleAndFailedAssertAtIndex(i));
                }
            }

            for (int i = 0; i < validationResult.getCheckedFiles().size(); i++) {
                File f = new File(validationResult.getCheckedFiles().get(i));
                validationResult.getCheckedFiles().set(i, f.getName());
            }
        } catch (SAXException | IOException e) {
            PrintHelper.printLogstatements(LOGGER, e, xmlFile.getName());
            throw new BpmnValidationException(
                    "Given file couldn't be read or doesn't exist!");
        } catch (Exception e) { // NOPMD
            LOGGER.debug("exception at schematron validation. Cause: {}", e);
            throw new BpmnValidationException(
                    "Something went wrong during schematron validation!");
        }

        validationResult.setValid(validationResult.getViolations().isEmpty());
        LOGGER.info("Validating process successfully done, file is valid: {}",
                validationResult.isValid());

        return validationResult;
    }

    private void handleSchematronErrors(File xmlFile,
            ValidationResult validationResult,
            PreProcessResult preProcessResult, FailedAssert failedAssert) {
        String message = failedAssert.getText().trim();
        String constraint = message.substring(0, message.indexOf('|'));
        String errorMessage = message.substring(message.indexOf('|') + 1);
        int line = xmlLocator.findLine(xmlFile, failedAssert.getLocation());
        String fileName = xmlFile.getName();
        String location = failedAssert.getLocation();
        LOGGER.info("violation of constraint {} in {} found.", constraint,
                fileName);
        if (line == -1) {
            try {
                String xpathId = "";
                if (failedAssert.getDiagnosticReferenceCount() > 0) {
                    xpathId = failedAssert.getDiagnosticReference().get(0)
                            .getText().trim();
                }
                String[] result = searchForViolationFile(xpathId,
                        validationResult, preProcessResult.getNamespaceTable());
                fileName = result[0];
                line = Integer.parseInt(result[1]);
                location = result[2];
            } catch (BpmnValidationException e) {
                fileName = e.getMessage();
            } catch (StringIndexOutOfBoundsException e) {
                fileName = "Element couldn't be found!";
            }
            LOGGER.debug("preprocessing needed. violation in {} at {}.",
                    fileName, line);
        }
        validationResult.getViolations().add(
                new Violation(constraint, fileName, line, location,
                        errorMessage));
    }

    /**
     * searches for the file and line, where the violation occured
     *
     * @param xpathExpression
     *            the expression, through which the file and line should be
     *            identified
     * @param validationResult
     *            for getting all checked files
     * @param namespaceTable
     *            to identify the file, where the violation occured
     * @return string array with filename, line and xpath expression to find the
     *         element
     * @throws BpmnValidationException
     *             if no element can be found
     */
    // TODO: extract in own object?
    private String[] searchForViolationFile(String xpathExpression,
            ValidationResult validationResult,
            Map<String, String> namespaceTable) throws BpmnValidationException {
        String fileName = "";
        String line = "-1";
        String xpathObjectId = "";

        String namespacePrefix = xpathExpression.substring(0,
                xpathExpression.indexOf('_'));
        String namespace = "";
        for (Map.Entry<String, String> entry : namespaceTable.entrySet()) {
            if (entry.getValue().equals(namespacePrefix)) {
                namespace = entry.getKey();
            }
        }

        for (String checkedFilePath : validationResult.getCheckedFiles()) {
            File checkedFile = new File(checkedFilePath);
            try {
                Document document = documentBuilder.parse(checkedFile);
                if (document.getDocumentElement()
                        .getAttribute("targetNamespace").equals(namespace)) {
                    xpathObjectId = BpmnHelper
                            .createIdBpmnExpression(xpathExpression
                                    .substring(xpathExpression.indexOf('_') + 1));
                    line = String.valueOf(xmlLocator.findLine(checkedFile,
                            xpathObjectId));
                    xpathObjectId += "[0]"; // NOPMD
                    fileName = checkedFile.getName();
                    break;
                }
            } catch (SAXException | IOException e) {
                PrintHelper
                        .printLogstatements(LOGGER, e, checkedFile.getName());
            }
        }

        if ("-1".equals(line)) {
            throw new BpmnValidationException("BPMN Element couldn't be found!");
        }

        return new String[] { fileName, line, xpathObjectId };
    }
}
