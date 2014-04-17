package de.uniba.dsg.ppn.ba.preprocessing;

import java.util.List;

import org.w3c.dom.Document;

public class PreProcessResult {

	private Document documentResult;
	private List<String[]> namespaceTable;

	public PreProcessResult(Document documentResult,
			List<String[]> namespaceTable) {
		this.documentResult = documentResult;
		this.namespaceTable = namespaceTable;
	}

	public Document getDocumentResult() {
		return documentResult;
	}

	public void setDocumentResult(Document documentResult) {
		this.documentResult = documentResult;
	}

	public List<String[]> getNamespaceTable() {
		return namespaceTable;
	}

	public void setNamespaceTable(List<String[]> namespaceTable) {
		this.namespaceTable = namespaceTable;
	}

}
