package de.uniba.dsg.ppn.ba.helper;

public class NothingFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public NothingFoundException() {
		super("BPMN Element couldn't be found!");
	}
}
