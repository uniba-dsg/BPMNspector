package de.uniba.dsg.bpmnspector.common.cli;

import de.uniba.dsg.bpmnspector.ValidationOption;
import de.uniba.dsg.bpmnspector.cli.BPMNspectorCli;
import de.uniba.dsg.bpmnspector.cli.CliException;
import de.uniba.dsg.bpmnspector.cli.CliParameter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBPMNspectorCli {


    private static final String FILE = "file.bpmn";
    private static final String DEBUG = "-d";
    private static final String CHECK = "-c";


    private final BPMNspectorCli cli = new BPMNspectorCli();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testNoArguments() throws CliException {
        exception.expect(CliException.class);
        exception.expectMessage("Invalid usage: No arguments detected. It is only possible to check one file or directory at the same time.");
        String[] args = {};
        cli.parse(args);
    }

    @Test
    public void testTooMuchArguments() throws CliException {
        exception.expect(CliException.class);
        exception.expectMessage("Invalid usage: Too much arguments detected. It is only possible to check one file or directory at the same time.");
        String[] args = {"bla.bpmn", "blubb.bpmn"};
        cli.parse(args);
    }

    @Test
    public void testFileOnly() throws CliException {
        String[] args = {FILE};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
    }
    
    @Test
    public void testFileAndDebug() throws CliException {
        String[] args = {FILE, DEBUG};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, true, expectedCheckOptions);
    }

    @Test
    public void testFileAndSingleCheckOptions() throws CliException {

        for(ValidationOption option : ValidationOption.values()) {
            String[] args = {FILE, CHECK, option.toString()};
            List<ValidationOption> expectedCheckOptions = new ArrayList<>();
            expectedCheckOptions.add(option);

            createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
        }
    }

    @Test
    public void testNoDuplicatesIfAllIsUsed() throws CliException {
        String checks = BPMNspectorCli.CHECK_ALL+","+ValidationOption.EXT.toString()+","+ValidationOption.XSD.toString();
        String[] args = {FILE, CHECK, checks};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
    }

    @Test
    public void testIgnoreInvalidOptionsIfAllIsUsed() throws CliException {
        String checks = BPMNspectorCli.CHECK_ALL+",A_UNKNOWN_OPTION";
        String[] args = {FILE, CHECK, checks};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
    }
    
    private void createAndValidateCliParameter(String[] args, String filename,
            boolean debug, List<ValidationOption> expectedCheckOptions) throws CliException {
        CliParameter params = cli.parse(args);

        assertTrue(params.isDebug() == debug);
        assertEquals(params.getPath(), filename);
        Collections.sort(expectedCheckOptions);
        Collections.sort(params.getValidationOptions());
        assertEquals(expectedCheckOptions, params.getValidationOptions());
    }

}
