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

}
