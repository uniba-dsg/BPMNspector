package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.068
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext068 extends TestCase {

    private static final String ERR_MSG = "If a multiInstance task is used in an executable process " +
            "loopDataInputReference must be resolvable to a DataInput defined in the InputOutputSpecification of the Task.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT068_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:multiInstanceLoopCharacteristics[bpmn:loopDataInputRef and not(parent::bpmn:subProcess) and ancestor::bpmn:process[@isExecutable='true']])[1]", 18);
    }

    @Override
    protected String getExtNumber() {
        return "068";
    }
}
