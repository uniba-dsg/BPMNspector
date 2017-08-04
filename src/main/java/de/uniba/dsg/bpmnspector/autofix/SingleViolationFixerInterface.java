package de.uniba.dsg.bpmnspector.autofix;

import org.jdom2.Document;

public interface SingleViolationFixerInterface {

    boolean fixSingleIssue(Document processAsDoc, String xPath);

}
