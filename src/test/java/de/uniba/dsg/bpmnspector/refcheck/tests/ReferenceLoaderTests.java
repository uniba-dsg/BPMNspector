package de.uniba.dsg.bpmnspector.refcheck.tests;

import api.ValidationException;
import de.uniba.dsg.bpmnspector.refcheck.BPMNElement;
import de.uniba.dsg.bpmnspector.refcheck.Reference;
import de.uniba.dsg.bpmnspector.refcheck.ReferenceLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class ReferenceLoaderTests {
    private static ReferenceLoader referenceLoader = new ReferenceLoader();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void loadAllReferences() throws ValidationException {
        referenceLoader.load("/references.xml", "/references.xsd");
    }

    @Test
    public void loadNonExistentFile() throws ValidationException {
        exception.expect(ValidationException.class);
        exception.expectMessage("Problems occurred while traversing the file 'NON_EXISTING'");
        referenceLoader.load("NON_EXISTING", "/references.xsd");
    }

    @Test
    public void useNonExistentXsd() throws ValidationException {
        exception.expect(ValidationException.class);
        exception.expectMessage("Problems occurred while trying to check the references XML file against the corresponding XSD file.");
        referenceLoader.load("/references.xml", "NON_EXISTING");
    }


    @Test
    public void testCorrectnessAssociation() throws ValidationException {
        Map<String, BPMNElement> refs = referenceLoader.load("/references.xml", "/references.xsd");
        assertTrue(refs.containsKey("association"));
        List<Reference> referenceList = refs.get("association").getReferences();
        assertTrue(referenceList.size()==2);
        assertTrue(referenceList.get(0).getName().equals("sourceRef"));
        assertTrue(referenceList.get(0).isAttribute());
        assertTrue(referenceList.get(0).isQname());
        assertTrue(referenceList.get(1).getName().equals("targetRef"));
        assertTrue(referenceList.get(1).isAttribute());
        assertTrue(referenceList.get(1).isQname());
    }

    @Test
    public void testCorrectnessDataObject() throws ValidationException {
        Map<String, BPMNElement> refs = referenceLoader.load("/references.xml", "/references.xsd");
        assertTrue(refs.containsKey("dataObject"));
        List<Reference> referenceList = refs.get("dataObject").getReferences();
        assertTrue(referenceList.size()==2);
        assertTrue(referenceList.get(0).getName().equals("categoryValueRef"));
        assertFalse(referenceList.get(0).isAttribute());
        assertTrue(referenceList.get(0).isQname());
        assertTrue(referenceList.get(1).getName().equals("itemSubjectRef"));
        assertTrue(referenceList.get(1).isAttribute());
        assertTrue(referenceList.get(1).isQname());
    }

    @Test
    public void testCorrectnessGateway() throws ValidationException {
        Map<String, BPMNElement> refs = referenceLoader.load("/references.xml", "/references.xsd");
        assertTrue(refs.containsKey("gateway"));
        List<Reference> referenceList = refs.get("gateway").getReferences();
        assertTrue(referenceList.size()==3);
        assertTrue(referenceList.get(0).getName().equals("categoryValueRef"));
        assertFalse(referenceList.get(0).isAttribute());
        assertTrue(referenceList.get(0).isQname());
        assertTrue(referenceList.get(1).getName().equals("incoming"));
        assertFalse(referenceList.get(1).isAttribute());
        assertTrue(referenceList.get(1).isQname());
        assertTrue(referenceList.get(2).getName().equals("outgoing"));
        assertFalse(referenceList.get(2).isAttribute());
        assertTrue(referenceList.get(2).isQname());
    }



}
