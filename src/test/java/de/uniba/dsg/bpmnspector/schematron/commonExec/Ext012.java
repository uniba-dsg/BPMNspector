package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.012
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext012 extends TestCase {

    private static final String ERR_MSG = "If natural-language expressions are used the process is not executable. A " +
            "FormalExpression has to be provided or the process must be marked as isExecutable='false'.";

    @Test
    public void testConstraintFailAdHocSubProcess() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_adHocSubProcess.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:adHocSubProcess[bpmn:completionCondition and ancestor::bpmn:process[@isExecutable='true']][0]", 7);
    }

    @Test
    public void testConstraintSuccessAdHocSubProcess() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_adHocSubProcess.bpmn"));
    }

    @Test
    public void testConstraintSuccessAdHocSubProcessNamespacePrefix() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_adHocSubProcessNamespacePrefix.bpmn"));
    }

    @Test
    public void testConstraintFailAssignment() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_assignment.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:assignment[ancestor::bpmn:process[@isExecutable='true']][0]", 21);
    }

    @Test
    public void testConstraintSuccessAssignment() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_assignment.bpmn"));
    }

    @Test
    public void testConstraintFailComplexGateway() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_complexGateway.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:complexGateway[bpmn:activationCondition and ancestor::bpmn:process[@isExecutable='true']][0]", 18);
    }

    @Test
    public void testConstraintSuccessComplexGateway() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_complexGateway.bpmn"));
    }


    @Test
    public void testConstraintFailMultiInstanceLoopCharacteristicsInvalidCardinality() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_multiInstanceLoopCharacteristics_invalidCardinality.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:multiInstanceLoopCharacteristics[bpmn:loopCardinality and ancestor::bpmn:process[@isExecutable='true']][0]", 10);
    }

    @Test
    public void testConstraintFailMultiInstanceLoopCharacteristicsInvalidCompletionCondition() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_multiInstanceLoopCharacteristics_invalidCompletionCondition.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:multiInstanceLoopCharacteristics[bpmn:completionCondition and ancestor::bpmn:process[@isExecutable='true']][0]", 10);
    }

    @Test
    public void testConstraintSuccessMultiInstanceLoopCharacteristics() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_multiInstanceLoopCharacteristics.bpmn"));
    }

    @Test
    public void testConstraintFailSequenceFlow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_sequenceFlow.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:sequenceFlow[bpmn:conditionExpression and ancestor::bpmn:process[@isExecutable='true']][0]", 16);
    }

    @Test
    public void testConstraintSuccessSequenceFlow() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_sequenceFlow.bpmn"));
    }

    @Test
    public void testConstraintFailStandardLoopCharacteristics() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_standardLoopCharacteristics.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:standardLoopCharacteristics[bpmn:loopCondition and ancestor::bpmn:process[@isExecutable='true']][0]", 10);
    }

    @Test
    public void testConstraintSuccessStandardLoopCharacteristics() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_standardLoopCharacteristics.bpmn"));
    }

    @Test
    public void testConstraintFailTimerEventTimeCycle() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_timerEvent_timeCycle.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:timerEventDefinition[(bpmn:timeDate or bpmn:timeDuration or bpmn:timeCycle) and ancestor::bpmn:process[@isExecutable='true']][0]", 10);
    }

    @Test
    public void testConstraintSuccessTimerEventTimeCycle() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_timerEvent_timeCycle.bpmn"));
    }

    @Test
    public void testConstraintFailTimerEventTimeDate() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_timerEvent_timeDate.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:timerEventDefinition[(bpmn:timeDate or bpmn:timeDuration or bpmn:timeCycle) and ancestor::bpmn:process[@isExecutable='true']][0]", 10);
    }

    @Test
    public void testConstraintSuccessTimerEventTimeDate() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_timerEvent_timeDate.bpmn"));
    }

    @Test
    public void testConstraintFailTimerEventTimeDuration() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT012_failure_timerEvent_timeDuration.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:timerEventDefinition[(bpmn:timeDate or bpmn:timeDuration or bpmn:timeCycle) and ancestor::bpmn:process[@isExecutable='true']][0]", 10);
    }

    @Test
    public void testConstraintSuccessTimerEventTimeDuration() throws ValidationException {
        verifyValidResult(createFile("EXT012_success_timerEvent_timeDuration.bpmn"));
    }
    @Override
    protected String getExtNumber() {
        return "012";
    }
}
