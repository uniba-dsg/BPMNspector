package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.011
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext011 extends TestCase {

    private static final String ERR_MSG = "An escalationCode must be present if the escalation is used in an EndEvent or " +
            "in an intermediate throw Event if the trigger is an Escalation.";

    @Test
    public void testConstraintFailEndEventNoRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT011_failure_endEventNoRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:endEvent[bpmn:escalationEventDefinition and ancestor::bpmn:process[@isExecutable='true']])[1]", 7);
    }

    @Test
    public void testConstraintFailEndEventNoEscCode() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT011_failure_endEventNoEscCode.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:endEvent[bpmn:escalationEventDefinition and ancestor::bpmn:process[@isExecutable='true']])[1]", 7);
    }

    @Test
    public void testConstraintFailIntermediateThrowNoEscCode() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT011_failure_intermediateThrowNoEscCode.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:intermediateThrowEvent[bpmn:escalationEventDefinition and ancestor::bpmn:process[@isExecutable='true']])[1]", 10);
    }

    @Test
    public void testConstraintSuccessEndEvent() throws ValidationException {
        verifyValidResult(createFile("EXT011_success_endEvent.bpmn"));
    }

    @Test
    public void testConstraintSuccessIntermediateThrow() throws ValidationException {
        verifyValidResult(createFile("EXT011_success_intermediateThrow.bpmn"));
    }

    @Test
    public void testConstraintSuccessBoundaryNoEscCode() throws ValidationException {
        verifyValidResult(createFile("EXT011_success_boundaryNoEscCode.bpmn"));
    }

    @Test
    public void testConstraintSuccessNotExecutable() throws ValidationException {
        verifyValidResult(createFile("EXT011_success_notExecutable.bpmn"));
    }


    @Override
    protected String getExtNumber() {
        return "011";
    }
}
