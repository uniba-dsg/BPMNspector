package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.096
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext096 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A Start Event must not have an incoming sequence flow",
                "//bpmn:startEvent[0]", 4);
    }

    @Override
    protected String getExtNumber() {
        return "096";
    }
}
