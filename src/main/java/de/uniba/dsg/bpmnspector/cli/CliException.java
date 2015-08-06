package de.uniba.dsg.bpmnspector.cli;

/**
 * @author Matthias Geiger
 */
public class CliException extends Exception {

    public CliException(String message) {
        super(message);
    }

    public CliException(String message, Throwable cause) {
        super(message, cause);
    }
}
