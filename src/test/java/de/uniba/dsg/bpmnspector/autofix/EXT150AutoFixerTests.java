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

public class EXT150AutoFixerTests {

    private static Document docToFix;
    private static Violation testViolation;

    private List<Violation> violationList;

    private Document clonedDoc;

    @BeforeClass
    public static void loadDocToFix() throws JDOMException, IOException {
        docToFix = DocHandlingHelper.loadResourceAsDoc("105/fail_end_without_sub-events.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(docToFix.getBaseURI().replace("file:/", "")),
                        new LocationCoordinate(11, 7),
                        "(//bpmn:startEvent)[1]"),
                "msg",
                "EXT.150");
    }

    @Before
    public void setUp() {
        clonedDoc = docToFix.clone();
    }

    @Test
    public void emptyViolationListDoesNotChangeDocument() {
        violationList = Collections.emptyList();
        EXT150AutoFixer fixer = new EXT150AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void otherConstraintViolationShouldNotBeFixed() {
        Violation otherViolation = new Violation(new Location(Paths.get("empty"), LocationCoordinate.EMPTY), "Should not be used", "OTHER");
        violationList = Collections.singletonList(otherViolation);
        EXT150AutoFixer fixer = new EXT150AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void fixAddsParallelGatewayToExistingStartEventAndConnectsUnconncetedSubProcess() throws IOException, ValidationException, JDOMException {
        Document doc = DocHandlingHelper.loadResourceAsDoc("150/fail_normal_sequence_flow_missing_2.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(doc.getBaseURI().replace("file:/", "")),
                        LocationCoordinate.EMPTY,
                        "(//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent])[1]"),
                "msg",
                "EXT.150");
        violationList = Collections.singletonList(testViolation);


        EXT150AutoFixer fixer = new EXT150AutoFixer();

        FixReport report = fixer.fixIssues(doc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(doc);
    }

    @Test
    public void fixAddsParallelGatewayToExistingStartEventAndConnectsToUnconncetedTask() throws IOException, ValidationException, JDOMException {
        Document doc = DocHandlingHelper.loadResourceAsDoc("150/fail_normal_sequence_flow_missing_1.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(doc.getBaseURI().replace("file:/", "")),
                        LocationCoordinate.EMPTY,
                        "(//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent])[5]"),
                "msg",
                "EXT.150");
        violationList = Collections.singletonList(testViolation);


        EXT150AutoFixer fixer = new EXT150AutoFixer();

        FixReport report = fixer.fixIssues(doc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(doc);
    }

    @Test
    public void fixAddsParallelGatewayToExistingStartEventAndConnectsToUnconncetedTaskInSubProcess() throws IOException, ValidationException, JDOMException {
        Document doc = DocHandlingHelper.loadResourceAsDoc("150/fail_sequence_flow_in_sub_process_missing_1.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(doc.getBaseURI().replace("file:/", "")),
                        LocationCoordinate.EMPTY,
                        "(//bpmn:serviceTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent])[1]"),
                "msg",
                "EXT.150");
        violationList = Collections.singletonList(testViolation);


        EXT150AutoFixer fixer = new EXT150AutoFixer();

        FixReport report = fixer.fixIssues(doc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(doc);
    }

}
