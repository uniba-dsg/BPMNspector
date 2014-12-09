package de.uniba.dsg.ppn.ba.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uniba.dsg.ppn.ba.preprocessing.ImportedFile;

/**
 * Collects all imported files from a provided document
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class ImportedFilesCrawler {

    /**
     * collects all imported files with bpmn namespace in the given document
     *
     * @param document
     *            the document, from which the imports are collected
     * @param folder
     *            the parent folder of the document
     * @param size
     *            the number of already collected imports for ensuring unique
     *            namespace prefixes
     * @param onlyBpmnFiles
     *            if true, just imports with the import type of the bpmn
     *            namespace are returned
     * @return a list of importedFile including all bpmn imports with the
     *         absolute path, the new namespace prefix and the namespace
     */
    public static List<ImportedFile> selectImportedFiles(Document document,
            File folder, int size, boolean onlyBpmnFiles) {
        NodeList importedFilesList = document.getElementsByTagNameNS(
                ConstantHelper.BPMNNAMESPACE, "import");
        List<ImportedFile> importedFiles = new ArrayList<>();

        for (int i = 0; i < importedFilesList.getLength(); i++) {
            Node importedFileNode = importedFilesList.item(i);
            String importType = importedFileNode.getAttributes()
                    .getNamedItem("importType").getTextContent();
            if (!onlyBpmnFiles
                    || ConstantHelper.BPMNNAMESPACE.equals(importType)) {
                File file = new File(importedFileNode.getAttributes()
                        .getNamedItem("location").getTextContent());
                if (!file.isAbsolute()) {
                    file = new File(folder.getPath()
                            + File.separator
                            + importedFileNode.getAttributes()
                                    .getNamedItem("location").getTextContent());
                }
                String prefix = "ns" + (i + size);
                String namespace = importedFileNode.getAttributes()
                        .getNamedItem("namespace").getTextContent();
                importedFiles.add(new ImportedFile(file, prefix, namespace,
                        importType));
            }
        }

        return importedFiles;
    }

}
