package de.uniba.dsg.bpmnspector.cli;

import de.uniba.dsg.bpmnspector.ReportOption;
import de.uniba.dsg.bpmnspector.ValidationOption;

import java.util.Collections;
import java.util.List;

public interface CliParameter {



    default boolean isDebug() {
        return false;
    }

    default List<ValidationOption> getValidationOptions() {
        return Collections.emptyList();
    }

    default String getPath() {
        return "";
    }

    default ReportOption getReportOption() {
        return ReportOption.NONE;
    }

    default boolean isOpenReport() {
        return false;
    }

    default boolean showHelpOnly() { return true; }
}