package de.uniba.dsg.bpmnspector.refcheck;

import java.util.List;

/**
 * This class represents a BPMN element, which has one or more checkable
 * references. It is also embedded in the BPMN element hierarchy. For that it
 * has maybe a parent element (represented here by its name) and maybe children
 * (also represented by their names). The information structure is consistent
 * with the 'references.xsd', so that the elements of the 'references.xml' can
 * be represented.
 * 
 * @author Andreas Vorndran
 * @version 1.0
 * 
 */
public class BPMNElement {

	private final String name;
	private final String parent;
	private final List<String> children;
	private final List<Reference> references;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of this element
	 * @param parent
	 *            the name of a parent element or null
	 * @param children
	 *            the children element names of this element or null
	 * @param references
	 *            the references of this element
	 */
	public BPMNElement(String name, String parent, List<String> children,
			List<Reference> references) {
		this.name = name;
		this.parent = parent;
		this.children = children;
		this.references = references;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @return the children
	 */
	public List<String> getChildren() {
		return children;
	}

	/**
	 * @return the references
	 */
	public List<Reference> getReferences() {
		return references;
	}

	@Override
	public String toString() {
		return String.format("<%s ; Possible referencing attribs/elems: %s >", name, references);
	}
}
