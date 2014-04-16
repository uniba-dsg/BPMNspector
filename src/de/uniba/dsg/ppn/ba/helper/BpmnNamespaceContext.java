package de.uniba.dsg.ppn.ba.helper;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import de.uniba.dsg.ppn.ba.SchematronBPMNValidator;

public class BpmnNamespaceContext implements NamespaceContext {

	@Override
	public String getNamespaceURI(String prefix) {
		if ("bpmn".equals(prefix)) {
			return SchematronBPMNValidator.bpmnNamespace;
		} else if ("xml".equals(prefix)) {
			return XMLConstants.XML_NS_URI;
		}
		return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String uri) {
		return "";
	}

	@Override
	public Iterator<?> getPrefixes(String uri) {
		return null;
	}

}
