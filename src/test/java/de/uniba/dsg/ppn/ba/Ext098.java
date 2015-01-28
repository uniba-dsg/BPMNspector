package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.Violation;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.098
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Ext098 extends TestCase {

    private final static String ERRORMESSAGE = "Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events";
    private final static String XPATHSTRING = "//bpmn:startEvent[parent::bpmn:process][0]";

    @Test
    public void testConstraintCancelFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_cancel.bpmn"), 2);
        assertViolation(result.getViolations().get(0), 4);
        assertViolation(
                result.getViolations().get(1),
                "A cancel EndEvent is only allowed in a transaction sub-process",
                "//bpmn:cancelEventDefinition[0]", 6);
    }

    @Test
    public void testConstraintCompensateFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_compensate.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 4);
    }

    @Test
    public void testConstraintErrorFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_error.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 4);
    }

    @Test
    public void testConstraintEscalationFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_escalation.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 4);
    }

    @Test
    public void testConstraintEscalationRefFail()
            throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_escalation_ref.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 5);
    }

    @Test
    public void testConstraintLinkFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_link.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 4);
    }

    @Test
    public void testConstraintMultipleFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_multiple.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 4);
    }

    @Test
    public void testConstraintTerminateFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_terminate.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 4);
    }

    @Test
    public void testConstraintConditionalSuccess()
            throws ValidatorException {
        verifyValidResult(createFile("success_conditional.bpmn"));
    }

    @Test
    public void testConstraintMessageSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_message.bpmn"));
    }

    @Test
    public void testConstraintMultipleSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_multiple.bpmn"));
    }

    @Test
    public void testConstraintNoneSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_none.bpmn"));
    }

    @Test
    public void testConstraintSignalSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_signal.bpmn"));
    }

    @Test
    public void testConstraintTimerSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_timer.bpmn"));
    }

    private void assertViolation(Violation v, int line) {
        assertViolation(v, XPATHSTRING, line);
    }

    @Override
    protected String getErrorMessage() {
        return ERRORMESSAGE;
    }

    @Override
    protected String getExtNumber() {
        return "098";
    }
}
