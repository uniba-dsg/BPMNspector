package de.uniba.dsg.bpmnspector.common.util;

import api.ValidationResult;
import api.Violation;
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

    public static Path createSummaryReport(List<ValidationResult> results, Path baseFolder) {
        String fileName = "ValidationSummary"+(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date()))+".html";

        try {
            Path reportPath = FileUtils.createResourcesForReports();

            Path summaryFile = reportPath.resolve(fileName);

            int checkedFilesSum = 0;
            int validResults = 0;
            int validWithWarnings = 0;

            List<SingleValidationSummary> summaries = new LinkedList<>();

            Map<String, Integer> violationsByConstraintCount = new TreeMap<>();

            for(ValidationResult singleResult : results) {
                //create Report
                Path singleReport = createHtmlReport(singleResult);
                // update counters
                checkedFilesSum += singleResult.getFoundFiles().size();
                if(singleResult.isValid()) {
                    validResults++;
                    if(!singleResult.getWarnings().isEmpty()) {
                        validWithWarnings++;
                    }
                }

                // update violationsByConstraintCount
                addViolationsToMap(singleResult, violationsByConstraintCount);

                //create summary for single validation result
                SingleValidationSummary summary = new SingleValidationSummary(singleReport.getFileName().toString(),
                        singleResult.getFoundFiles().get(0).toString(), singleResult.getViolations().size(),
                        singleResult.getWarnings().size());
                summaries.add(summary);
            }

            createSummaryHtml(baseFolder.toString(), summaryFile, checkedFilesSum, validResults, validWithWarnings, violationsByConstraintCount, summaries);

            return summaryFile;
        } catch ( IOException ioe) {
            LOGGER.error("Creation of HTML Report files failed. Report directory or needed resources could not be created.", ioe);
            return null;
        }
    }

    private static void createSummaryHtml(String baseFolder, Path summaryFile, int checkedFilesSum, int validResults,
                                   int validWithWarnings, Map<String, Integer> violationsByConstraintCount,
                                   List<SingleValidationSummary> summaries) {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();

        try {
            Template template = Velocity.getTemplate("reporting/ValidationSummary.vm");

            VelocityContext context = new VelocityContext();
            context.put("baseFolder", baseFolder);
            context.put("checkedFilesSum", checkedFilesSum);
            context.put("directlyChecked", summaries.size());
            context.put("importedFilesChecked", checkedFilesSum-summaries.size());
            context.put("validResults", validResults);
            context.put("validWithWarnings", validWithWarnings);
            context.put("completelyValid", validResults-validWithWarnings);
            context.put("invalidResults", summaries.size()-validResults);
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

    private static void addViolationsToMap(ValidationResult singleResult, Map<String, Integer> violationsByConstraintCount) {
        for(Violation violation : singleResult.getViolations()) {
            Integer i = violationsByConstraintCount.get(violation.getConstraint());
            if (i == null) {
                violationsByConstraintCount.put(violation.getConstraint(), 1);
            } else {
                violationsByConstraintCount.put(violation.getConstraint(), i + 1);
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
            context.put("resourcesWithWarnings", getResourcesWithWarnings(result));
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

    private static List<String> getResourcesWithWarnings(ValidationResult result) {
        return result.getWarnings().stream().map(v -> v.getLocation().getResource().getResourceName()).distinct().collect(Collectors.toList());
    }

    public static class SingleValidationSummary {
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
