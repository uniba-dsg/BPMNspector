package de.uniba.dsg.bpmnspector.common.util;

import api.ValidationResult;
import api.Violation;
import api.Warning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class CsvReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvReportGenerator.class.getSimpleName());

    public static void createCsvReport(Path baseFolder, List<ValidationResult> results) {
        String fileName = "ValidationSummary" + (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date())) + ".csv";

        try {
            Path reportPath = FileUtils.createResourcesForReports();

            Path csvFile = reportPath.resolve(fileName);

            try (FileWriter fileWriter = new FileWriter(csvFile.toFile())) {
                fileWriter.write(createHeadline());
                for (ValidationResult result : results) {
                    fileWriter.write(createSingleLineForMojoResults(result));
                }
            }

        } catch (IOException ioe) {
            LOGGER.error("Creation of CSV Report files failed. Report directory or needed resources could not be created.", ioe);
        }

    }

    private static String createHeadline() {
        StringBuilder builder = new StringBuilder();
        builder.append("FileName ; ");
        // violations:
        builder.append("Valid? ; ");
        builder.append("no. violations ; ");
        createMojoViolationReasonMap().forEach((key, value) -> {
            builder.append(key);
            builder.append(" ; ");
        });

        // warnings:
        builder.append(" canceled ; ");
        builder.append("no. warnings ; ");

        createMojoWarningReasonMap().forEach((key, value) -> {
            builder.append(key);
            builder.append(" ; ");
        });
        builder.append("\n");
        return builder.toString();
    }

    private static String createSingleLineForMojoResults(ValidationResult result) {
        boolean hasBeenCanceled = false;
        Map<MojoViolationReason, Integer> violationReasonIntegerMap = createMojoViolationReasonMap();
        Map<MojoWarningReason, Integer> reasonIntegerMap = createMojoWarningReasonMap();

        for (Violation violation : result.getViolations()) {
            for (MojoViolationReason reason : MojoViolationReason.values()) {
                if (reason.getSearchString().equals(violation.getConstraint())) {
                    violationReasonIntegerMap.put(reason, violationReasonIntegerMap.get(reason) + 1);
                }
            }
        }

        for (Warning warning : result.getWarnings()) {
            for (MojoWarningReason reason : MojoWarningReason.values()) {
                if (warning.getMessage().contains(reason.getSearchString())) {

                    reasonIntegerMap.put(reason, reasonIntegerMap.get(reason) + 1);

                    if (reason.isCanceling) {
                        hasBeenCanceled = true;
                    }
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(result.getResources().get(0).getResourceName());
        builder.append(" ; ");
        // violations:
        builder.append(result.isValid());
        builder.append(" ; ");
        builder.append(result.getViolations().size());
        builder.append(" ; ");
        violationReasonIntegerMap.forEach((key, value) -> {
            builder.append(value);
            builder.append(" ; ");
        });

        // warnings:
        builder.append(hasBeenCanceled ? "canceled ; " : " ; ");

        builder.append(result.getWarnings().size());
        builder.append(" ; ");

        reasonIntegerMap.forEach((key, value) -> {
            builder.append(value);
            builder.append(" ; ");
        });

        builder.append("\n");
        return builder.toString();
    }


    private static Map<MojoViolationReason, Integer> createMojoViolationReasonMap() {
        Map<MojoViolationReason, Integer> violationReasonMap = new TreeMap<>();
        for (MojoViolationReason r : MojoViolationReason.values()) {
            violationReasonMap.put(r, 0);
        }
        return violationReasonMap;
    }

    private static Map<MojoWarningReason, Integer> createMojoWarningReasonMap() {
        Map<MojoWarningReason, Integer> warningReasonMap = new TreeMap<>();
        for (MojoWarningReason r : MojoWarningReason.values()) {
            warningReasonMap.put(r, 0);
        }
        return warningReasonMap;
    }

    private enum MojoViolationReason {
        XSD("XSD-Check"),
        DEADLOCK("Deadlock"),
        LACK_OF_SYNC("LackOfSync");

        private final String searchString;

        MojoViolationReason(String searchString) {
            this.searchString = searchString;
        }

        public String getSearchString() {
            return searchString;
        }
    }

    private enum MojoWarningReason {
        MULTI_PROCESS("participant", false),
        SUB_PROCESS("SubProcess", false),
        CALL_ACTIVITY("CallActivity", false),
        INT_CATCH_EVENT("IntermediateCatchEvent", false),
        INT_THROW_EVENT("ThrowEvent", false),
        EXCLUSIVE_GW_SINGLE("ExclusiveGateway", false),
        PARALLEL_GW_SINGLE("ParallelGateway", false),
        INCLUSIVE_GW_SINGLE("InclusiveGateway", false),
        // Canceling:
        BOUNDARY_EVENT("Boundary Events", true),
        COND_SEQ_FLOW("Conditional sequence flows", true),
        // Findings:
        POT_DEADLOCK("Potential Deadlock:", false),
        POT_LACK("Potential LackOfSync:", false),
        // Others:
        UNCONNECTED("mojo: The process is unconnected.", false),
        // Error:
        ERROR("mojo: Mojo validation failed due to internal problems.", true);

        private final String searchString;
        private final boolean isCanceling;

        MojoWarningReason(String searchString, boolean isCanceling) {
            this.searchString = searchString;
            this.isCanceling = isCanceling;
        }

        public String getSearchString() {
            return searchString;
        }

        public boolean isCanceling() {
            return isCanceling;
        }
    }
}
