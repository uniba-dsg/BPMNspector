package de.uniba.dsg.bpmnspector.autofix;

import api.Location;
import api.LocationCoordinate;
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

public class EXT012FirstOptionNotExecutableFixerTests {
    private static Document docToFix;
    private static Document fixedVersionOfDoc;
    private static Violation testViolation;

    private List<Violation> violationList;

    private Document clonedDoc;

    @BeforeClass
    public static void loadDocToFix() throws JDOMException, IOException {
        docToFix = DocHandlingHelper.loadResourceAsDoc("012/EXT012_failure_sequenceFlow.bpmn");
        fixedVersionOfDoc = DocHandlingHelper.loadResourceAsDoc("012/EXT012_failure_sequenceFlow_manualFix.bpmn");
        testViolation = new Violation(
                new Location(
                        Paths.get(docToFix.getBaseURI().replace("file:/", "")),
                        new LocationCoordinate(11, 7),
                        "(//bpmn:sequenceFlow[bpmn:conditionExpression and ancestor::bpmn:process[@isExecutable='true']])[1]"),
                "msg",
                "EXT.012");
    }

    @Before
    public void setUp() {
        clonedDoc = docToFix.clone();
    }

    @Test
    public void emptyViolationListDoesNotChangeDocument() {
        violationList = Collections.emptyList();
        EXT012FirstOptionNotExecutableFixer fixer = new EXT012FirstOptionNotExecutableFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void otherConstraintViolationShouldNotBeFixed() {
        Violation otherViolation = new Violation(new Location(Paths.get("empty"), LocationCoordinate.EMPTY), "Should not be used", "OTHER");
        violationList = Collections.singletonList(otherViolation);
        EXT012FirstOptionNotExecutableFixer fixer = new EXT012FirstOptionNotExecutableFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(docToFix, clonedDoc);
        assertFalse(report.violationsHaveBeenFixed());
    }

    @Test
    public void fixingAutoFixViolationChangesProcessExecutabilityToFalse() {
        violationList = Collections.singletonList(testViolation);
        EXT012FirstOptionNotExecutableFixer fixer = new EXT012FirstOptionNotExecutableFixer();

        FixReport report = fixer.fixIssues(clonedDoc, violationList);

        DocHandlingHelper.assertEqualDocumentSerialization(fixedVersionOfDoc, clonedDoc);
        assertTrue(report.violationsHaveBeenFixed());
        assertEquals(violationList, report.getFixedViolations());
    }
}
