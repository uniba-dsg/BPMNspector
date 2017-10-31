package de.uniba.dsg.bpmnspector;

import api.*;
import de.jena.uni.mojo.Mojo;
import de.jena.uni.mojo.analysis.information.AnalysisInformation;
import de.jena.uni.mojo.error.AbundanceAnnotation;
import de.jena.uni.mojo.error.Annotation;
import de.jena.uni.mojo.error.DeadlockAnnotation;
import de.jena.uni.mojo.error.EAlarmCategory;
import de.jena.uni.mojo.interpreter.AbstractEdge;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

public class MojoValidator implements BpmnProcessValidator {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MojoValidator.class.getSimpleName());

    private final String MOJO_MESSAGE_PREFIX = "mojo";

    private final Mojo mojo;
    private final XMLOutputter xmlOutputter = new XMLOutputter();

    public MojoValidator() {
        this.mojo = new Mojo();
    }

    public void validate(BPMNProcess process, ValidationResult result) throws ValidationException {
        String processAsString = xmlOutputter.outputString(process.getProcessAsDoc());
        LOGGER.debug(processAsString);
        AnalysisInformation analysisInformation = new AnalysisInformation();
        // FIXME Use processAsString information as soon as Mojo is capable to do this
        List<Annotation> mojoResult = mojo.verify(process.getBaseURI(), processAsString, "bpmn.xml", analysisInformation, StandardCharsets.UTF_8);
        //List<Annotation> mojoResult = mojo.verify(new File(process.getBaseURI()), analysisInformation);
        if (!mojoResult.isEmpty()) {
            addMojoResultToValidationResult(mojoResult, result, process);
        }

    }

    private void addMojoResultToValidationResult(List<Annotation> mojoResult, ValidationResult validationResult, BPMNProcess baseProcess)
            throws ValidationException {
        for (Annotation annotation : mojoResult) {

            LOGGER.debug("Found an mojo annotation: Class: " + annotation.getClass() + " AlarmCategory: " + annotation.getAlarmCategory());
            if (LOGGER.isDebugEnabled()) {
                annotation.printInformation(null);
            }

            if (annotation instanceof DeadlockAnnotation) {
                handleDeadlockAnnotation(validationResult, baseProcess, annotation);
            } else if (annotation instanceof AbundanceAnnotation) {
                handleAbundanceAnnotation(validationResult, baseProcess, annotation);
            } else if (EAlarmCategory.WARNING.equals(annotation.getAlarmCategory())) {
                handleWarningAnnotation(validationResult, baseProcess, annotation);
            } else if (EAlarmCategory.ERROR.equals(annotation.getAlarmCategory())) {
                throw new ValidationException("Unknown error during mojo execution.");
            }
        }
    }

    private void handleDeadlockAnnotation(ValidationResult validationResult, BPMNProcess baseProcess, Annotation annotation) throws ValidationException {
        StringBuilder builder = new StringBuilder(MOJO_MESSAGE_PREFIX);
        builder.append(": Process contains a deadlock.\nPaths to deadlock:\n");
        List<List<AbstractEdge>> listOfPaths = ((DeadlockAnnotation) annotation).getListOfFailurePaths();
        for (List<AbstractEdge> path : listOfPaths) {
            builder.append("Path: ");
            for (int i = 0; i < path.size(); i++) {
                AbstractEdge edge = path.get(i);
                if (edge.source instanceof BaseElement) {
                    String edgeString = String.format("%s[%s] \u2192", edge.source.getClass().getSimpleName(), ((BaseElement) edge.source).getId());
                    builder.append(edgeString);
                }
            }
            Object lastTarget = path.get(path.size() - 1).target;
            String lastTargetString = String.format("%s[%s])%n",
                    lastTarget.getClass().getSimpleName(),
                    ((BaseElement) lastTarget).getId());
            builder.append(lastTargetString);
        }
        String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
        Location locationOfViolation = determineLocationByXPath(bpmnId, baseProcess);
        Violation violation = new Violation(locationOfViolation, builder.toString(), "Deadlock");
        validationResult.addViolation(violation);
    }

    private void handleAbundanceAnnotation(ValidationResult validationResult, BPMNProcess baseProcess, Annotation annotation) throws ValidationException {
        String message = MOJO_MESSAGE_PREFIX+"Process contains a lack of synchronization.";
        String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
        Location locationOfViolation = determineLocationByXPath(bpmnId, baseProcess);
        Violation violation = new Violation(locationOfViolation, message, "LackOfSync");
        validationResult.addViolation(violation);
    }

    private void handleWarningAnnotation(ValidationResult validationResult, BPMNProcess baseProcess, Annotation annotation) throws ValidationException {
        if (annotation.getInterpretedPrintableNodes().isEmpty()) {
            validationResult.addWarning(createMojoWarning(annotation.getDescription(), baseProcess));
        } else {
            BaseElement problematicElement = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0));
            validationResult.addWarning(createMojoWarning(annotation.getDescription(), baseProcess, problematicElement));
        }
    }

    private Warning createMojoWarning(String message, BPMNProcess affectedProcess, BaseElement affectedElement) throws ValidationException {
        String fullMessage;
        Location locationOfWarning;

        if (affectedElement == null) {
            locationOfWarning = new Location(Paths.get(affectedProcess.getBaseURI()), LocationCoordinate.EMPTY);
            fullMessage = String.format("%s: %s", MOJO_MESSAGE_PREFIX, message);
        } else {
            String bpmnId = affectedElement.getId();
            locationOfWarning = determineLocationByXPath(bpmnId, affectedProcess);
            fullMessage = String.format("%s: %s: %s", MOJO_MESSAGE_PREFIX, affectedElement.getClass().getSimpleName(), message);
        }

        return new Warning(fullMessage, locationOfWarning);
    }

    private Warning createMojoWarning(String message, BPMNProcess affectedProcess) throws ValidationException {
        return createMojoWarning(message, affectedProcess, null);
    }

    private Location determineLocationByXPath(String xpathExpression, BPMNProcess baseProcess)
            throws ValidationException {

        LOGGER.debug("Found ID:" + xpathExpression);

        int line = -1;
        int column = -1;

        // use ID with generated prefix for lookup
        String xpathObjectId = createIdBpmnExpression(xpathExpression);

        LOGGER.debug("Expression to evaluate: " + xpathObjectId);
        XPathFactory fac = XPathFactory.instance();
        List<Element> elems = fac.compile(xpathObjectId, Filters.element(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE)).evaluate(baseProcess.getProcessAsDoc());

        if (elems.size() == 1) {
            line = ((LocatedElement) elems.get(0)).getLine();
            column = ((LocatedElement) elems.get(0)).getColumn();
            //use ID without prefix  (=original ID) as Violation xPath
            xpathObjectId = createIdBpmnExpression(xpathExpression.substring(xpathExpression.indexOf('_') + 1));
        }

        if (line == -1 || column == -1) {
            throw new ValidationException("BPMN Element couldn't be found in file '" + baseProcess.getBaseURI() + "'!");
        }

        return new Location(Paths.get(baseProcess.getBaseURI()).toAbsolutePath(), new LocationCoordinate(line, column),
                xpathObjectId);
    }

    /**
     * creates a xpath expression for finding the id
     *
     * @param id the id, to which the expression should refer
     * @return the xpath expression, which refers the given id
     */
    private static String createIdBpmnExpression(String id) {
        return String.format("//bpmn:*[@id = '%s']", id);
    }

}
