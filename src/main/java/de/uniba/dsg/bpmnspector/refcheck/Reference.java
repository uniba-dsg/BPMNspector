package de.uniba.dsg.bpmnspector.refcheck;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This class holds the specification of a reference used to validate against.
 * The information structure is consistent with the 'references.xsd', so that
 * the elements of the 'references.xml' can be represented. A reference is
 * specified through its name and the characteristics: XSD:QName or a XSD:IDREF
 * reference and located as attribute or as sub element. There are possibly also
 * the allowed types of the referenced elements. The special field marks it for
 * additional hard coded handling. The number allows an easy look up in the
 * corresponding bachelor thesis.
 * 
 * @author Andreas Vorndran
 * @version 1.0
 * 
 */
public class Reference {

	private final int number;
	private final String name;
	private final ArrayList<String> types;
	private final boolean qname;
	private final boolean attribute;
	private final boolean special;
	private final Properties language;

	/**
	 * Constructor
	 * 
	 * @param number
	 *            the sequence number used in the bachelor thesis
	 * @param name
	 *            the name of reference
	 * @param types
	 *            a list with the possible referenced types or null if only
	 *            existence check
	 * @param qname
	 *            identifies XSD:QName (true) or XSD:IDREF (false)
	 * @param attribute
	 *            identifies whether the reference is an attribute (true) or an
	 *            child element (false)
	 * @param special
	 *            true if a special handling is needed (more than the normal
	 *            program logic)
	 * @param language
	 *            the reference to the language properties (for the toString
	 *            method)
	 */
	public Reference(int number, String name, ArrayList<String> types,
			boolean qname, boolean attribute, boolean special,
			Properties language) {
		this.number = number;
		this.name = name;
		this.types = types;
		this.qname = qname;
		this.attribute = attribute;
		this.special = special;
		this.language = language;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the types
	 */
	public ArrayList<String> getTypes() {
		return types;
	}

	/**
	 * @return the qname
	 */
	public boolean isQname() {
		return qname;
	}

	/**
	 * @return the attribute
	 */
	public boolean isAttribute() {
		return attribute;
	}

	/**
	 * @return the special
	 */
	public boolean isSpecial() {
		return special;
	}

	@Override
	public String toString() {
		return "{" + language.getProperty("reference.number") + number
				+ language.getProperty("reference.name") + name
				+ language.getProperty("reference.qname") + qname
				+ language.getProperty("reference.attribute") + attribute
				+ language.getProperty("reference.types") + types
				+ language.getProperty("reference.special") + special + "}";
	}
}
