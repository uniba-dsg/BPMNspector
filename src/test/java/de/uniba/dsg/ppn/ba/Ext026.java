package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext026 extends TestCase {

    @Test
    public void testConstraintActivityFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_activity.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:task[@default][0]");
    }

    @Test
    public void testConstraintGatewayFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_gateway.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:exclusiveGateway[@default][0]");
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    private void assertViolation(Violation v, String xpath) {
        assertEquals(
                "If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef",
                v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(11, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "026";
    }
}
