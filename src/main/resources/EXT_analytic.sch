<?xml version="1.0" encoding="utf-8"?>
<iso:schema xmlns="http://purl.oclc.org/dsdl/schematron" 
    xmlns:iso="http://purl.oclc.org/dsdl/schematron"
    queryBinding="xslt2" schemaVersion="ISO19757-3">
    <iso:title>ISO schematron validation file for descriptive extended constraints</iso:title>
    <iso:ns prefix='bpmn' uri='http://www.omg.org/spec/BPMN/20100524/MODEL'/>


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

    <iso:pattern name="EXT.057">
        <iso:rule context="bpmn:subProcess[@triggeredByEvent='true']">
            <iso:assert test="not(bpmn:incoming) and not(bpmn:outgoing)" diagnostics="id">
                EXT.057|An Event Sub-Process MUST NOT have any incoming or outgoing Sequence Flows.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.058">
        <iso:rule context="bpmn:subProcess[@triggeredByEvent='true']">
            <iso:assert test="count(./bpmn:startEvent)=1" diagnostics="id">
                EXT.058|An Event Sub-Process MUST have exactly one Start Event.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.059">
        <iso:rule context="bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent">
            <iso:assert test="count($eventDefinitions)>0" diagnostics="id">
                EXT.059|An Event Sub-Process MUST define at least one of the following EventDefinitions:
                messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition,
                conditionalEventDefinition, signalEventDefinition.
            </iso:assert>
            <iso:assert test="not(./bpmn:linkEventDefinition or ./bpmn:eventDefinitionRef = $linkEventDefinitions/@id)" diagnostics="id">
                EXT.059|An Event Sub-Process MUST define at least one of the following EventDefinitions:
                messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition,
                conditionalEventDefinition, signalEventDefinition.
                A linkEventDefinition is not allowed for Event Sub-Processes.
            </iso:assert>
            <iso:assert test="not(./bpmn:cancelEventDefinition or ./bpmn:eventDefinitionRef = $cancelEventDefinitions/@id)" diagnostics="id">
                EXT.059|An Event Sub-Process MUST define at least one of the following EventDefinitions:
                messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition,
                conditionalEventDefinition, signalEventDefinition.
                A cancelEventDefinition is not allowed for Event Sub-Processes.
            </iso:assert>
            <iso:assert test="not(./bpmn:terminateEventDefinition or ./bpmn:eventDefinitionRef = $terminateEventDefinitions/@id)" diagnostics="id">
                EXT.059|An Event Sub-Process MUST define at least one of the following EventDefinitions:
                messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition,
                conditionalEventDefinition, signalEventDefinition.
                A terminateEventDefinition is not allowed for Event Sub-Processes.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:subProcess[@triggeredByEvent='true']/bpmn:startEvent[@isInterrupting='false']">
            <iso:assert test="not(./bpmn:compensateEventDefinition or ./bpmn:eventDefinitionRef = $compensateEventDefinitions/@id)" diagnostics="id">
                EXT.059|An Event Sub-Process MUST define at least one of the following EventDefinitions:
                messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition,
                conditionalEventDefinition, signalEventDefinition.
                Moreover, a compensateEventDefinition is not allowed for Non-Interrupting StartEvents.
            </iso:assert>
            <iso:assert test="not(./bpmn:errorEventDefinition or ./bpmn:eventDefinitionRef = $errorEventDefinitions/@id)" diagnostics="id">
                EXT.059|An Event Sub-Process MUST define at least one of the following EventDefinitions:
                messageEventDefinition, errorEventDefinition, escalationEventDefinition, compensationEventDefinition,
                conditionalEventDefinition, signalEventDefinition.
                Moreover, an errorEventDefinition is not allowed for Non-Interrupting StartEvents.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.061">
        <iso:rule context="bpmn:adHocSubProcess">
            <iso:assert test="count($activities)>0" diagnostics="id">
                EXT.061|At least one Activity must be contained in an AdHocSubProcess.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.062">
        <iso:rule context="bpmn:adHocSubProcess">
            <!-- Conversations are not allowed by the XSD-->
            <iso:assert test="count(bpmn:startEvent)=0 and count(bpmn:endEvents)=0 and count($chorActivities)=0" diagnostics="id">
                EXT.062|Start Event, End Event, Conversations, Conversation Links and Choreography Activities MUST NOT be used in an AdHocSubProcess.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.091">
        <iso:rule context="bpmn:dataInputAssociation[not(./bpmn:transformation)]">
            <!-- Very complex condition due to combinatorial explosion regarding the combination of itemAwareElements
            and dataStoreReferences and/or dataObjectReferencesdirect usage of itemAwareElements each line represents
             one of the following cases:
                 (no dataObjectReference or dataStoreReference)
              or (dataObjectReference for source - no Reference for target)
              or (no reference for source - dataObjectReference for target)
              or (dataObjectReference for both)
              or (dataStoreReference for source - no reference for target)
              or (no reference for source - dataStoreReference for target)
              or (dataStoreReference for both)
              or (dataObjectReference for source - dataStoreReference for target)
              or (dateStoreReference for source - dataObjectReference for target)
              or (underspecified usage: no itemSubjectRefs used for itemAwareItems)-->
            <iso:assert test="(//bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:sourceRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:targetRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:targetRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:sourceRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:targetRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:sourceRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef)
                or (
                    (($itemAwareNoRef)[@id=current()/bpmn:sourceRef and not(@itemSubjectRef)])
                    or (//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef] and not(@itemSubjectRef)])
                    or (//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef] and not(@itemSubjectRef)])
                    or (($itemAwareNoRef)[@id=current()/bpmn:targetRef and not(@itemSubjectRef)])
                    or (//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef] and not(@itemSubjectRef)])
                    or (//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef] and not(@itemSubjectRef)])
	            )" diagnostics="id">
                EXT.091|sourceRef and targetRef of a DataAssociation must have the same ItemDefinition or a transformation must be present.
            </iso:assert>

        </iso:rule>
        <iso:rule context="bpmn:dataOutputAssociation[not(./bpmn:transformation)]">
            <!-- Very complex condition due to combinatorial explosion regarding the combination of itemAwareElements
            and dataStoreReferences and/or dataObjectReferencesdirect usage of itemAwareElements each line represents
             one of the following cases:
                 (no dataObjectReference or dataStoreReference)
              or (dataObjectReference for source - no Reference for target)
              or (no reference for source - dataObjectReference for target)
              or (dataObjectReference for both)
              or (dataStoreReference for source - no reference for target)
              or (no reference for source - dataStoreReference for target)
              or (dataStoreReference for both)
              or (dataObjectReference for source - dataStoreReference for target)
              or (dateStoreReference for source - dataObjectReference for target)
              or (underspecified usage: no itemSubjectRefs used for itemAwareItems)-->
            <iso:assert test="(//bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:sourceRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:targetRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:targetRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:sourceRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:targetRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=($itemAwareElements)[@id=current()/bpmn:sourceRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef)
                or (//bpmn:itemDefinition[@id=//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef]/@dataStoreRef]/@itemSubjectRef]/@structureRef = //bpmn:itemDefinition[@id=//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef]/@dataObjectRef]/@itemSubjectRef]/@structureRef)
                or (
                    (($itemAwareNoRef)[@id=current()/bpmn:sourceRef and not(@itemSubjectRef)])
                    or (//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:sourceRef] and not(@itemSubjectRef)])
                    or (//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:sourceRef] and not(@itemSubjectRef)])
                    or (($itemAwareNoRef)[@id=current()/bpmn:targetRef and not(@itemSubjectRef)])
                    or (//bpmn:dataObject[@id=//bpmn:dataObjectReference[@id=current()/bpmn:targetRef] and not(@itemSubjectRef)])
                    or (//bpmn:dataStore[@id=//bpmn:dataStoreReference[@id=current()/bpmn:targetRef] and not(@itemSubjectRef)])
	            )" diagnostics="id">
                EXT.091|sourceRef and targetRef of a DataAssociation must have the same ItemDefinition or a transformation must be present.
            </iso:assert>

        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.092">
        <iso:rule context="bpmn:dataInputAssociation[not(./bpmn:transformation)]">
            <iso:assert test="count(bpmn:sourceRef)=1" diagnostics="id">
                EXT.092|If a DataAssociation does not define a Transformation, only a single sourceRef is allowed.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:dataOutputAssociation[not(./bpmn:transformation)]">
            <iso:assert test="count(bpmn:sourceRef)=1" diagnostics="id">
                EXT.092|If a DataAssociation does not define a Transformation, only a single sourceRef is allowed.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.110">
        <iso:rule context="bpmn:boundaryEvent[@cancelActivity='true']">
            <iso:assert test="count($eventDefinitions)>0
                    and count(bpmn:terminateEventDefinition)=0
                    and count(bpmn:linkEventDefinition)=0" diagnostics="id">
                EXT.110|A boundaryEvent with cancelActivity='true' must have at least one eventDefinition but does not allow link or terminate eventDefinitions.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:boundaryEvent[@cancelActivity='false']">
            <iso:assert test="count($eventDefinitions)>0
                    and count(bpmn:terminateEventDefinition)=0
                    and count(bpmn:linkEventDefinition)=0
                    and count(bpmn:errorEventDefinition)=0
                    and count(bpmn:cancelEventDefinition)=0
                    and count(bpmn:compensateEventDefinition)=0" diagnostics="id">
                EXT.110|A boundaryEvent with cancelActivity='false' must have at least one eventDefinition but does not allow link, error, cancel, compensate or terminate eventDefinitions.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.111">
        <iso:rule context="bpmn:boundaryEvent[bpmn:cancelEventDefinition]">
            <iso:assert test="@attachedToRef = $transactions/@id" diagnostics="id">
                EXT.111|A cancel boundary event must be attached to a Transaction.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.112">
        <iso:rule context="bpmn:boundaryEvent[bpmn:incoming]">
            <iso:assert test="false()" diagnostics="id">
                EXT.112|A boundary event must not be target of a Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.113">
        <iso:rule context="bpmn:boundaryEvent[not(bpmn:outgoing)]">
            <iso:assert test="./bpmn:compensateEventDefinition" diagnostics="id">
                EXT.113|A boundary event must be a source of at least a SequenceFlow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:diagnostics>
        <iso:diagnostic id="id"><value-of select="current()/@id" /></iso:diagnostic>
        <iso:diagnostic id="sourceRef"><value-of select="current()/@sourceRef" /></iso:diagnostic>
        <iso:diagnostic id="targetRef"><value-of select="current()/@targetRef" /></iso:diagnostic>
    </iso:diagnostics>    
</iso:schema> 
