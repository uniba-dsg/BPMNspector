package de.uniba.dsg.ppn.ba.preprocessing;

import java.io.File;

/**
 * Just a helper class for avoiding the usage of object arrays and making the
 * code better readable. Is returned as result from
 * preprocessor.selectImportedFiles()
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class ImportedFile {

    private final File file;
    private final String prefix;
    private final String namespace;
    private final String importType;

    public ImportedFile(File file, String prefix, String namespace,
            String importType) {
        this.file = file;
        this.prefix = prefix;
        this.namespace = namespace;
        this.importType = importType;
    }

    public File getFile() {
        return file;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getImportType() {
        return importType;
    }
}
