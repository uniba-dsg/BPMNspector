package de.uniba.dsg.bpmnspector.cli;

import de.uniba.dsg.bpmnspector.ReportOption;
import de.uniba.dsg.bpmnspector.ValidationOption;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BPMNspectorCli {

    public static final Logger LOGGER = LoggerFactory
            .getLogger(BPMNspectorCli.class.getSimpleName());

    public static final String HELP = "help";
    public static final String DEBUG = "debug";
    public static final String CHECKS = "checks";
    public static final String OPEN = "open";

    public static final String CHECK_ALL = "ALL";

    private final Map<String, String> checkOptions;
    private final Map<String, String> reportOptions;

    public BPMNspectorCli() {
        checkOptions = createCheckOptions();
        reportOptions = createReportOptions();
    }

    private Options createCliOptions() {
        Option debug = Option.builder("d").desc("run BPMNspector in debug mode")
                .longOpt(DEBUG)
                .build();

        StringBuilder reportDescBuilder = new StringBuilder(400)
                .append("defines which report type should be generated.\nAllowed values:\n");

        for(Map.Entry entry : reportOptions.entrySet()) {
            reportDescBuilder.append(entry.getKey()).append(" - ")
                    .append(entry.getValue()).append('\n');
        }

        Option reportFormat = Option.builder("r").desc(reportDescBuilder.toString())
                .hasArg()
                .argName("NONE | XML | HTML | BOTH")
                .build();

        StringBuilder checksDescBuilder = new StringBuilder(400)
                .append("defines which checks should be performed.\nAllowed values:\n");

        for(Map.Entry entry : checkOptions.entrySet()) {
            checksDescBuilder.append(entry.getKey()).append(" - ")
                    .append(entry.getValue()).append('\n');
        }

        Option checks = Option.builder("c").desc(checksDescBuilder.toString())
                .hasArg()
                .argName("[opt1[,opt2]...")
                .longOpt(CHECKS)
                .build();

        Option help = new Option("h", HELP, false, "prints this usage information");

        Option open = new Option("o", OPEN, false, "open the report file upon completion");

        Options options = new Options();
        options.addOption(debug);
        options.addOption(help);
        options.addOption(reportFormat);
        options.addOption(open);
        options.addOption(checks);

        return options;
    }

    private void printUsageInformation() {
        HelpFormatter formatter = new HelpFormatter();
        String example = "\nExamples:\n"
                +"\t\tBPMNspector myfile.bpmn\n"
                +"\t\tBPMNspector c:\\absolute\\path\\to\\folder -c REF -d\n"
                +"\t\tBPMNspector c:\\absolute\\path\\to\\file.bpmn -o -r HTML";
        formatter.printHelp("BPMNspector <file or directory>", "Options:", createCliOptions(), "", true);
        System.out.println(example);
    }

    public CliParameter parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cl = parser.parse(createCliOptions(), args);
            if(cl.hasOption(HELP)) {
                printUsageInformation();
                System.exit(0);
            } else {
                if(cl.getArgs().length==0) { //NOPMD
                    LOGGER.error("Invalid usage: No arguments detected. It is only possible to check one file or directory at the same time.");
                    printUsageInformation();
                    System.exit(0);
                } else if(cl.getArgs().length==1) {//NOPMD
                    return new CliParameter(cl.getArgs()[0],
                            validateAndCreateValidationOptions(
                                    cl.getOptionValue(CHECKS, CHECK_ALL)),
                            cl.hasOption(DEBUG), validateAndCreateReportOption(cl.getOptionValue("r", "HTML")));
                } else {
                    LOGGER.error("Invalid usage: Too much arguments detected. It is only possible to check one file or directory at the same time.");
                    printUsageInformation();
                    System.exit(0);
                }
            }
        } catch (ParseException e) {
            LOGGER.error("Invalid usage: "+e.getMessage());
            printUsageInformation();
            System.exit(-1);
        }

        return null;
    }

    private List<ValidationOption> validateAndCreateValidationOptions(String checkArgs)
            throws ParseException {

        List<String> argsAsList = new ArrayList<>();
        Collections.addAll(argsAsList, checkArgs.split(","));

        List<ValidationOption> options = new ArrayList<>();

        if(argsAsList.contains(CHECK_ALL)) {
            Collections.addAll(options, ValidationOption.values());
        } else {
            for (String str : argsAsList) {
                try {
                    options.add(ValidationOption.valueOf(str));
                } catch (IllegalArgumentException iae) {
                    throw new ParseException(
                            "Validation option "+str+" is not valid.");
                }
            }
        }

        return options;
    }

    private ReportOption validateAndCreateReportOption(String reportOptionString) throws ParseException {
        try {
            return ReportOption.valueOf(reportOptionString);
        } catch (IllegalArgumentException iae) {
            throw new ParseException(
                    "Report option "+reportOptionString+" is not valid.");
        }
    }

    private Map<String, String> createCheckOptions() {
        Map<String, String> checkOptions = new HashMap<>();
        for(ValidationOption option : ValidationOption.values()) {
            checkOptions.put(option.toString(), option.getDescription());
        }

        checkOptions.put("ALL", "performs all checks (default)");

        return checkOptions;
    }

    private Map<String, String> createReportOptions() {
        Map<String, String> reportOptions = new HashMap<>();
        for(ReportOption option : ReportOption.values()) {
            reportOptions.put(option.toString(), option.getDescription());
        }
        return reportOptions;
    }
}
