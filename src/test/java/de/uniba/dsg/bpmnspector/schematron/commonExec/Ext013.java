package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.013
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext013 extends TestCase {

    private static final String ERR_MSG = "According to constraint CARD.064 (see Sec. 3.1) body is a mandatory attribute " +
            "of a FormalExpression. Therefore the element must contain any Expression as a string content.";

    @Test
    public void testConstraintFailTimerEventTimeDateNoBody() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT013_failure_timerEvent_timeDate_noBody.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[contains(@*[local-name()='type'],'tFormalExpression')])[1]", 11);
    }

    @Test
    public void testConstraintFailTransformation() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT013_failure_transformation.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:transformation)[1]", 21);
    }

    @Test
    public void testConstraintSuccessTransformation() throws ValidationException {
        verifyValidResult(createFile("EXT013_success_transformation.bpmn"));
    }

    @Test
    public void testConstraintFailComplexBehaviorDefinition() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT013_failure_complexBehaviorDefinition.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:condition[parent::bpmn:complexBehaviorDefinition])[1]", 15);
    }

    @Test
    public void testConstraintSuccessComplexBehaviorDefinition() throws ValidationException {
        verifyValidResult(createFile("EXT013_success_complexBehaviorDefinition.bpmn"));
    }

    @Test
    public void testConstraintFailCorrelation() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT013_failure_correlation.bpmn"), 2);
        assertViolation(result.getViolations().get(1),
                ERR_MSG,
                "(//bpmn:messagePath)[1]", 9);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:dataPath)[1]", 21);
    }

    @Test
    public void testConstraintSuccessCorrelation() throws ValidationException {
        verifyValidResult(createFile("EXT013_success_correlation.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "013";
    }
}
