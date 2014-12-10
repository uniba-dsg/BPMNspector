package de.uniba.dsg.bpmnspector.common.importer;

import org.jdom2.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BPMNProcess {

    private final Document processAsDoc;

    private final String baseURI;
    private final String namespace;

    private final BPMNProcess parent;

    private List<BPMNProcess> children = new ArrayList<>();

    private List<Document> wsdls = new ArrayList<>();
    private List<Document> xsds = new ArrayList<>();

    public BPMNProcess(Document processAsDoc, String baseURI, String namespace) {
        this.processAsDoc = processAsDoc;
        this.baseURI = baseURI;
        this.namespace = namespace;
        this.parent = null;
    }

    public BPMNProcess(Document processAsDoc, String baseURI, String namespace,
            BPMNProcess parent) {
        this.processAsDoc = processAsDoc;
        this.baseURI = baseURI;
        this.namespace = namespace;
        this.parent = parent;
    }

    public List<BPMNProcess> getChildren() {
        return children;
    }

    public Document getProcessAsDoc() {
        return processAsDoc;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public String getNamespace() {
        return namespace;
    }

    public BPMNProcess getParent() {
        return parent;
    }

    public List<Document> getWsdls() {
        return wsdls;
    }

    public List<Document> getXsds() {
        return xsds;
    }

    public void getAllProcessesRecursively(List<BPMNProcess> processList) {
        if(!processList.contains(this)) {
            processList.add(this);
            for(BPMNProcess child : this.getChildren()) {
                child.getAllProcessesRecursively(processList);
            }
        }
    }

    public List<BPMNProcess> findProcessByNamespace(String namespace) {
        List<BPMNProcess> allProcesses = new ArrayList<>();
        getAllProcessesRecursively(allProcesses);
        return allProcesses.stream().filter(
                process -> process.getNamespace().equals(namespace)).collect(
                Collectors.toList());
    }
}
