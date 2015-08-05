package de.uniba.dsg.bpmnspector;

public enum ReportOption {

    NONE("No report files should be created"),
    XML("create XML reports"),
    HTML("create HTML reports (default)"),
    ALL("create all report types");


    private final String description;

    ReportOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
