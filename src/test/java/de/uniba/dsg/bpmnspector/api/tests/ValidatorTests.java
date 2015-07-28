package de.uniba.dsg.bpmnspector.api.tests;

import api.ValidationException;
import api.Validator;
import de.uniba.dsg.bpmnspector.BPMNspector;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Matthias Geiger
 */
public class ValidatorTests {

    private final Path path = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test").resolve("resources").resolve("test-1-gruppe-c.bpmn");
    private final Validator validator;

    public ValidatorTests() throws ValidationException {
        validator = new BPMNspector();
    }

    @Test
    public void testValidateMethodPath() throws ValidationException {
        validator.validate(path);
    }

    @Test
    public void testValidateMethodString() throws ValidationException {
        validator.validate(path.toString());
    }
}
