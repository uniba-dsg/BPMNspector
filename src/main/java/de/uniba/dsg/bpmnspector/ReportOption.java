package de.uniba.dsg.bpmnspector;

public enum ReportOption {

    NONE("No report file should be created"),
    XML("create XML reports"),
    HTML("create HTML reports (default)"),
    ALL("create both report types");


    private final String description;

    ReportOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
