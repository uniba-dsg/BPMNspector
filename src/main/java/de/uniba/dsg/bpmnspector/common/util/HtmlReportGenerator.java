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
import java.util.List;
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
}
