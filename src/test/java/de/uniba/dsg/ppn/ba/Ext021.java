package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * Test class for testing Constraint EXT.021
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext021 extends TestCase {

    @Test
    public void testConstraintEventSubProcessFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_sub_process.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][0]", 7);
    }

    @Test
    public void testConstraintEventsSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_events.bpmn"));

    }

    @Test
    public void testConstraintGatewaysSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_gateways.bpmn"));
    }

    @Test
    public void testConstraintTasksSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_tasks.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "021";
    }
}
