package de.uniba.dsg.bpmnspector.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class.getSimpleName());

    public static List<Path> getAllBpmnFileFromDirectory(Path directory) {
        assertDirectory(directory);

        List<Path> bpmnFiles = new ArrayList<>();

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory, "*.{bpmn,bpmn2,bpmn20.xml}")) {
            for (Path path : dirStream) {
                bpmnFiles.add(path);
            }
        } catch (IOException e) {
            LOGGER.error("IOException while traversing folder.", e);
        }

        return bpmnFiles;
    }

    public static void assertDirectory(Path path) {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path " + path.toAbsolutePath() + " is no directory.");
        }
    }

    public static Path createResourcesForReports() throws IOException {
        Path reportDir = Paths.get("reports");

        if(!Files.exists(reportDir)) {
            Files.createDirectory(reportDir);
        }
        Path reportResDir = reportDir.resolve("res");
        if(!Files.exists(reportResDir)) {
            Files.createDirectory(reportResDir);
        }
        Path myPath = null;
        boolean loadedFromJar = false;
        try {
            URI uri = FileUtils.class.getResource("/reporting/res").toURI();

            if (uri.getScheme().equals("jar")) {
                loadedFromJar = true;
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                myPath = fileSystem.getPath("/reporting/res");
            } else {
                myPath = Paths.get(uri);
            }

            Files.walk(myPath).forEach(path -> {
                try {
                    Files.copy(path, reportResDir.resolve(path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    LOGGER.error("Unable to copy resources for HTML reports.", e);
                }
            });
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to copy resources for HTML reports.", e);
        } finally {
            // close FileSystem if created before - try-with resources is not working as FileSystem is needed during
            // Files.walk()
            if(loadedFromJar && myPath!=null) {
                myPath.getFileSystem().close();
            }
        }

        return reportDir;
    }

    public static Path createFileForReport(Path reportDir, String filePrefix, String fileSuffix) {
        Path reportFile = reportDir.resolve(filePrefix + "_validation_result."+fileSuffix);

        for(int i=1; Files.exists(reportFile); i++) {
            reportFile = reportDir.resolve(filePrefix+"("+i+")_validation_result."+fileSuffix);
        }

        return reportFile;
    }

}
