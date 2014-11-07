package de.uniba.dsg.ppn.ba.helper;

/**
 * Used to have validator exception messages if anything fails during validation
 *
 * @author Philipp Neugebauer
 * @version 1.0
 */
public class BpmnValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public BpmnValidationException(final String text) {
        super(text);
    }
}
