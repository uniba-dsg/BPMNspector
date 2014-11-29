package de.uniba.dsg.ppn.ba.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BpmnHelper {

    /**
     * removes all BPMNDiagram Nodes from the given file
     *
     * @param headFileDocument
     *            the document, where the BPMNDiagram Nodes should be deleted
     *            from
     */
    public static void removeBPMNDINode(Document headFileDocument) {
        Element definitionsNode = headFileDocument.getDocumentElement();
        NodeList bpmnDiagramNode = headFileDocument.getElementsByTagNameNS(
                ConstantHelper.BPMNDINAMESPACE, "BPMNDiagram");
        if (bpmnDiagramNode.getLength() > 0) {
            definitionsNode.removeChild(bpmnDiagramNode.item(0));
        }
    }

    /**
     * creates a xpath expression for finding the id
     *
     * @param id
     *            the id, to which the expression should refer
     * @return the xpath expression, which refers the given id
     */
    public static String createIdBpmnExpression(String id) {
        return String.format("//bpmn:*[@id = '%s']", id);
    }
}
