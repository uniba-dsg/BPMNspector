package de.uniba.dsg.bpmnspector.autofix;

import api.SimpleValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DocHandlingHelper {

    private static final String PATH_PREFIX = Paths.get(System.getProperty("user.dir"))
            .resolve("src/test/resources").toString();



    public static void assertEqualBPMNProcess(BPMNProcess expected, BPMNProcess actual) {
        if (expected.getBaseURI().equals(actual.getBaseURI()) && expected.getNamespace().equals(actual.getNamespace())) {
            assertEqualDocumentSerialization(expected.getProcessAsDoc(), actual.getProcessAsDoc());
            return;
        }

        fail("Checked BPMNProcesses "+expected+"and "+actual+" are not equal.");
    }

    public static void assertEqualDocumentSerialization(Document expected, Document actual) {
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        String expectedAsString = outputter.outputString(expected);
        String actualAsString = outputter.outputString(actual);
        assertEquals(expectedAsString, actualAsString);
    }

    public static BPMNProcess loadResource(String path) throws ValidationException {
        ProcessImporter importer = new ProcessImporter();

        Path fullPath = Paths.get(PATH_PREFIX + File.separator + path);
        return importer.importProcessFromPath(fullPath, new SimpleValidationResult());
    }

    public static Document loadResourceAsDoc(String path) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new File(PATH_PREFIX + File.separator + path));
    }

}
