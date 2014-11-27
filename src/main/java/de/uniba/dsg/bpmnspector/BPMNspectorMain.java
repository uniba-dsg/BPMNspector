package de.uniba.dsg.bpmnspector;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmnspector.cli.BPMNspectorCli;
import de.uniba.dsg.bpmnspector.cli.CliParameter;
import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;
import de.uniba.dsg.ppn.ba.xml.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BPMNspectorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNspectorMain.class.getSimpleName());

    public static void main(String[] args) {
        BPMNspectorCli cli = new BPMNspectorCli();
        CliParameter params = cli.parse(args);

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
                        results.forEach(BPMNspectorMain::createXmlReport);
                    } else {
                        ValidationResult result = inspector.inspectFile(path,
                                params.getValidationOptions());
                        createXmlReport(result);
                    }

                } catch (ValidatorException e) {
                    LOGGER.error("Inspection failed.", e);
                }
            } else {
                LOGGER.error("File or directory does not exist.");
                System.exit(-1);
            }
        } catch (ValidatorException e) {
            LOGGER.error("Initialization of BPMNspector failed.", e);
        }
    }

    private static void setDebugLevel() {
        ((ch.qos.logback.classic.Logger) LOGGER).setLevel(Level.DEBUG);
        LOGGER.debug("Debug mode activated.");
    }

    private static void createXmlReport(ValidationResult result) {
        try {
            String firstFile = result.getCheckedFiles().get(0);
            String fileName = Paths.get(firstFile).getFileName().toString();
            Path reportPath = Paths.get("reports");

            if(!Files.exists(reportPath)) {
                Files.createDirectory(reportPath);
            }

            Path reportFile = reportPath.resolve(fileName + "_validation_result.xml");

            XmlWriter xmlWriter = new XmlWriter();
            xmlWriter.writeResult(result,reportFile.toFile());
        } catch (JAXBException | IOException e) {
            LOGGER.error("result of validation couldn't be written in xml!", e);
        }
    }

}
