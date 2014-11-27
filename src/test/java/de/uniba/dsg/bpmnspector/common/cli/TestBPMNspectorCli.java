package de.uniba.dsg.bpmnspector.common.cli;

import de.uniba.dsg.bpmnspector.ValidationOption;
import de.uniba.dsg.bpmnspector.cli.BPMNspectorCli;
import de.uniba.dsg.bpmnspector.cli.CliParameter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestBPMNspectorCli {


    private static final String FILE = "file.bpmn";
    private static final String DEBUG = "-d";
    private static final String CHECK = "-c";


    private final BPMNspectorCli cli = new BPMNspectorCli();


    @Test
    public void testFileOnly() {
        String[] args = {FILE};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
    }
    
    @Test
    public void testFileAndDebug() {
        String[] args = {FILE, DEBUG};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, true, expectedCheckOptions);
    }

    @Test
    public void testFileAndSingleCheckOptions() {

        for(ValidationOption option : ValidationOption.values()) {
            String[] args = {FILE, CHECK, option.toString()};
            List<ValidationOption> expectedCheckOptions = new ArrayList<>();
            expectedCheckOptions.add(option);

            createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
        }
    }

    @Test
    public void testNoDuplicatesIfAllIsUsed() {
        String checks = BPMNspectorCli.CHECK_ALL+","+ValidationOption.EXT.toString()+","+ValidationOption.XSD.toString();
        String[] args = {FILE, CHECK, checks};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
    }

    @Test
    public void testIgnoreInvalidOptionsIfAllIsUsed() {
        String checks = BPMNspectorCli.CHECK_ALL+",A_UNKNOWN_OPTION";
        String[] args = {FILE, CHECK, checks};

        List<ValidationOption> expectedCheckOptions = Arrays.asList(ValidationOption.values());

        createAndValidateCliParameter(args, FILE, false, expectedCheckOptions);
    }
    
    private void createAndValidateCliParameter(String[] args, String filename,
            boolean debug, List<ValidationOption> expectedCheckOptions)  {
        CliParameter params = cli.parse(args);

        assertTrue(params.isDebug() == debug);
        assertEquals(params.getPath(), filename);
        Collections.sort(expectedCheckOptions);
        Collections.sort(params.getValidationOptions());
        assertEquals(expectedCheckOptions, params.getValidationOptions());
    }

}
