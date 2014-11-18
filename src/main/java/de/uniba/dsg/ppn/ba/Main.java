package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import de.uniba.dsg.ppn.ba.xml.XmlWriter;

public class Main {

    private final static Logger logger;
    private final static Level debugLevel;

    static {
        logger = (Logger) LoggerFactory.getLogger(Main.class.getSimpleName());
        debugLevel = Level.DEBUG;
    }

    public static void main(final String[] args) {
        final SchematronBPMNValidator validator = new SchematronBPMNValidator();
        final ArrayList<String> argsAsList = new ArrayList<>(
                Arrays.asList(args));
        final XmlWriter xmlWriter = new XmlWriter();

        if (argsAsList.contains("--debug")) {
            validator.setLogLevel(debugLevel);
            logger.setLevel(debugLevel);
            ((Logger) LoggerFactory.getLogger(xmlWriter.getClass()
                    .getSimpleName())).setLevel(debugLevel);
            argsAsList.remove("--debug");
        }

        logger.info("loglevel is set to {}", logger.getEffectiveLevel());

        if (argsAsList.isEmpty()) {
            logger.error("There must be files to check!");
            System.exit(-1);
        } else {
            for (String parameter : argsAsList) {
                try {
                    File file = new File(parameter);
                    if (!file.isAbsolute()) {
                        file = file.getAbsoluteFile();
                    }
                    ValidationResult result = validator.validate(file);
                    xmlWriter.writeResult(result,
                            new File(file.getParentFile() + File.separator
                                    + "validation_result_" + file.getName()
                                    + ".xml"));
                } catch (BpmnValidationException e) {
                    logger.error(e.getMessage());
                } catch (JAXBException e) {
                    logger.error("result of validation couldn't be written in xml!");
                }
            }
        }
    }
}
