package de.uniba.dsg.bpmnspector.common.util;

import api.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Matthias Geiger
 * @version 1.0
 */
public class HtmlReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReportGenerator.class
            .getSimpleName());

    public static Path createHtmlReport(ValidationResult result) {
        String fileName = result.getFoundFiles().get(0).getFileName().toString();

        try {
            Path reportPath = FileUtils.createResourcesForReports();

            Path htmlFile = FileUtils.createFileForReport(reportPath, fileName, "html");

            createReportFromValidationResult(result, htmlFile);

            return htmlFile;
        } catch ( IOException ioe) {
            LOGGER.error("Creation of HTML Report files failed. Report directory or needed resources could not be created.", ioe);
            return null;
        }
    }

    public Path createSummaryReport(List<ValidationResult> results, Path baseFolder) {
        String fileName = "ValidationSummary"+(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()))+".html";

        try {
            Path reportPath = FileUtils.createResourcesForReports();

            Path summaryFile = reportPath.resolve(fileName);

            int checkedFilesSum = 0;
            int filesWithViolationsSum = 0;

            List<SingleValidationSummary> summaries = new LinkedList<>();

            Map<String, Integer> violationsByConstraintCount = new TreeMap<>();

            for(ValidationResult singleResult : results) {
                //create Report
                Path singleReport = createHtmlReport(singleResult);
                // update counters
                checkedFilesSum += singleResult.getFoundFiles().size();
                filesWithViolationsSum += singleResult.getFilesWithViolations().size();

                // update violationsByConstraintCount
                addViolationsToMap(singleResult, violationsByConstraintCount);

                //create summary for single validation result
                SingleValidationSummary summary = new SingleValidationSummary(singleReport.getFileName().toString(),
                        singleResult.getFoundFiles().get(0).toString(), singleResult.getViolations().size(),
                        singleResult.getWarnings().size());
                summaries.add(summary);
            }

            createSummaryHtml(baseFolder.toString(), summaryFile, checkedFilesSum, filesWithViolationsSum, violationsByConstraintCount, summaries);

            return summaryFile;
        } catch ( IOException ioe) {
            LOGGER.error("Creation of HTML Report files failed. Report directory or needed resources could not be created.", ioe);
            return null;
        }
    }

    private void createSummaryHtml(String baseFolder, Path summaryFile, int checkedFilesSum, int filesWithViolationsSum,
                                   Map<String, Integer> violationsByConstraintCount,
                                   List<SingleValidationSummary> summaries) {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();

        try {
            Template template = Velocity.getTemplate("reporting/ValidationSummary.vm");

            VelocityContext context = new VelocityContext();
            context.put("baseFolder", baseFolder);
            context.put("checkedFilesSum", checkedFilesSum);
            context.put("validFilesSum", checkedFilesSum-filesWithViolationsSum);
            context.put("filesWithViolationsSum", filesWithViolationsSum);
            context.put("violationsByConstraintCount", violationsByConstraintCount);
            context.put("summaries", summaries);

            StringWriter sw = new StringWriter();
            template.merge(context, sw);


            try (FileWriter fw = new FileWriter(summaryFile.toFile())) {
                fw.write(sw.toString());
                fw.flush();
            } catch (IOException ioe) {
                LOGGER.error("Creation of HTML Report file {} failed.", summaryFile.toString(), ioe);
            }
        } catch (VelocityException e) {
            LOGGER.error("Creation of HTML report failed due to a Velocity Exception", e);
        }
    }

    private void addViolationsToMap(ValidationResult singleResult, Map<String, Integer> violationsByConstraintCount) {
        for(Violation violation : singleResult.getViolations()) {
            Integer i = violationsByConstraintCount.get(violation.getConstraint());
            if (i != null) {
                violationsByConstraintCount.put(violation.getConstraint(), i + 1);
            } else {
                violationsByConstraintCount.put(violation.getConstraint(), 1);
            }
        }
    }

    private static void createReportFromValidationResult(ValidationResult result, Path outputPath) {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();

        try {
            Template template = Velocity.getTemplate("reporting/ValidationResult.vm");

            VelocityContext context = new VelocityContext();
            context.put("validationResult", result);
            context.put("filesWithWarnings", getFilesWithWarnings(result));
            StringWriter sw = new StringWriter();
            template.merge(context, sw);

            try (FileWriter fw = new FileWriter(outputPath.toFile())) {
                fw.write(sw.toString());
                fw.flush();
            } catch (IOException ioe) {
                LOGGER.error("Creation of HTML Report file {} failed.", outputPath.toString(), ioe);
            }
        } catch (VelocityException e) {
            LOGGER.error("Creation of HTML report failed due to a Velocity Exception", e);
        }

    }

    private static List<Path> getFilesWithWarnings(ValidationResult result) {
        return result.getWarnings().stream().map(v -> v.getLocation().getFileName()).distinct().collect(Collectors.toList());
    }

    public class SingleValidationSummary {
        private final String reportFilename;
        private final String checkedFilename;
        private final int violationCount;
        private final int warningCount;

        public SingleValidationSummary(String reportFilename, String checkedFilename, int violationCount, int warningCount) {
            this.reportFilename = reportFilename;
            this.checkedFilename = checkedFilename;
            this.violationCount = violationCount;
            this.warningCount = warningCount;
        }


        public String getReportFilename() {
            return reportFilename;
        }

        public String getCheckedFilename() {
            return checkedFilename;
        }

        public int getViolationCount() {
            return violationCount;
        }

        public int getWarningCount() {
            return warningCount;
        }
    }
}
