package de.uniba.dsg.bpmnspector;

public enum FixOption {
    NONE("No fixes should be performed (default)"),
    AUTO("all fixable violations will be fixed automatically"),
    INTERACTIVE("ask for each violation");

    private final String description;

    FixOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
