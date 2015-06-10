package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.093
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext093 extends TestCase {

    @Test
    public void testConstraintFailBoundaryEventMissingDataOutput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT093_failure_boundaryEvent_missingDataOutput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "If dataOutputs are used in a BoundaryEvent for each eventDefinition a DataInput must be defined.",
                "//bpmn:boundaryEvent[bpmn:dataOutput][0]", 21);
    }

    @Test
    public void testConstraintFailEndEventMissingDataInput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT093_failure_endEvent_missingDataInput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "If dataInputs are used in an EndEvent for each eventDefinition a DataInput must be defined.",
                "//bpmn:endEvent[bpmn:dataInput][0]", 15);
    }

    @Test
    public void testConstraintFailIntCatchMissingDataOutput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT093_failure_intCatch_missingDataOutput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "If dataOutputs are used in an IntermediateCatchEvent for each eventDefinition a DataInput must be defined.",
                "//bpmn:intermediateCatchEvent[bpmn:dataOutput][0]", 10);
    }

    @Test
    public void testConstraintFailIntThrowMissingDataInput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT093_failure_intThrow_missingDataInput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "If dataInputs are used in an intermediateThrowEvent for each eventDefinition a DataInput must be defined.",
                "//bpmn:intermediateThrowEvent[bpmn:dataInput][0]", 10);
    }

    @Test
    public void testConstraintFailStartEventMissingDataOutput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT093_failure_startEvent_missingDataOutput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "If dataOutputs are used in a StartEvent for each eventDefinition a DataInput must be defined.",
                "//bpmn:startEvent[bpmn:dataOutput][0]", 7);
    }

    @Test
    public void testConstraintSuccessBoundaryEvent() throws ValidationException {
        verifyValidResult(createFile("EXT093_success_boundaryEvent.bpmn"));
    }

    @Test
    public void testConstraintSuccessEndEvent() throws ValidationException {
        verifyValidResult(createFile("EXT093_success_endEvent.bpmn"));
    }

    @Test
    public void testConstraintSuccessIntCatchw() throws ValidationException {
        verifyValidResult(createFile("EXT093_success_intCatch.bpmn"));
    }

    @Test
    public void testConstraintSuccessIntThrow() throws ValidationException {
        verifyValidResult(createFile("EXT093_success_intThrow.bpmn"));
    }

    @Test
    public void testConstraintSuccessStartEvent() throws ValidationException {
        verifyValidResult(createFile("EXT093_success_startEvent.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "093";
    }
}
