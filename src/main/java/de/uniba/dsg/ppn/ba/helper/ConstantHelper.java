package de.uniba.dsg.ppn.ba.helper;

/**
 * This class provides the constants used in the whole project
 *
 * @author Philipp Neugebauer
 *
 */
public class ConstantHelper {

    public final static String PINAMESPACE = "http://www.uniba.de/pi/bpmn-cons/validation";
    public final static String BPMNNAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    public final static String BPMNDINAMESPACE = "http://www.omg.org/spec/BPMN/20100524/DI";
    public final static String FILENOTFOUNDMESSAGE = "file {} couldn't be read.";
    public final static String FILENOTFOUNDMESSAGEWITHCAUSE = String.format(
            "%s%s", FILENOTFOUNDMESSAGE, "Cause: {}");

}
