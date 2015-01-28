package de.uniba.dsg.bpmnspector.common.importer;

import org.jdom2.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BPMNProcess {

    private final Document processAsDoc;

    private final String baseURI;
    private final String namespace;
    private final String generatedPrefix;

    private final BPMNProcess parent;

    private final List<BPMNProcess> children = new ArrayList<>();

    private final List<Document> wsdls = new ArrayList<>();
    private final List<Document> xsds = new ArrayList<>();

    public BPMNProcess(Document processAsDoc, String baseURI, String namespace) {
        this(processAsDoc, baseURI, namespace, null);
    }

    public BPMNProcess(Document processAsDoc, String baseURI, String namespace,
            BPMNProcess parent) {
        this.processAsDoc = processAsDoc;
        this.baseURI = baseURI;
        this.namespace = namespace;
        this.parent = parent;
        this.generatedPrefix = createPrefixForProcess();
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

    public String getGeneratedPrefix() {
        return generatedPrefix;
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

    public Optional<BPMNProcess> findProcessByGeneratedPrefix(String prefix) {
        List<BPMNProcess> allProcesses = new ArrayList<>();
        getAllProcessesRecursively(allProcesses);
        return allProcesses.stream().filter(
                process -> process.getGeneratedPrefix().equals(prefix)).findFirst();
    }

    private String createPrefixForProcess() {

        return "ns"+(this.namespace+this.baseURI).hashCode();
    }
}
