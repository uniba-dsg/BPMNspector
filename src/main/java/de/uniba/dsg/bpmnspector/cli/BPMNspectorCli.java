package de.uniba.dsg.bpmnspector.cli;

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

    public static final String CHECK_ALL = "ALL";

    private final Map<String, String> checkOptions;

    public BPMNspectorCli() {
        checkOptions = createCheckOptions();
    }

    private Options createCliOptions() {
        Option debug = OptionBuilder.withDescription("run BPMNspector in debug mode")
                .withLongOpt(DEBUG)
                .create("d");
        StringBuilder checksDescBuilder = new StringBuilder(400)
                .append("defines which checks should be performed.\nAllowed values:\n");

        for(Map.Entry entry : checkOptions.entrySet()) {
            checksDescBuilder.append(entry.getKey()).append(" - ")
                    .append(entry.getValue()).append('\n');
        }

        Option checks = OptionBuilder.withDescription(checksDescBuilder.toString())
                .hasArg()
                .withArgName("[opt1[,opt2]...")
                .withLongOpt(CHECKS)
                .create("c");
        Option help = new Option("h", HELP, false, "prints this usage information");
        Options options = new Options();
        options.addOption(debug);
        options.addOption(help);
        options.addOption(checks);

        return options;
    }

    private void printUsageInformation() {
        HelpFormatter formatter = new HelpFormatter();
        String example = "\nExamples:\n"
                +"\t\tBPMNspector myfile.bpmn\n"
                +"\t\tBPMNspector c:\\absolute\\path\\to\\folder -c REF -d";
        formatter.printHelp("BPMNspector <file or directory>", "Options:", createCliOptions(), "", true);
        System.out.println(example);
    }

    public CliParameter parse(String[] args) {
        Parser parser = new BasicParser();
        try {
            CommandLine cl = parser.parse(createCliOptions(), args);
            if(cl.hasOption(HELP)) {
                printUsageInformation();
                System.exit(0);
            } else {
                if(cl.getArgs().length==1) { //NOPMD
                    return new CliParameter(cl.getArgs()[0],
                            validateAndCreateValidationOptions(
                                    cl.getOptionValue(CHECKS, CHECK_ALL)),
                            cl.hasOption(DEBUG));
                } else {
                    LOGGER.error("Invalid usage: Too much arguments detected. It is only possible to check on file or directory at one time.");
                    printUsageInformation();
                    System.exit(-1);
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

    private Map<String, String> createCheckOptions() {
        Map<String, String> checkOptions = new HashMap<>();
        for(ValidationOption option : ValidationOption.values()) {
            checkOptions.put(option.toString(), option.getDescription());
        }

        checkOptions.put("ALL", "performs all checks (default)");

        return checkOptions;
    }
}
