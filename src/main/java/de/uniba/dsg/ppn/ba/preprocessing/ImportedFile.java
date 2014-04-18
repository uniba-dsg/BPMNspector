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

	private File file;
	private String prefix;
	private String namespace;

	public ImportedFile() {

	}

	public ImportedFile(File file, String prefix, String namespace) {
		this.file = file;
		this.prefix = prefix;
		this.namespace = namespace;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
