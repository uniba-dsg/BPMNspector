package de.uniba.dsg.bpmnspector.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtils {

    public static List<Path> getAllBpmnFileFromDirectory(Path directory) throws
            IOException {
        assertDirectory(directory);
        List<Path> bpmnFiles = new ArrayList<>();
        String[] suffixes = {"bpmn", "bpmn2", "bpmn20.xml"};
        Collection<File> filesColl = org.apache.commons.io.FileUtils.listFiles(directory.toFile(), suffixes, true);
        for(File file : filesColl) {
            bpmnFiles.add(file.toPath());
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
        if(!Files.exists(reportDir) || !Files.exists(reportDir.resolve("res"))) {
            Files.createDirectory(reportDir.resolve("res"));
            org.apache.commons.io.FileUtils.copyDirectory(Paths.get("src/main/resources/reporting/res").toFile(), reportDir.resolve("res").toFile());
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
