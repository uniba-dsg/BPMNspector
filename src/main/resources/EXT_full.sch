<?xml version="1.0" encoding="utf-8"?>
<iso:schema xmlns="http://purl.oclc.org/dsdl/schematron" 
    xmlns:iso="http://purl.oclc.org/dsdl/schematron"
    queryBinding="xslt2" schemaVersion="ISO19757-3">
    <iso:title>ISO schematron validation file for common executable extended constraints</iso:title>
    <iso:ns prefix='bpmn' uri='http://www.omg.org/spec/BPMN/20100524/MODEL'/>
    <iso:ns prefix='xsi' uri='http://www.w3.org/2001/XMLSchema-instance'/>

    <!-- EventDefinitions -->
    <let name="eventDefinitions" value="bpmn:eventDefinitionRef | bpmn:messageEventDefinition | bpmn:timerEventDefinition | bpmn:escalationEventDefinition | bpmn:conditionalEventDefinition | bpmn:linkEventDefinition | bpmn:errorEventDefinition | bpmn:cancelEventDefinition | bpmn:compensateEventDefinition | bpmn:signalEventDefinition | bpmn:terminateEventDefinition" />
    
    <!-- Artifacts -->
    <let name="associations" value="//bpmn:association" />
    <let name="textAnnotations" value="//bpmn:textAnnotation" />
    <let name="groups" value="//bpmn:group" />
    
    <!-- Flows -->
    <let name="sequenceFlows" value="//bpmn:sequenceFlow"/>
    <let name="messageFlows" value="//bpmn:messageFlow"/>
    
    <!-- Activities -->
        <!-- activities as direct children of context -->
        <let name="activities" value="bpmn:task | bpmn:sendTask | bpmn:receiveTask | bpmn:serviceTask | bpmn:userTask | bpmn:manualTask | bpmn:scriptTask | bpmn:businessRuleTask | bpmn:callActivity | bpmn:subProcess | bpmn:transaction | bpmn:adHocSubProcess"/>"

        <!-- chorography activities as direct children of context -->
        <let name="chorActivities" value="bpmn:choreographyTask | bpmn:callChoreography | bpmn:subChoreography" />
        <let name="callActivities" value="//bpmn:callActivity"/>

        <!-- Tasks -->
        <let name="tasks" value="//bpmn:task"/>
        <let name="sendTasks" value="//bpmn:sendTask"/>
        <let name="receiveTasks" value="//bpmn:receiveTask"/>
        <let name="serviceTasks" value="//bpmn:serviceTask"/>
        <let name="userTasks" value="//bpmn:userTask"/>
        <let name="manualTasks" value="//bpmn:manualTask"/>
        <let name="scriptTasks" value="//bpmn:scriptTask"/>
        <let name="businessTasks" value="//bpmn:businessRuleTask"/>

        <!-- SubProcesses -->
        <let name="subProcesses" value="//bpmn:subProcess"/>
        <let name="transactions" value="//bpmn:transaction"/>
        <let name="adHocSubProcesses" value="//bpmn:adHocSubProcess"/>
         
    <!-- Events -->
    <let name="startEvents" value="//bpmn:startEvent"/>
    <let name="boundaryEvents" value="//bpmn:boundaryEvent"/>
    <let name="intermediateCatchEvents" value="//bpmn:intermediateCatchEvent"/>
    <let name="intermediateThrowEvents" value="//bpmn:intermediateThrowEvent"/>
    <let name="endEvents" value="//bpmn:endEvent"/>
    
    <!-- EventDefinitions -->
    <let name="eventDefinitionRefs" value="//bpmn:eventDefinitionRef"/>
    <let name="messageEventDefinitions" value="//bpmn:messageEventDefinition"/>
    <let name="conditionalEventDefinitions" value="//bpmn:conditionalEventDefinition"/>
    <let name="timerEventDefinitions" value="//bpmn:timerEventDefinition"/>
    <let name="linkEventDefinitions" value="//bpmn:linkEventDefinition"/>
    <let name="escalationEventDefinitions" value="//bpmn:escalationEventDefinition"/>
    <let name="errorEventDefinitions" value="//bpmn:errorEventDefinition"/>
    <let name="cancelEventDefinitions" value="//bpmn:cancelEventDefinition"/>
    <let name="compensateEventDefinitions" value="//bpmn:compensateEventDefinition"/>
    <let name="terminateEventDefinitions" value="//bpmn:terminateEventDefinition"/>
    
    <!-- Gateways -->
    <let name="exclusiveGateways" value="//bpmn:exclusiveGateway"/>
    <let name="parallelGateways" value="//bpmn:parallelGateway"/>
    <let name="inclusiveGateways" value="//bpmn:inclusiveGateway"/>
    <let name="complexGateways" value="//bpmn:complexGateway"/>
    <let name="eventBasedGateways" value="//bpmn:eventBasedGateway"/>

    <!-- ItemAwareElements -->
    <let name="itemAwareElements" value="//bpmn:dataObject | //bpmn:dataObjectReference | //bpmn:property | //bpmn:dataStore | //bpmn:dataStoreReference | //bpmn:dataInput | //bpmn:dataOutput" />
    <let name="itemAwareNoRef" value="//bpmn:dataObject | //bpmn:property | //bpmn:dataStore | //bpmn:dataInput | //bpmn:dataOutput" />

    <!-- All -->
    <let name="allElements" value="//bpmn:*"/>

    <iso:pattern name="EXT.027">
        <iso:rule context="bpmn:choreography[bpmn:choreographyRef]">
            <iso:assert test="false()" diagnostics="id">
                EXT.027|A choreography or a GlobalConversation must not reference a choreography.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:globalConversation[bpmn:choreographyRef]">
            <iso:assert test="false()" diagnostics="id">
                EXT.027|A choreography or a GlobalConversation must not reference a choreography.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.029">
        <iso:rule context="bpmn:participantMultiplicity[@minimum]">
            <iso:assert test="@minimum >= 0" diagnostics="id">
                EXT.029|The value of the minimum attribute of the participant multiplicity must not be negative.
            </iso:assert>
        </iso:rule>
    </iso:pattern>


    <iso:pattern name="EXT.030">
        <iso:rule context="bpmn:participantMultiplicity">
            <iso:assert test="(@maximum >= @minimum) or (not(@minimum) and @maximum>0) or (not(@maximum) and @minimum&lt;=1) or (not(@maximum) and not(@minimum))" diagnostics="id">
                EXT.030|The value of maximum MUST be one or greater, AND MUST be equal or greater than the minimum value.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.032">
        <iso:rule context="bpmn:globalConversation">
            <iso:assert test="not(bpmn:conversationNode | bpmn:conversation | bpmn:subConversation | bpmn:callConversation)
                    and not(bpmn:participantAssociation)
                    and not(bpmn:messageFlowAssociation)
                    and not(bpmn:conversationAssociation)
                    and not(bpmn:association | bpmn:group | bpmn:textAnnotation)" diagnostics="id">
                EXT.032|A globalConversation must not contain any ConversationNodes, participantAssociations,
                messageFlowAssociations, conversationAssociations or artifacts.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.033">
        <iso:rule context="bpmn:collaboration[bpmn:choreographyRef]">
            <iso:assert test="@isClosed = //bpmn:choreography[@id=current()/bpmn:choreographyRef]/@isClosed" diagnostics="id">
                EXT.033|If a collaboration references a Choreography the value of the attribute isClosed must be equal for both elements.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.037">
        <iso:rule context="bpmn:*[@startQuantity]">
            <iso:assert test="@startQuantity >= 1" diagnostics="id">
                EXT.037|The value of the startQuantity attribute of an activity MUST NOT be less than 1.
            </iso:assert>
        </iso:rule>
    </iso:pattern>


    <iso:pattern name="EXT.038">
        <iso:rule context="bpmn:*[@completionQuantity]">
            <iso:assert test="@completionQuantity >= 1" diagnostics="id">
                EXT.038|The value of the completionQuantity attribute of an activity MUST NOT be less than 1.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:diagnostics>
        <iso:diagnostic id="id"><value-of select="current()/@id" /></iso:diagnostic>
        <iso:diagnostic id="sourceRef"><value-of select="current()/@sourceRef" /></iso:diagnostic>
        <iso:diagnostic id="targetRef"><value-of select="current()/@targetRef" /></iso:diagnostic>
    </iso:diagnostics>    
</iso:schema> 
