package de.uniba.dsg.bpmnspector.common.importer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.*;

public class BPMNProcessTest {

    static BPMNProcess simpleProcess;
    static BPMNProcess complexProcess;

    @BeforeClass
    public static void setUpTestProcesses() throws JDOMException, IOException {
        simpleProcess = createSimpleBpmnProcess();
        complexProcess = createComplexProcess();
    }

    @Test(expected = NullPointerException.class)
    public void findElementByIdUsingNullIsRejected() throws JDOMException, IOException {
        simpleProcess.findElementById(null);
    }

    @Test
    public void findElementByIdReturnsEmptyOptional() throws JDOMException, IOException {
        assertEquals(Optional.empty(), simpleProcess.findElementById("unknownID"));
    }

    @Test
    public void findElementByIdFindsCorrectElement() throws JDOMException, IOException {
        Element foundElem = simpleProcess.findElementById("_4").get();

        assertEquals("sequenceFlow", foundElem.getName());
        assertEquals("_4", foundElem.getAttribute("id").getValue());
    }

    @Test
    public void multiProcessFileIsDetectedCorrectly() {
        assertTrue(complexProcess.isMultiProcessModel());
    }

    @Test
    public void simpleProcessIsNoMultiProcessModel() {
        assertFalse(simpleProcess.isMultiProcessModel());
    }

    @Test
    public void complexProcessHasConditionalSequenceFlows() {
        assertTrue(complexProcess.hasConditionalSeqFlowTasks());
    }

    @Test
    public void simpleProcessHasNoConditionalSequenceFlows() {
        assertFalse(simpleProcess.hasConditionalSeqFlowTasks());
    }

    @Test
    public void complexProcessHasBoundaryEvents() {
        assertTrue(complexProcess.hasBoundaryEvents());
    }

    @Test
    public void simpleProcessHasNoBoundaryEvents() {
        assertFalse(simpleProcess.hasBoundaryEvents());
    }

    private static BPMNProcess createSimpleBpmnProcess() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" " +
                "       xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" " +
                "       xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "       expressionLanguage=\"http://www.w3.org/1999/XPath\" id=\"_1533014286776\" name=\"\" " +
                "       targetNamespace=\"targetNS\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\">\n"+
                "  <process id=\"PROCESS_1\" isClosed=\"false\" isExecutable=\"true\" processType=\"None\">\n"+
                "    <task completionQuantity=\"1\" id=\"_3\" isForCompensation=\"false\" name=\"Task\" startQuantity=\"1\">\n"+
                "      <incoming>_4</incoming>\n"+
                "      <outgoing>_6</outgoing>\n"+
                "    </task>\n"+
                "    <sequenceFlow id=\"_4\" sourceRef=\"_2\" targetRef=\"_3\"/>\n"+
                "    <startEvent id=\"_2\" isInterrupting=\"true\" name=\"Start Event\" parallelMultiple=\"false\">\n"+
                "      <outgoing>_4</outgoing>\n"+
                "      <outputSet/>\n"+
                "    </startEvent>\n"+
                "    <endEvent id=\"_5\" name=\"End Event\">\n"+
                "      <incoming>_6</incoming>\n"+
                "      <inputSet/>\n"+
                "    </endEvent>\n"+
                "    <sequenceFlow id=\"_6\" sourceRef=\"_3\" targetRef=\"_5\"/>\n"+
                "  </process>\n"+
                "</definitions>\n").getBytes(StandardCharsets.UTF_8)));

        return new BPMNProcess(doc, "C:\\bla.bpmn", "targetNS");
    }

    private static BPMNProcess createComplexProcess() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" " +
                "       xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" " +
                "       xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "       expressionLanguage=\"http://www.w3.org/1999/XPath\" id=\"_1533014286776\" name=\"\" " +
                "       targetNamespace=\"targetNS\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\">\n"+
                "  <collaboration id=\"COLLABORATION_1\" isClosed=\"false\">\n" +
                "    <participant id=\"_7\" name=\"Participant\" processRef=\"PROCESS_1\">\n" +
                "      <participantMultiplicity maximum=\"1\" minimum=\"0\"/>\n" +
                "    </participant>\n" +
                "    <participant id=\"_8\" name=\"Participant\">\n" +
                "      <participantMultiplicity maximum=\"1\" minimum=\"0\"/>\n" +
                "    </participant>\n" +
                "    <messageFlow id=\"_9\" sourceRef=\"_3\" targetRef=\"_8\"/>\n" +
                "  </collaboration>\n" +
                "  <process id=\"PROCESS_1\" isClosed=\"false\" isExecutable=\"true\" processType=\"None\">\n" +
                "    <startEvent id=\"_2\" isInterrupting=\"true\" name=\"Start Event\" parallelMultiple=\"false\">\n" +
                "      <outgoing>_4</outgoing>\n" +
                "      <outputSet/>\n" +
                "    </startEvent>\n" +
                "    <task completionQuantity=\"1\" id=\"_3\" isForCompensation=\"false\" name=\"Task\" startQuantity=\"1\">\n" +
                "      <incoming>_4</incoming>\n" +
                "      <outgoing>_6</outgoing>\n" +
                "      <outgoing>_14</outgoing>\n" +
                "    </task>\n" +
                "    <sequenceFlow id=\"_4\" sourceRef=\"_2\" targetRef=\"_3\"/>\n" +
                "    <sequenceFlow id=\"_6\" sourceRef=\"_3\" targetRef=\"_5\"/>\n" +
                "    <boundaryEvent attachedToRef=\"_3\" cancelActivity=\"true\" id=\"_10\" name=\"Boundary Event\" parallelMultiple=\"false\">\n" +
                "      <outgoing>_12</outgoing>\n" +
                "      <outputSet/>\n" +
                "      <timerEventDefinition id=\"_10_ED_1\"/>\n" +
                "    </boundaryEvent>\n" +
                "    <endEvent id=\"_11\" name=\"End Event\">\n" +
                "      <incoming>_12</incoming>\n" +
                "      <inputSet/>\n" +
                "    </endEvent>\n" +
                "    <sequenceFlow id=\"_12\" sourceRef=\"_10\" targetRef=\"_11\"/>\n" +
                "    <endEvent id=\"_5\" name=\"End Event\">\n" +
                "      <incoming>_6</incoming>\n" +
                "      <inputSet/>\n" +
                "    </endEvent>\n" +
                "    <endEvent id=\"_13\" name=\"End Event\">\n" +
                "      <incoming>_14</incoming>\n" +
                "      <inputSet/>\n" +
                "    </endEvent>\n" +
                "    <sequenceFlow id=\"_14\" sourceRef=\"_3\" targetRef=\"_13\">\n" +
                "      <conditionExpression><![CDATA[condition!]]></conditionExpression>\n" +
                "    </sequenceFlow>\n" +
                "  </process>"+
                "</definitions>\n").getBytes(StandardCharsets.UTF_8)));

        return new BPMNProcess(doc, "C:\\bla.bpmn", "targetNS");
    }


}
