package de.uniba.dsg.ppn.ba.helper;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Namespace Context helper class which sets the namespace uri to the bpmn
 * namespace if the prefix is bpmn
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class BpmnNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
        if ("bpmn".equals(prefix)) {
            return ConstantHelper.BPMNNAMESPACE;
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
