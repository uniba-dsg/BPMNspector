package de.uniba.dsg.bpmnspector.schematron;

import api.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import de.uniba.dsg.bpmnspector.schematron.preprocessing.PreProcessor;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.output.DOMOutputter;
import org.jdom2.xpath.XPathFactory;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Does the validation process of the xsd and the schematron validation and
 * returns the results of the validation
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 */
public class SchematronBPMNValidator {

    private final PreProcessor preProcessor;
    private final ProcessImporter bpmnImporter;
    private final Ext002Checker ext002Checker;
    private final static Logger LOGGER;

    static {
        LOGGER = (Logger) LoggerFactory.getLogger(SchematronBPMNValidator.class
                .getSimpleName());
    }

    {
        preProcessor = new PreProcessor();
        bpmnImporter = new ProcessImporter();
        ext002Checker = new Ext002Checker();
    }

    public Level getLogLevel() {
        return LOGGER.getLevel();
    }

    public void setLogLevel(Level logLevel) {
        // FIXME: without phloc libraries
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(logLevel);
    }

    public List<ValidationResult> validateFiles(List<File> xmlFiles)
            throws ValidationException {
        List<ValidationResult> validationResults = new ArrayList<>();
        for (File xmlFile : xmlFiles) {
            validationResults.add(validate(xmlFile));
        }
        return validationResults;
    }

    public ValidationResult validate(File xmlFile)
            throws ValidationException {

        ValidationResult validationResult = new UnsortedValidationResult();
        // Trying to import process
        BPMNProcess process = bpmnImporter
                .importProcessFromPath(Paths.get(xmlFile.getPath()), validationResult);
        if(process != null) {
            validate(process, validationResult);
        }
        return validationResult;

    }

    public ValidationResult validate(BPMNProcess process, ValidationResult validationResult)
            throws ValidationException {
        final List<ISchematronResource> schemaToCheck = loadAndValidateSchematronFiles();

        LOGGER.info("Validating {}", process.getBaseURI());

        try {
                // EXT.002 checks whether there are ID duplicates - as ID
                // duplicates in a single file are already detected during XSD
                // validation this is only relevant if other processes are imported
                if(!process.getChildren().isEmpty()) {
                    ext002Checker.checkConstraint002(process, validationResult);
                }

                org.jdom2.Document documentToCheck = preProcessor.preProcess(process);
                DOMOutputter domOutputter = new DOMOutputter();
                Document w3cDoc = domOutputter
                        .output(documentToCheck);
                for(ISchematronResource schematronFile : schemaToCheck) {
                    SchematronOutputType schematronOutputType = schematronFile
                            .applySchematronValidationToSVRL(new DOMSource(w3cDoc));

                    schematronOutputType.getActivePatternAndFiredRuleAndFailedAssert().stream()
                            .filter(obj -> obj instanceof FailedAssert)
                            .forEach(obj -> handleSchematronErrors(process, validationResult, (FailedAssert) obj));
                }
        } catch (Exception e) { // NOPMD
            LOGGER.debug("exception during schematron validation. Cause: {}", e);
            throw new ValidationException(
                    "Something went wrong during schematron validation!");
        }

        LOGGER.info("Validating process successfully done, file is valid: {}",
                validationResult.isValid());

        return validationResult;
    }


    private List<ISchematronResource> loadAndValidateSchematronFiles() throws ValidationException {
        List<ISchematronResource> schemasToCheck = new ArrayList<>();

        final ISchematronResource schematronSchemaDescriptive = SchematronResourcePure
                .fromClassPath("EXT_descriptive.sch");
        if (!schematronSchemaDescriptive.isValidSchematron()) {
            LOGGER.debug("schematron file for Descriptive Conformance class is invalid");
            throw new ValidationException("Invalid Schematron file (EXT_descriptive.sch)!");
        } else {
            schemasToCheck.add(schematronSchemaDescriptive);
        }

        final ISchematronResource schematronSchemaAnalytic = SchematronResourcePure.fromClassPath("EXT_analytic.sch");
        if(!schematronSchemaAnalytic.isValidSchematron()) {
            LOGGER.debug("schematron file for Analytic Conformance class is invalid");
            throw new ValidationException("Invalid Schematron file (EXT_analytic.sch)!");
        } else {
            schemasToCheck.add(schematronSchemaAnalytic);
        }

        final ISchematronResource schematronSchemaCommonExec = SchematronResourcePure.fromClassPath("EXT_commonExec.sch");
        if(!schematronSchemaCommonExec.isValidSchematron()) {
            LOGGER.debug("schematron file for Common Executable Conformance class is invalid");
            throw new ValidationException("Invalid Schematron file (EXT_commonExec.sch)!");
        } else {
            schemasToCheck.add(schematronSchemaCommonExec);
        }

        final ISchematronResource schematronSchemaFull = SchematronResourcePure.fromClassPath("EXT_full.sch");
        if(!schematronSchemaFull.isValidSchematron()) {
            LOGGER.debug("schematron file for Full Conformance class is invalid");
            throw new ValidationException("Invalid Schematron file (EXT_full.sch)!");
        } else {
            schemasToCheck.add(schematronSchemaFull);
        }

        return schemasToCheck;
    }

