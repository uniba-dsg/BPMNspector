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

public class EXT151AutoFixerTests {

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
                "EXT.151");
    }

    @Before
    public void setUp() {
        clonedDoc = docToFix.clone();
    }

    @Test
    public void emptyViolationListDoesNotChangeDocument() {
        violationList = Collections.emptyList();
        EXT151AutoFixer fixer = new EXT151AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void otherConstraintViolationShouldNotBeFixed() {
        Violation otherViolation = new Violation(new Location(Paths.get("empty"), LocationCoordinate.EMPTY), "Should not be used", "OTHER");
        violationList = Collections.singletonList(otherViolation);
        EXT151AutoFixer fixer = new EXT151AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void fixAddsAdditionalEndElementToUnconncetedSubProcess() throws IOException, ValidationException, JDOMException {
        Document doc = DocHandlingHelper.loadResourceAsDoc("151/fail_normal_sequence_flow_missing_1.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(doc.getBaseURI().replace("file:/", "")),
                        LocationCoordinate.EMPTY,
                        "(//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent])[1]"),
                "msg",
                "EXT.151");
        violationList = Collections.singletonList(testViolation);


        EXT151AutoFixer fixer = new EXT151AutoFixer();

        FixReport report = fixer.fixIssues(doc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(doc);
    }

    @Test
    public void fixAddsAdditionalEndElementToUnconncetedTask() throws IOException, ValidationException, JDOMException {
        Document doc = DocHandlingHelper.loadResourceAsDoc("151/fail_normal_sequence_flow_missing_2.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(doc.getBaseURI().replace("file:/", "")),
                        LocationCoordinate.EMPTY,
                        "(//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent])[4]"),
                "msg",
                "EXT.151");
        violationList = Collections.singletonList(testViolation);


        EXT151AutoFixer fixer = new EXT151AutoFixer();

        FixReport report = fixer.fixIssues(doc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(doc);
    }

    @Test
    public void fixAddsAdditionalEndElementToUnconncetedTaskInSubProcess() throws IOException, ValidationException, JDOMException {
        Document doc = DocHandlingHelper.loadResourceAsDoc("151/fail_sequence_flow_in_sub_process_missing_1.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(doc.getBaseURI().replace("file:/", "")),
                        LocationCoordinate.EMPTY,
                        "(//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent])[1]"),
                "msg",
                "EXT.151");
        violationList = Collections.singletonList(testViolation);


        EXT151AutoFixer fixer = new EXT151AutoFixer();

        FixReport report = fixer.fixIssues(doc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(doc);
    }

}
