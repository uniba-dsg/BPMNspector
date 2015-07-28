package de.uniba.dsg.bpmnspector;

import org.junit.Test;

/**
 * @author Matthias Geiger
 */
public class SystemTest {
    @Test
    public void runMainDefaultOptions() {
        String[] args = {"src/test/resources/test-1-gruppe-c.bpmn"};
        BPMNspectorMain.main(args);
    }

    @Test
    public void runMainCheckingFolderInDebugMode() {
        String[] args = {"src/test/resources/001", "-d"};
        BPMNspectorMain.main(args);
    }

    @Test
    public void runMainCheckingFolderCreateXMLReports() {
        String[] args = {"src/test/resources/001 -r XML"};
        BPMNspectorMain.main(args);
    }

}
