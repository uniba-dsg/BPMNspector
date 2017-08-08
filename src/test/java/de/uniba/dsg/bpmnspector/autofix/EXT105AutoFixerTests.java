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

public class EXT105AutoFixerTests {

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
                "EXT.105");
    }

    @Before
    public void setUp() {
        clonedDoc = docToFix.clone();
    }

    @Test
    public void emptyViolationListDoesNotChangeDocument() {
        violationList = Collections.emptyList();
        EXT105AutoFixer fixer = new EXT105AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void otherConstraintViolationShouldNotBeFixed() {
        Violation otherViolation = new Violation(new Location(Paths.get("empty"), LocationCoordinate.EMPTY), "Should not be used", "OTHER");
        violationList = Collections.singletonList(otherViolation);
        EXT105AutoFixer fixer = new EXT105AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void fixingAutoFixViolationChangesProcessExecutabilityToFalse() throws IOException, ValidationException {
        violationList = Collections.singletonList(testViolation);
        EXT105AutoFixer fixer = new EXT105AutoFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());

        DocHandlingHelper.assertValidBPMNspectorResult(clonedDoc);
    }

}
