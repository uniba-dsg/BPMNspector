package de.uniba.dsg.bpmnspector.refcheck;

import java.util.List;

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
 * @author Matthias Geiger
 * @version 1.0
 * 
 */
public class Reference {

	private final int number;
	private final String name;
	private final List<String> types;
	private final boolean qname;
	private final boolean attribute;

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
	 */
	public Reference(int number, String name, List<String> types,
			boolean qname, boolean attribute) {
		this.number = number;
		this.name = name;
		this.types = types;
		this.qname = qname;
		this.attribute = attribute;
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
	public List<String> getTypes() {
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

	@Override
	public String toString() {
		return String.format("{ #%d name: %s isQname? %b isAttribute? %b allowed Types: [%s]",
				number, name, qname, attribute, types);
	}
}
