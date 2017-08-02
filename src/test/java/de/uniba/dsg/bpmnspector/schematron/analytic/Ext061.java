package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.061
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext061 extends TestCase {

    private static final String ERR_MSG = "At least one Activity must be contained in an AdHocSubProcess.";


    @Test
    public void testConstraintFailEmptyAdHoc() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT061_fail_empty_AdHoc.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:adHocSubProcess)[1]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT061_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "061";
    }
}
