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
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class MojoValidator implements BpmnProcessValidator {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MojoValidator.class.getSimpleName());

    private final String MOJO_MESSAGE_PREFIX = "mojo";

    private final String UNSUPPORTED_STRING = "The marked bpmn element is not supported yet. It will be handled as a task.";

    private final Mojo mojo;
    private final XMLOutputter xmlOutputter = new XMLOutputter();

    public MojoValidator() {
        this.mojo = new Mojo();
    }

    public void validate(BPMNProcess process, ValidationResult result) throws ValidationException {
        String processAsString = xmlOutputter.outputString(process.getProcessAsDoc());
        LOGGER.debug(processAsString);

        if (process.hasConditionalSeqFlowTasks()) {
            result.addWarning(createMojoWarning("Unable to perform valid mojo execution. " +
                    "Conditional sequence flows attached to tasks are not supported.", process));

            return;
        }

        AnalysisInformation analysisInformation = new AnalysisInformation();
        List<Annotation> mojoResult = Collections.emptyList();
        try {
            // FIXME Use processAsString information as soon as Mojo is capable to do this
            mojoResult = mojo.verify(process.getBaseURI(), processAsString, "bpmn.xml", analysisInformation, StandardCharsets.UTF_8);
            //List<Annotation> mojoResult = mojo.verify(new File(process.getBaseURI()), analysisInformation);
        } catch (Exception e) { // FIXME just to make sure failing mojo validation does not kill the whole validation proces
            LOGGER.warn("Validation of process " + process.getBaseURI() + " failed due to internal problems in mojo: ", e);
            result.addWarning(createMojoWarning("Mojo validation failed due to internal problems.", process));
        }

        if (!mojoResult.isEmpty()) {
            addMojoResultToValidationResult(mojoResult, result, process);
        }

    }

    private void addMojoResultToValidationResult(List<Annotation> mojoResult, ValidationResult validationResult, BPMNProcess baseProcess)
            throws ValidationException {

        boolean containsUnsupportedElems = checkAndHandleUnsupportedElements(mojoResult, validationResult, baseProcess);

        if (containsUnsupportedElems) {
            handleFurtherElements(mojoResult, validationResult, baseProcess, FindingType.WARNING);
        } else {
            handleFurtherElements(mojoResult, validationResult, baseProcess, FindingType.VIOLATION);
        }

    }


    private boolean checkAndHandleUnsupportedElements(List<Annotation> mojoResult, ValidationResult validationResult, BPMNProcess baseProcess) throws ValidationException {
        if (mojoResult.isEmpty()) {
            return false;
        }

        SortedSet<String> unsupportedElements = new TreeSet<>();
        Iterator<Annotation> iterator = mojoResult.iterator();
        while (iterator.hasNext()) {
            Annotation annotation = iterator.next();
            if (EAlarmCategory.WARNING.equals(annotation.getAlarmCategory())
                    && UNSUPPORTED_STRING.equals(annotation.getDescription())) {
                BaseElement problematicElement = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0));
                unsupportedElements.add(problematicElement.getClass().getSimpleName());

                // remove Unsupported Element warning from mojoResult
                iterator.remove();
            }
        }

        if (!unsupportedElements.isEmpty()) {
            Warning warning = createMojoWarning("Unable to perform valid mojo execution. The following elements are unsupported: "
                            + String.join(", ", unsupportedElements),
                    baseProcess);
            validationResult.addWarning(warning);
            return true;
        }

        return false;
    }

    private void handleFurtherElements(List<Annotation> mojoResult, ValidationResult validationResult, BPMNProcess baseProcess, FindingType findingType) throws ValidationException {
        for (Annotation annotation : mojoResult) {

            LOGGER.debug("Found an mojo annotation: Class: " + annotation.getClass() + " AlarmCategory: " + annotation.getAlarmCategory());
            if (LOGGER.isDebugEnabled()) {
                annotation.printInformation(null);
            }

            if (annotation instanceof DeadlockAnnotation) {
                handleDeadlockAnnotation(validationResult, baseProcess, annotation, findingType);
            } else if (annotation instanceof AbundanceAnnotation) {
                handleAbundanceAnnotation(validationResult, baseProcess, annotation, findingType);
            } else if (EAlarmCategory.WARNING.equals(annotation.getAlarmCategory())) {
                handleWarningAnnotation(validationResult, baseProcess, annotation);
            } else if (EAlarmCategory.ERROR.equals(annotation.getAlarmCategory())) {
                throw new ValidationException("Unknown error during mojo execution.");
            }
        }
    }

    private void handleDeadlockAnnotation(ValidationResult validationResult, BPMNProcess baseProcess,
                                          Annotation annotation, FindingType findingType) throws ValidationException {
        StringBuilder builder = new StringBuilder(MOJO_MESSAGE_PREFIX);
        builder.append(": Process contains a deadlock.\nPaths to deadlock:\n");
        List<List<AbstractEdge>> listOfPaths = ((DeadlockAnnotation) annotation).getListOfFailurePaths();
        for (List<AbstractEdge> path : listOfPaths) {
            builder.append("Path: ");
            for (AbstractEdge edge : path) {
                if (edge.source instanceof BaseElement) {
                    if(edge.source instanceof FlowElement && ((FlowElement) edge.source).getName() != null
                            && (!"".equals(((FlowElement) edge.source).getName()))) {
                        String edgeString = String.format("%s[id: %s; name: %s] \u2192", edge.source.getClass().getSimpleName(),
                                ((BaseElement) edge.source).getId(), ((FlowElement) edge.source).getName());
                        builder.append(edgeString);
                    } else {
                        String edgeString = String.format("%s[id: %s] \u2192", edge.source.getClass().getSimpleName(), ((BaseElement) edge.source).getId());
                        builder.append(edgeString);
                    }
                }
            }
            Object lastTarget = path.get(path.size() - 1).target;
            String lastTargetString = String.format("%s[%s])%n",
                    lastTarget.getClass().getSimpleName(),
                    ((BaseElement) lastTarget).getId());
            builder.append(lastTargetString);
        }
        String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
        Location locationOfViolation = baseProcess.determineLocationByXPath(bpmnId);

        if (findingType.equals(FindingType.WARNING)) {
            Warning warning = new Warning("Potential Deadlock: " + builder.toString(), locationOfViolation);
            validationResult.addWarning(warning);
        } else {
            Violation violation = new Violation(locationOfViolation, builder.toString(), "Deadlock");
            validationResult.addViolation(violation);
        }
    }

    private void handleAbundanceAnnotation(ValidationResult validationResult, BPMNProcess baseProcess,
                                           Annotation annotation, FindingType findingType) throws ValidationException {

        String message = MOJO_MESSAGE_PREFIX + "Process contains a lack of synchronization.";
        String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
        Location locationOfViolation = baseProcess.determineLocationByXPath(bpmnId);

        if (findingType.equals(FindingType.WARNING)) {
            Warning warning = new Warning("Potential LackOfSync: " + message, locationOfViolation);
            validationResult.addWarning(warning);
        } else {
            Violation violation = new Violation(locationOfViolation, message, "LackOfSync");
            validationResult.addViolation(violation);
        }
    }

    private void handleWarningAnnotation(ValidationResult validationResult, BPMNProcess baseProcess,
                                         Annotation annotation) throws ValidationException {
        if (annotation.getInterpretedPrintableNodes().isEmpty()) {
            validationResult.addWarning(createMojoWarning(annotation.getDescription(), baseProcess));
        } else {
            BaseElement problematicElement = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0));
            validationResult.addWarning(createMojoWarning(annotation.getDescription(), baseProcess, problematicElement));
        }
    }

    private Warning createMojoWarning(String message, BPMNProcess affectedProcess, BaseElement affectedElement)
            throws ValidationException {
        String fullMessage;
        Location locationOfWarning;

        if (affectedElement == null) {
            locationOfWarning = new Location(Paths.get(affectedProcess.getBaseURI()), LocationCoordinate.EMPTY);
            fullMessage = String.format("%s: %s", MOJO_MESSAGE_PREFIX, message);
        } else {
            String bpmnId = affectedElement.getId();
            locationOfWarning = affectedProcess.determineLocationByXPath(bpmnId);
            fullMessage = String.format("%s: %s: %s", MOJO_MESSAGE_PREFIX, affectedElement.getClass().getSimpleName(), message);
        }

        return new Warning(fullMessage, locationOfWarning);
    }

    private Warning createMojoWarning(String message, BPMNProcess affectedProcess) throws ValidationException {
        return createMojoWarning(message, affectedProcess, null);
    }

    public enum FindingType {WARNING, VIOLATION}
}
