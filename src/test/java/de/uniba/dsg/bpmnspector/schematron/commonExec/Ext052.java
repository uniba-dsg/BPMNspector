package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.048
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext052 extends TestCase {

    private static final String ERR_MSG = "An Item must be referenced which must be declared in referenced Message definition.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT052_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:receiveTask[bpmn:ioSpecification/bpmn:dataOutput][0]", 9);
    }

    @Test
    public void testConstraintFailDifferentItemDefUnequalStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT052_failure_differentItemDef_unequalStructureRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:receiveTask[bpmn:ioSpecification/bpmn:dataOutput][0]", 10);
    }

    @Test
    public void testConstraintSuccessDifferentItemDefEqualStructureRef() throws ValidationException {
        verifyValidResult(createFile("EXT052_success_differentItemDef_equalStructureRef.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "052";
    }
}
