package de.uniba.dsg.bpmnspector;


import de.uniba.dsg.bpmnspector.cli.BPMNspectorCli;
import de.uniba.dsg.bpmnspector.cli.CliParameter;
import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.common.util.HtmlReportGenerator;
import de.uniba.dsg.bpmnspector.common.util.XmlWriterApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class BPMNspectorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNspectorMain.class.getSimpleName());

    public static void main(String[] args) {
        BPMNspectorCli cli = new BPMNspectorCli();
        CliParameter params = cli.parse(args);

        if(params.isDebug()) {
            setDebugLevel();
        }

        try {
            BPMNspector inspector = new BPMNspector();

            // Check whether path exists
            Path path = Paths.get(params.getPath());
            if (Files.exists(path)) {

                try {
                    if (Files.isDirectory(path)) {
                        List<ValidationResult> results = inspector
                                .inspectDirectory(path,
                                        params.getValidationOptions());

                        createFolderReports(path, results,params.getReportOption(), true);
                    } else {
                        ValidationResult result = inspector.inspectFile(path,
                                params.getValidationOptions());
                        createReport(result, params.getReportOption(), true);
                    }

                } catch (ValidationException e) {
                    LOGGER.error("Inspection failed.", e);
                }
            } else {
                LOGGER.error("File or directory does not exist.");
                System.exit(-1);
            }
        } catch (ValidationException e) {
            LOGGER.error("Initialization of BPMNspector failed.", e);
        }
    }

    private static void setDebugLevel() {
        ch.qos.logback.classic.Logger rootLogger  = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        LOGGER.debug("Debug mode activated.");
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
    }
}
