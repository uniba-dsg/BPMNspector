package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.059
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext059 extends TestCase {

    private static final String ERR_MSG_DEFAULT = "An Event Sub-Process MUST define at least one of the following EventDefinitions: " +
            "messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition, " +
            "conditionalEventDefinition, signalEventDefinition.";
    private static final String ERR_CANCEL = " A cancelEventDefinition is not allowed for Event Sub-Processes.";
    private static final String ERR_LINK = " A linkEventDefinition is not allowed for Event Sub-Processes.";
    private static final String ERR_TERMINATE = " A terminateEventDefinition is not allowed for Event Sub-Processes.";
    private static final String ERR_NONINTERRUPTING_COMPENSATE = " Moreover, a compensateEventDefinition is not allowed for Non-Interrupting StartEvents.";
    private static final String ERR_NONINTERRUPTING_ERROR = " Moreover, an errorEventDefinition is not allowed for Non-Interrupting StartEvents.";



    @Test
    public void testConstraintFailNoEventDef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT059_fail_no_eventDef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_DEFAULT,
                "(//bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent)[1]", 28);
    }

    @Test
    public void testConstraintFailIllegalCancelEventDef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT059_fail_illegal_cancelEventDef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_DEFAULT+ERR_CANCEL,
                "(//bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent)[1]", 28);
    }

    @Test
    public void testConstraintFailIllegalLinkEventDef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT059_fail_illegal_linkEventDef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_DEFAULT+ERR_LINK,
                "(//bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent)[1]", 28);
    }

    @Test
    public void testConstraintFailIllegalTerminateEventDef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT059_fail_illegal_terminateEventDef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_DEFAULT+ERR_TERMINATE,
                "(//bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent)[1]", 28);
    }

    @Test
    public void testConstraintFailNonInterruptingIllegalCompensateEventDef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT059_fail_nonInterrupting_illegal_compensateEventDef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_DEFAULT+ERR_NONINTERRUPTING_COMPENSATE,
                "(//bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent[@isInterrupting='false'])[1]", 28);
    }

    @Test
    public void testConstraintFailNonInterruptingIllegalErrorEventDef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT059_fail_nonInterrupting_illegal_errorEventDef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_DEFAULT+ERR_NONINTERRUPTING_ERROR,
                "(//bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent[@isInterrupting='false'])[1]", 28);
    }

    @Test
    public void testConstraintSuccessInterruptingErrorEventDef() throws ValidationException {
        verifyValidResult(createFile("EXT059_success_interrupting_errorEventDef.bpmn"));
    }

    @Test
    public void testConstraintSuccessInterruptingCompensateEventDef() throws ValidationException {
        verifyValidResult(createFile("EXT059_success_interrupting_compensateEventDef.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "059";
    }
}
