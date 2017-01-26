package de.uniba.dsg.bpmnspector;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import api.Location;
import api.LocationCoordinate;
import api.ValidationException;
import api.ValidationResult;
import api.Violation;
import api.Warning;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.mojo.Mojo;
import org.mojo.analysis.information.AnalysisInformation;
import org.mojo.error.AbundanceAnnotation;
import org.mojo.error.Annotation;
import org.mojo.error.DeadlockAnnotation;
import org.mojo.error.EAlarmCategory;
import org.mojo.interpreter.AbstractEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MojoValidator {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MojoValidator.class.getSimpleName());

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
        // List<Annotation> mojoResult = mojo.verify(processAsString, analysisInformation);
        List<Annotation> mojoResult = mojo.verify(new File(process.getBaseURI()), analysisInformation);
        addMojoResultToValidationResult(mojoResult, result, process);
    }

    private void addMojoResultToValidationResult(List<Annotation> mojoResult, ValidationResult validationResult, BPMNProcess baseProcess)
            throws ValidationException {
        if(!mojoResult.isEmpty()) {
            for(Annotation annotation : mojoResult) {
                annotation.printInformation(null);
                System.out.println("Annotation class: "+annotation.getClass()+" AlarmCategory: "+annotation.getAlarmCategory());
                if(annotation instanceof DeadlockAnnotation) {
                    String message = "Mojo: Process contains a deadlock.\nPaths to deadlock:\n";
                    List<List<AbstractEdge>> listOfPaths = ((DeadlockAnnotation) annotation).getListOfFailurePaths();
                    for(List<AbstractEdge> path : listOfPaths) {
                        message += "Path: ";
                        for(int i=0; i<path.size(); i++) {
                            AbstractEdge edge = path.get(i);
                            if(edge.source instanceof BaseElement) {
                                message += edge.source.getClass().getSimpleName()+"["+((BaseElement) edge.source).getId()+"] \u2192 ";
                            }
                        }
                        Object lastTarget = path.get(path.size()-1).target;
                        message += lastTarget.getClass().getSimpleName()+"["+((BaseElement) lastTarget).getId()+"])";
                        message += "\n";
                    }
                    String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
                    Location locationOfViolation = searchForViolationFile(bpmnId, baseProcess);
                    Violation violation = new Violation(locationOfViolation, message, "Deadlock");
                    validationResult.addViolation(violation);
                } else if(annotation instanceof AbundanceAnnotation) {
                    String message = "Mojo: Process contains a lack of synchronization.";
                    String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
                    Location locationOfViolation = searchForViolationFile(bpmnId, baseProcess);
                    Violation violation = new Violation(locationOfViolation, message, "LackOfSync");
                    validationResult.addViolation(violation);
                } else if (EAlarmCategory.ERROR.equals(annotation.getAlarmCategory())) {
                    throw new ValidationException("Unknown error during mojo execution.");
                } else if (EAlarmCategory.WARNING.equals(annotation.getAlarmCategory())) {
                    String bpmnId = ((BaseElement) annotation.getInterpretedPrintableNodes().get(0)).getId();
                    Location locationOfWarning = searchForViolationFile(bpmnId, baseProcess);
                    Warning warning = new Warning(annotation.getDescription(), locationOfWarning);
                    validationResult.addWarning(warning);
                }
            }
        }
    }

    private Location searchForViolationFile(String xpathExpression, BPMNProcess baseProcess)
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
     * @param id
     *            the id, to which the expression should refer
     * @return the xpath expression, which refers the given id
     */
    private static String createIdBpmnExpression(String id) {
        return String.format("//bpmn:*[@id = '%s']", id);
    }
}
