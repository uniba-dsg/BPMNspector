package de.uniba.dsg.bpmnspector.common.util;


import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    public static List<Path> getAllBpmnFileFromDirectory(Path directory) {
        assertDirectory(directory);
        List<Path> bpmnFiles = new ArrayList<>();
        String[] suffixes = {"bpmn", "bpmn2", "bpmn20.xml"};
        Collection<File> filesColl = org.apache.commons.io.FileUtils.listFiles(directory.toFile(), suffixes, true);
        bpmnFiles.addAll(filesColl.stream().map(File::toPath).collect(Collectors.toList()));
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
        if(isEmptyDir(reportResDir)) {
            File zip = org.apache.commons.io.FileUtils.toFile(FileUtils.class.getClassLoader().getResource("reportingResources.zip"));
            if(zip != null) {
                ZipFile zipFile = new ZipFile(zip);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    Path zippedFile = reportResDir.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectory(zippedFile);
                    } else {
                        InputStream in = zipFile.getInputStream(entry);
                        org.apache.commons.io.FileUtils.copyInputStreamToFile(in, zippedFile.toFile());
                    }
                }
            } else {
                System.out.println("Zip not found!");
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

    private static boolean isEmptyDir(Path dir) throws IOException {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            return !stream.iterator().hasNext();
        }
    }

}
