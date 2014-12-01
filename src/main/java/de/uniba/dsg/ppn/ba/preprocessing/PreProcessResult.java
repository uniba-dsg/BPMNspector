package de.uniba.dsg.ppn.ba.preprocessing;

import java.util.Map;

import org.w3c.dom.Document;

/**
 * Just a helper class for avoiding the usage of object arrays and making the
 * code better readable. Is returned as result from preprocessor.preprocess()
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class PreProcessResult {

    private final Document documentResult;
    private final Map<String, String> namespaceTable;

    public PreProcessResult(Document documentResult,
           Map<String, String> namespaceTable) {
        this.documentResult = documentResult;
        this.namespaceTable = namespaceTable;
    }

    public Document getDocumentResult() {
        return documentResult;
    }

    public Map<String, String> getNamespaceTable() {
        return namespaceTable;
    }
}
