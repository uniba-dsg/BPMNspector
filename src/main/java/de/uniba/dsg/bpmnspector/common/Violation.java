package de.uniba.dsg.bpmnspector.common;

import de.uniba.dsg.ppn.ba.helper.ConstantHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Basic class representing a single Violation of a BPMN constraint.
 *
 * Each {@code Violation} is defined by four attributes to describe the
 * violation clearly:
 *
 * <ul>
 * <li>constraint - key of the violated constraint (e.g. EXT.032, REF.100,...)</li>
 * <li>fileName - the name of the affected file</li>
 * <li>line - the line number in the file where the violation occured</li>
 * <li>xPath - an XPath expression which can be used to navigate to the affected
 * element.</li>
 * </ul>
 *
 * Moreover, the attribute message can be used to provide a description of the
 * violation.
 */
@XmlType(namespace = ConstantHelper.PINAMESPACE)
public class Violation {

    @XmlAttribute(required = true)
    private final String constraint;

    @XmlAttribute(required = true)
    private final String fileName;

    @XmlAttribute
    private final int line;

    @XmlElement(namespace = ConstantHelper.PINAMESPACE)
    private final String xPath;

    @XmlElement(namespace = ConstantHelper.PINAMESPACE)
    private final String message;

    /**
     * Constructor
     *
     * @param constraint
     *            id of the failed constraint
     * @param fileName
     *            name of the affected file
     * @param line
     *            line number
     * @param xPath
     *            XPath expression to the affected element/attribute
     * @param message
     *            description of the violation
     */
    public Violation(String constraint, String fileName, int line,
            String xPath, String message) {
        this.constraint = constraint;
        this.fileName = fileName;
        this.line = line;
        this.xPath = xPath;
        this.message = message;
    }

    /**
     * @return the affected constraint
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     * @return affected file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return affected line
     */
    public int getLine() {
        return line;
    }

    /**
     * @return XPath expression to determine affected element/attribute
     */
    public String getxPath() {
        return xPath;
    }

    /**
     * @return message to describe the violation
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(200);
        stringBuilder.append(String.format("[%s, %s]: Violation of %s",
                fileName, line, constraint));

        if (message != null && !message.isEmpty()) {
            stringBuilder.append(String.format(": %s", message));
        }
        if (xPath != null && !xPath.isEmpty()) {
            stringBuilder.append(String.format("(XPath: %s)", xPath));
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if(object == this) {
            return true;
        } else if(object instanceof Violation) {
            Violation other = (Violation) object;
            if(other.constraint.equals(this.constraint) &&
                    other.fileName.equals(this.fileName) &&
                    other.message.equals(this.message) &&
                    other.line == this.line) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return constraint.hashCode()+fileName.hashCode()+message.hashCode()+line;
    }
}
