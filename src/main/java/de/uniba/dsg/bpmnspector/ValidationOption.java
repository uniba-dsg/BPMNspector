package de.uniba.dsg.bpmnspector;

public enum ValidationOption {

    XSD("performs an XML schema validation"),
    REF("checks the correctness of references"),
    EXT("checks conformance to EXT rules");


    private final String description;

    ValidationOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
