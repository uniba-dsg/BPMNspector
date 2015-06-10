package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.087
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext087 extends TestCase {

    private static final String ERR_MSG = "If an inputSet references an outputSet using the outputSetRefs element it " +
            "must be referenced by the outputSet using the inputSetRefs element and vice versa.";

    @Test
    public void testConstraintFailInvalidReferencing() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT087_failure_invalidReferencing.bpmn"), 4);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:inputSet[bpmn:outputSetRefs][0]", 8);
    }

    @Test
    public void testConstraintFailMissingInputSetRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT087_failure_missingInputSetRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:inputSet[bpmn:outputSetRefs][0]", 7);
    }

    @Test
    public void testConstraintFailMissingOutputSetRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT087_failure_missingOutputSetRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:outputSet[bpmn:inputSetRefs][0]", 10);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT087_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "087";
    }
}
