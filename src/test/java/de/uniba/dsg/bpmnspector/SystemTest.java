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
}
