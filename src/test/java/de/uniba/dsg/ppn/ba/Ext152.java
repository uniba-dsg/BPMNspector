package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext152 extends TestCase {

    @Test
    public void testConstraintFail1() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_1.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "//bpmn:sequenceFlow[0]", 16);
        Violation v = result.getViolations().get(1);
        assertEquals(
                "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
                v.getMessage());
        assertEquals(
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
                v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintFail2() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_2.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "//bpmn:sequenceFlow[1]", 17);
        Violation v = result.getViolations().get(1);
        assertEquals(
                "If end events are used, all flow nodes must have an outgoing sequence flow",
                v.getMessage());
        assertEquals(
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]",
                v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    private void assertViolation(Violation v, String xpath, int line) {
        assertEquals("A Sequence Flow must not cross the border of a Pool",
                v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "152";
    }
}