    /**
     * tries to locate errors in the specific files
     *
     * @param baseProcess
     *            the file where the error must be located
     * @param validationResult
     *            the result of the validation to add new found errors
     * @param failedAssert
     *            the error of the schematron validation
     */
    private void handleSchematronErrors(BPMNProcess baseProcess,
            ValidationResult validationResult, FailedAssert failedAssert) {
        String message = failedAssert.getText().trim();
        String constraint = message.substring(0, message.indexOf('|'));
        String errorMessage = message.substring(message.indexOf('|') + 1);

        Location violationLocation = null;

        String location = "";

        // direct usage of xpath expression failedAssert.getLocation() not possible
        // when using JDOM - predicates are not supported correctly.
        // Instead: determine the index of the element to be found and use this
        // accessor in the list of found elements
        Pattern p = Pattern.compile("\\[\\d+\\]");
        Matcher m = p.matcher(failedAssert.getLocation());
        int xpathIndex = 0;
        if(m.find()) {
            xpathIndex = Integer.parseInt(
                    m.group(0).replace("[", "").replace("]", ""));
            location = failedAssert.getLocation().replace(m.group(0), "");
        }

        LOGGER.debug("XPath to evaluate: "+location);
        XPathFactory fac = XPathFactory.instance();
        List<Element> elems = fac.compile(location, Filters.element(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE)).evaluate(
                baseProcess.getProcessAsDoc());
        LOGGER.debug("Number of found elems: "+elems.size());
        String logText;
        if(elems.size()>=xpathIndex+1) {
            int line = ((LocatedElement) elems.get(xpathIndex)).getLine();
            int column = ((LocatedElement) elems.get(xpathIndex)).getColumn();
            String fileName = baseProcess.getBaseURI();
            location = failedAssert.getLocation();
            violationLocation = new Location(
                    Paths.get(fileName).toAbsolutePath(),
                    new LocationCoordinate(line, column), location);
            logText = String.format(
                    "violation of constraint %s found in %s at line %s.",
                    constraint, violationLocation.getFileName().getFileName().toString(), violationLocation.getLocation().getRow());
        } else {
            try {
                String xpathId = "";
                if (failedAssert.getDiagnosticReferenceCount() > 0) {
                    xpathId = failedAssert.getDiagnosticReference().get(0)
                            .getText().trim();
                }
                LOGGER.debug("Trying to locate in files: "+xpathId);
                violationLocation = searchForViolationFile(xpathId, baseProcess);
                logText = String.format(
                        "violation of constraint %s found in %s at line %s.",
                        constraint, violationLocation.getFileName().getFileName().toString(), violationLocation.getLocation().getRow());
            } catch (ValidationException | StringIndexOutOfBoundsException e) {
                LOGGER.error("Line of affected Element could not be determined.");
                logText = String.format("Found violation of constraint %s but the correct location could not be determined.",
                        constraint);
            }
        }

        LOGGER.info(logText);

        Violation violation = new Violation(violationLocation, errorMessage, constraint);
        validationResult.addViolation(violation);
    }

    /**
     * searches for the file and line, where the violation occured
     *
     * @param xpathExpression
     *            the expression, through which the file and line should be
     *            identified
     * @param baseProcess
     *            baseProcess used for validation
     * @return the violation Location
     * @throws ValidationException
     *             if no element can be found
     */
    private Location searchForViolationFile(String xpathExpression,
            BPMNProcess baseProcess) throws ValidationException {

        String namespacePrefix = xpathExpression.substring(0,
                xpathExpression.indexOf('_'));

        Optional<BPMNProcess> optional = baseProcess.findProcessByGeneratedPrefix(namespacePrefix);
        if(optional.isPresent()) {
            String fileName = optional.get().getBaseURI();

            int line = -1;
            int column = -1;

            // use ID with generated prefix for lookup
            String xpathObjectId = createIdBpmnExpression(xpathExpression);

            LOGGER.debug("Expression to evaluate: "+xpathObjectId);
            XPathFactory fac = XPathFactory.instance();
            List<Element> elems = fac.compile(xpathObjectId, Filters.element(), null,
                    Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE)).evaluate(optional.get().getProcessAsDoc());

            if(elems.size()==1) {
                line = ((LocatedElement) elems.get(0)).getLine();
                column = ((LocatedElement) elems.get(0)).getColumn();
                //use ID without prefix  (=original ID) as Violation xPath
                xpathObjectId = createIdBpmnExpression(xpathExpression
                        .substring(xpathExpression.indexOf('_') + 1));
            }

            if (line==-1 || column==-1) {
                throw new ValidationException("BPMN Element couldn't be found in file '"+fileName+"'!");
            }

            return new Location(Paths.get(fileName).toAbsolutePath(), new LocationCoordinate(line, column), xpathObjectId);

        } else {
            // File not found
            throw new ValidationException("BPMN Element couldn't be found as no corresponding file could be found.");
        }
    }

    /**
     * creates a xpath expression for finding the id
     *
     * @param id
     *            the id, to which the expression should refer
     * @return the xpath expression, which refers the given id
     */
    private static String createIdBpmnExpression(String id) {
        return String.format("//bpmn:*[@id = '%s']", id);
    }
}
