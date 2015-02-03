package de.uniba.dsg.bpmnspector.cli;

import de.uniba.dsg.bpmnspector.ReportOption;
import de.uniba.dsg.bpmnspector.ValidationOption;

import java.util.List;

public class CliParameter {

    private final boolean debug;
    private final List<ValidationOption> validationOptions;
    private final String path;
    private final ReportOption reportOption;

    public CliParameter(String path, List<ValidationOption> validationOptions, boolean debug, ReportOption reportOption) {
        this.path = path;
        this.validationOptions = validationOptions;
        this.debug = debug;
        this.reportOption = reportOption;
    }

    public boolean isDebug() {
        return debug;
    }

    public List<ValidationOption> getValidationOptions() {
        return validationOptions;
    }

    public String getPath() {
        return path;
    }

    public ReportOption getReportOption() {
        return reportOption;
    }
}