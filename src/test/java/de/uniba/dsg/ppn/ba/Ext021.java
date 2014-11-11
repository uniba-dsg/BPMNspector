package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext021 extends TestCase {

    @Test
    public void testConstraintEventSubProcessFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_sub_process.bpmn"), 1);
        Violation v = result.getViolations().get(0);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][0]",
                v.getxPath());
        assertEquals(7, v.getLine());
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
