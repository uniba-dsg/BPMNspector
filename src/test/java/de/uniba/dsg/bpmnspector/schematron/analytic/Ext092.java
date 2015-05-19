package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.092
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext092 extends TestCase {

    private static final String ERR_MSG = "If a DataAssociation does not define a Transformation, only a single sourceRef is allowed.";


    @Test
    public void testConstraintFailDifferentStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT092_failure_twoSourceRefs.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:dataInputAssociation[not(./bpmn:transformation)][0]", 13);
    }


    @Test
    public void testConstraintSuccessWithTransformation() throws ValidationException {
        verifyValidResult(createFile("EXT092_success_withTransformation.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "092";
    }
}
