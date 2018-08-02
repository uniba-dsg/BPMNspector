package de.uniba.dsg.bpmnspector;


import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.autofix.FixReport;
import de.uniba.dsg.bpmnspector.cli.AutoFixController;
import de.uniba.dsg.bpmnspector.cli.BPMNspectorCli;
import de.uniba.dsg.bpmnspector.cli.CliException;
import de.uniba.dsg.bpmnspector.cli.CliParameter;
import de.uniba.dsg.bpmnspector.common.util.CsvReportGenerator;
import de.uniba.dsg.bpmnspector.common.util.FileUtils;
import de.uniba.dsg.bpmnspector.common.util.HtmlReportGenerator;
import de.uniba.dsg.bpmnspector.common.util.XmlWriterApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BPMNspectorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNspectorMain.class.getSimpleName());

    public static void main(String[] args) {
        BPMNspectorCli cli = new BPMNspectorCli();
        try {
            CliParameter params = cli.parse(args);
            if (params.showHelpOnly()) {
                cli.printUsageInformation();
                return;
            } else if (params.isDebug()) {
                setDebugLevel();
            } else {
                setInfoLevel();
            }

            try {
                BPMNspector inspector = new BPMNspector();

                // Check whether path exists
                Path path = Paths.get(params.getPath());
                if (Files.exists(path)) {
                    path = path.toAbsolutePath().normalize();
                    try {
                        if (Files.isDirectory(path)) {
                            List<ValidationResult> results = inspector
                                    .inspectDirectory(path,
                                            params.getValidationOptions());
                            List<FixReport> reports = fixProblemsInFolder(results, params.getFixOption(), path);
                            createFolderReports(path, results, params.getReportOption(), params.isOpenReport());
                        } else {
                            ValidationResult result = inspector.inspectFile(path,
                                    params.getValidationOptions());
                            FixReport fixReport = fixProblems(result, params.getFixOption(), path);
                            createReport(result, params.getReportOption(), params.isOpenReport());
                        }

                    } catch (ValidationException e) {
                        LOGGER.error("Inspection failed.", e);
                    }
                } else {
                    LOGGER.error("File or directory does not exist.");
                }
            } catch (ValidationException e) {
                LOGGER.error("Initialization of BPMNspector failed.", e);
            }
        } catch (CliException e) {
            LOGGER.error(e.getMessage());
            cli.printUsageInformation();
        }
    }

    private static FixReport fixProblems(ValidationResult result, FixOption fixOption, Path path) throws ValidationException {
        AutoFixController controller = new AutoFixController();
        return controller.fixProblems(result, fixOption, path);
    }

    private static List<FixReport> fixProblemsInFolder(List<ValidationResult> results, FixOption fixOption, Path path) {
        List<FixReport> reports = new ArrayList<>(results.size());
        List<Path> allRelevantFiles = FileUtils.getAllBpmnFileFromDirectory(path);
        for(Path singleFile : allRelevantFiles) {
            Optional<ValidationResult> resultOptional = results.stream().filter(r -> r.getFilesWithViolations().contains(singleFile)).findAny();
            if(resultOptional.isPresent()) {
                try {
                    reports.add(fixProblems(resultOptional.get(), fixOption, singleFile));
                } catch (ValidationException e) {
                    LOGGER.warn("Fixing failed for file "+singleFile, e);
                }
            }
        }

        return reports;
    }

    private static void setDebugLevel() {
        ch.qos.logback.classic.Logger rootLogger  = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        LOGGER.debug("Debug mode activated.");
    }

    private static void setInfoLevel() {
        ch.qos.logback.classic.Logger rootLogger  = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.INFO);
    }

    private static void createReport(ValidationResult result, ReportOption option, boolean andOpen) {
        Path reportPath = null;
        if(ReportOption.ALL.equals(option)) {
            reportPath = HtmlReportGenerator.createHtmlReport(result);
            XmlWriterApi.createXmlReport(result);
        } else if (ReportOption.HTML.equals(option)) {
            reportPath = HtmlReportGenerator.createHtmlReport(result);
        } else if (ReportOption.XML.equals(option)) {
            reportPath = XmlWriterApi.createXmlReport(result);
        } // else: NONE - create no reports
        if(andOpen && reportPath!=null) {
            try {
                Desktop.getDesktop().browse(reportPath.toUri());
            } catch (Exception ignore) {
                // ignore any exceptions
            }
        }
    }

    private static void createFolderReports(Path baseFolder, List<ValidationResult> results, ReportOption option, boolean andOpen) {
        Path reportPath = null;
        if(ReportOption.ALL.equals(option)) {
            reportPath = HtmlReportGenerator.createSummaryReport(results, baseFolder);
            results.forEach(XmlWriterApi::createXmlReport);
        } else if (ReportOption.HTML.equals(option)) {
            reportPath = HtmlReportGenerator.createSummaryReport(results, baseFolder);
        } else if (ReportOption.XML.equals(option)) {
            results.forEach(XmlWriterApi::createXmlReport);
        } // else: NONE - create no reports
        if(andOpen && reportPath!=null) {
            try {
                Desktop.getDesktop().browse(reportPath.toUri());
            } catch (Exception ignore) {
                // ignore any exceptions
            }
        }

        // CSV report for dissertation
        CsvReportGenerator.createCsvReport(baseFolder, results);
    }
}
