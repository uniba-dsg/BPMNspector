package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.139
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext139 extends TestCase {

    private static final String ERR_MSG = "If Message Intermediate Events are used in the configuration, then Receive " +
            "Tasks MUST NOT be used in that configuration and vice versa.";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT139_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:eventBasedGateway[bpmn:outgoing=//bpmn:sequenceFlow[@targetRef=//bpmn:receiveTask/@id]/@id][0]", 4);
    }

    @Override
    protected String getExtNumber() {
        return "139";
    }
}
