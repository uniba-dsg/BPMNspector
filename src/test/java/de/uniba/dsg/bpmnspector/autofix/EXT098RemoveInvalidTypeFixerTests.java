package de.uniba.dsg.bpmnspector.autofix;

import api.Location;
import api.LocationCoordinate;
import api.ValidationException;
import api.Violation;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EXT098RemoveInvalidTypeFixerTests {

    private static Document fixedVersionOfDoc;
    private static Document defaultDocToFix;

    private List<Violation> violationList;

    private Document clonedDoc;

    @BeforeClass
    public static void loadDocToFix() throws JDOMException, IOException {
        fixedVersionOfDoc = DocHandlingHelper.loadResourceAsDoc("098/success_none.bpmn");
        defaultDocToFix = DocHandlingHelper.loadResourceAsDoc("098/fail_cancel.bpmn");
    }

    @Before
    public void setUp() {
        clonedDoc = defaultDocToFix.clone();
    }

    @Test
    public void emptyViolationListDoesNotChangeDocument() {
        violationList = Collections.emptyList();
        EXT098RemoveInvalidTypeFixer fixer = new EXT098RemoveInvalidTypeFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(defaultDocToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void otherConstraintViolationShouldNotBeFixed() {
        Violation otherViolation = new Violation(new Location(Paths.get("empty"), LocationCoordinate.EMPTY), "Should not be used", "OTHER");
        violationList = Collections.singletonList(otherViolation);
        EXT098RemoveInvalidTypeFixer fixer = new EXT098RemoveInvalidTypeFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(defaultDocToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void fixingEXT098RemovesInvalidEscalationEventDefFromMultipleEvent() throws IOException, ValidationException, JDOMException {

        Document invalidMultiple = DocHandlingHelper.loadResourceAsDoc("098/fail_multiple.bpmn");
        Document fixedMultiple = DocHandlingHelper.loadResourceAsDoc("098/fail_multiple_manualFix.bpmn");

        violationList = Collections.singletonList(createTestViolation(invalidMultiple));

        EXT098RemoveInvalidTypeFixer fixer = new EXT098RemoveInvalidTypeFixer();

        FixReport report = fixer.fixIssues(invalidMultiple, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(fixedMultiple, invalidMultiple);
        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(invalidMultiple);
    }

    @Test
    public void fixingEXT098RemovesInvalidCancelEventDef() throws IOException, ValidationException, JDOMException {

        Document invalidCancel = DocHandlingHelper.loadResourceAsDoc("098/fail_cancel.bpmn");

        assertCorrectnessOfFix(invalidCancel);
    }

    @Test
    public void fixingEXT098RemovesInvalidCompensateEventDef() throws IOException, ValidationException, JDOMException {

        Document invalidCompensate = DocHandlingHelper.loadResourceAsDoc("098/fail_compensate.bpmn");

        assertCorrectnessOfFix(invalidCompensate);
    }

    @Test
    public void fixingEXT098RemovesInvalidErrorEventDef() throws IOException, ValidationException, JDOMException {

        Document invalidError = DocHandlingHelper.loadResourceAsDoc("098/fail_error.bpmn");

        assertCorrectnessOfFix(invalidError);
    }

    @Test
    public void fixingEXT098RemovesInvalidEscalationEventDef() throws IOException, ValidationException, JDOMException {

        Document invalidEscalation = DocHandlingHelper.loadResourceAsDoc("098/fail_escalation.bpmn");

        assertCorrectnessOfFix(invalidEscalation);
    }

    @Test
    public void fixingEXT098RemovesInvalidLinkEventDef() throws IOException, ValidationException, JDOMException {

        Document invalidLink = DocHandlingHelper.loadResourceAsDoc("098/fail_link.bpmn");

        assertCorrectnessOfFix(invalidLink);
    }

    @Test
    public void fixingEXT098RemovesInvalidTerminateEventDef() throws IOException, ValidationException, JDOMException {

        Document invalidTerminate = DocHandlingHelper.loadResourceAsDoc("098/fail_terminate.bpmn");

        assertCorrectnessOfFix(invalidTerminate);
    }

    private void assertCorrectnessOfFix(Document docToFix) throws IOException, ValidationException {
        violationList = Collections.singletonList(createTestViolation(docToFix));

        EXT098RemoveInvalidTypeFixer fixer = new EXT098RemoveInvalidTypeFixer();

        FixReport report = fixer.fixIssues(docToFix, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(fixedVersionOfDoc, docToFix);
        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(docToFix);
    }


    private Violation createTestViolation(Document docToFix) {
        return new Violation(
                new Location(
                        Paths.get(docToFix.getBaseURI().replace("file:/", "")),
                        new LocationCoordinate(7, 94),
                        "(//bpmn:startEvent)[1]"),
                "msg",
                "EXT.098");
    }
}
