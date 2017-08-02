package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.*;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for testing Constraint EXT.076
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext076 extends TestCase {

    @Test
    public void testConstraintFail1() throws ValidationException {

        List<Warning> expectedWarnings = new ArrayList<>();
        expectedWarnings.add(new Warning(getErrorMessage(), new Location(createFile("Fail_1.bpmn").toPath(),
                new LocationCoordinate(5,79), "(//bpmn:dataObjectReference[@name])[1]")));

        ValidationResult result = validator.validate(createFile("Fail_1.bpmn"));

        assertTrue(result.isValid());
        assertEquals(expectedWarnings, result.getWarnings());
    }

    @Test
    public void testConstraintFail2() throws ValidationException {
        List<Warning> expectedWarnings = new ArrayList<>();
        expectedWarnings.add(new Warning(getErrorMessage(), new Location(createFile("Fail_2.bpmn").toPath(),
                new LocationCoordinate(5,88), "(//bpmn:dataObjectReference[@name])[1]")));

        ValidationResult result = validator.validate(createFile("Fail_2.bpmn"));

        assertTrue(result.isValid());
        assertEquals(expectedWarnings, result.getWarnings());
    }

    @Test
    public void testConstraintFail3() throws ValidationException {
        List<Warning> expectedWarnings = new ArrayList<>();
        expectedWarnings.add(new Warning(getErrorMessage(), new Location(createFile("Fail_3.bpmn").toPath(),
                new LocationCoordinate(5,74), "(//bpmn:dataObjectReference[@name])[1]")));

        ValidationResult result = validator.validate(createFile("Fail_3.bpmn"));

        assertTrue(result.isValid());
        assertEquals(expectedWarnings, result.getWarnings());
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        assertValidValidationResultForFile("Success.bpmn");
    }

    @Override
    protected String getErrorMessage() {
        return "Naming Convention: name = Data Object Name [Data Object Reference State]";
    }

    @Override
    protected String getExtNumber() {
        return "076";
    }
}
