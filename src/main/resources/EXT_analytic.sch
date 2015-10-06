<?xml version="1.0" encoding="utf-8"?>
<iso:schema xmlns="http://purl.oclc.org/dsdl/schematron" 
    xmlns:iso="http://purl.oclc.org/dsdl/schematron"
    queryBinding="xslt2" schemaVersion="ISO19757-3">
    <iso:title>ISO schematron validation file for descriptive extended constraints</iso:title>
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

    <iso:pattern name="EXT.114">
        <iso:rule context="bpmn:boundaryEvent[bpmn:compensateEventDefinition]">
            <iso:assert test="not(bpmn:outgoing)" diagnostics="id">
                EXT.114|A compensation boundary event MUST NOT have an outgoing Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.115">
        <iso:rule context="bpmn:intermediateThrowEvent[not(bpmn:incoming)]">
            <iso:assert test="false()" diagnostics="id">
                EXT.115|Intermediate Events MUST be a target of at least a Sequence Flow.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[not(bpmn:linkEventDefinition) and not(bpmn:incoming)]">
            <iso:assert test="false()" diagnostics="id">
                EXT.115|Intermediate Events MUST be a target of at least a Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.116">
        <iso:rule context="bpmn:intermediateThrowEvent[not(bpmn:linkEventDefinition) and not(bpmn:outgoing)]">
            <iso:assert test="false()" diagnostics="id">
                EXT.116|Intermediate Events MUST be a source of at least a Sequence Flow.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[not(bpmn:outgoing)]">
            <iso:assert test="false()" diagnostics="id">
                EXT.116|Intermediate Events MUST be a source of at least a Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.117">
        <iso:rule context="bpmn:intermediateThrowEvent[bpmn:linkEventDefinition and bpmn:outgoing]">
            <iso:assert test="false()" diagnostics="id">
                EXT.117|A Link Intermediate Event MUST NOT be both a target and a source of a Sequence Flow.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[bpmn:linkEventDefinition and bpmn:incoming]">
            <iso:assert test="false()" diagnostics="id">
                EXT.117|A Link Intermediate Event MUST NOT be both a target and a source of a Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.118">
        <iso:rule context="bpmn:linkEventDefinition[bpmn:target]">
            <iso:assert test="//bpmn:linkEventDefinition[@id=current()/bpmn:target]/bpmn:source = current()/@id" diagnostics="id">
                EXT.118|For each source Link there must exist a corresponding target. There may be multiple sources for one target.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.119">
        <iso:rule context="bpmn:intermediateThrowEvent[bpmn:messageEventDefinition]">
            <iso:assert test="not(//bpmn:messageFlow[@targetRef=current()/@id])" diagnostics="id">
                EXT.119|An intermediateThrowEvent must not be a target of a messageFlow.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[bpmn:messageEventDefinition]">
            <iso:assert test="not(//bpmn:messageFlow[@sourceRef=current()/@id])" diagnostics="id">
                EXT.119|An intermediateCatchEvent must not be a source of a messageFlow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.122">
        <iso:rule context="bpmn:intermediateCatchEvent[bpmn:compensateEventDefinition]">
            <iso:assert test="false()" diagnostics="id">
                EXT.122|A intermediateCatchEvent in normal flow must not contain a compensateEventDefinition.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.123">
        <iso:rule context="bpmn:conditionalEventDefinition[ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:condition/node() and (bpmn:condition/@xsi:type='tFormalExpression' or substring-after(bpmn:condition/@xsi:type, ':')='tFormalExpression')" diagnostics="id">
                EXT.123|For executable Processes (isExecutable = true), if the trigger is Conditional, then a
                FormalExpression MUST be entered.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.124">
        <iso:rule context="bpmn:linkEventDefinition[parent::bpmn:intermediateCatchEvent]">
            <iso:assert test="count(bpmn:source)>0" diagnostics="id">
                EXT.124|A LinkEventDefinition in a Catch Event must have at least one source Element.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.125">
        <iso:rule context="bpmn:linkEventDefinition[parent::bpmn:intermediateThrowEvent]">
            <iso:assert test="count(bpmn:target)=1" diagnostics="id">
                EXT.125|A LinkEventDefinition in a Throw Event must have exactly one target Element.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.126">
        <iso:rule context="bpmn:linkEventDefinition[parent::bpmn:intermediateThrowEvent and bpmn:target]">
            <iso:assert test="parent::*/parent::* =//bpmn:linkEventDefinition[./bpmn:source=current()/@id]/parent::*/parent::*" diagnostics="id">
                EXT.126|Links are only allowed to a target in the same process and process level.
            </iso:assert>
        </iso:rule>
    </iso:pattern>


    <iso:pattern name="EXT.136">
        <iso:rule context="bpmn:eventBasedGateway">
            <iso:assert test="count(bpmn:outgoing)>=2" diagnostics="id">
                EXT.136|An Event Gateway MUST have two or more outgoing Sequence Flows.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.137">
        <iso:rule context="bpmn:sequenceFlow[@id=//bpmn:eventBasedGateway/bpmn:outgoing]">
            <iso:assert test="not(bpmn:conditionExpression)" diagnostics="id">
                EXT.137|The outgoing Sequence Flows of the Event Gateway MUST NOT have a conditionExpression.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.138">
        <iso:rule context="bpmn:sequenceFlow[@id=//bpmn:eventBasedGateway/bpmn:outgoing]">
            <iso:assert test="(//bpmn:receiveTask[@id=current()/@targetRef])
                    or (//bpmn:intermediateCatchEvent[(bpmn:messageEventDefinition or bpmn:signalEventDefinition or
                                bpmn:timerEventDefinition or bpmn:conditionalEventDefinition) and
                                @id=current()/@targetRef])" diagnostics="id">
                EXT.138|An eventBasedGateway may only be connected to a ReceiveTask or one of the following intermediate
                Events: Message, Signal, Timer, Conditional, and Multiple (which can only include the previous triggers)
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.139">
        <iso:rule context="bpmn:eventBasedGateway[bpmn:outgoing=//bpmn:sequenceFlow[@targetRef=//bpmn:receiveTask/@id]/@id]">
            <iso:assert test="not(//bpmn:intermediateCatchEvent[@id=//bpmn:sequenceFlow[@id=current()/bpmn:outgoing]/@targetRef and bpmn:messageEventDefinition])" diagnostics="id">
                EXT.139|If Message Intermediate Events are used in the configuration, then Receive Tasks MUST NOT be
                used in that configuration and vice versa.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.140">
        <iso:rule context="bpmn:receiveTask[@id=//bpmn:sequenceFlow[@id=//bpmn:eventBasedGateway/bpmn:outgoing]/@targetRef]">
            <iso:assert test="not(//bpmn:boundaryEvent[@attachedToRef=current()/@id])" diagnostics="id">
                EXT.140|Receive Tasks used in an Event Gateway configuration MUST NOT have any attached Intermediate Events.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.141">
        <iso:rule context="bpmn:*[@id=//bpmn:sequenceFlow[@id=//bpmn:eventBasedGateway/bpmn:outgoing]/@targetRef]">
            <iso:assert test="count(bpmn:incoming) &lt; 2" diagnostics="id">
                EXT.141|Targets of an EventBasedGateway must not have any other incoming SequenceFlow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.142">
        <iso:rule context="bpmn:eventBasedGateway[@instantiate='true']">
            <iso:assert test="count(bpmn:incoming) = 0" diagnostics="id">
                EXT.142|When an EventBasedGateway is used to instantiate a process instance no Incoming
                Sequence Flow is allowed.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.143">
        <iso:rule context="bpmn:boundaryEvent[bpmn:compensateEventDefinition]">
            <iso:assert test="//bpmn:association[@sourceRef=current()/@id]" diagnostics="id">
                EXT.143|A compensateBoundaryEvent must be connected with an Association to a Compensation Activity.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.144">
        <iso:rule context="bpmn:boundaryEvent[bpmn:compensateEventDefinition]">
            <iso:assert test="//bpmn:*[@id=//bpmn:association[@sourceRef=current()/@id]/@targetRef]/@isForCompensation='true'" diagnostics="id">
                EXT.144|The associated Activity must be a Task or a Sub-Process which is marked for compensation
                (i.e., isForCompensation=true)
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.148">
        <iso:rule context="bpmn:intermediateCatchEvent">
            <iso:assert test="not(count($eventDefinitions)=0 or bpmn:escalationEventDefinition
                or bpmn:errorEventDefinition or bpmn:cancelEventDefinition or bpmn:compensateEventDefinition or bpmn:terminateEventDefinition)" diagnostics="id">
                EXT.148|Only messageEventDefinitions, timerEventDefinitions, conditionalEventDefinitions,
                linkEventDefinitions and signalEventDefinitions are allowed for intermediate catch events.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.149">
        <iso:rule context="bpmn:intermediateThrowEvent[bpmn:timerEventDefinition or bpmn:errorEventDefinition or bpmn:cancelEventDefinition or bpmn:conditionalEventDefinition or bpmn:terminateEventDefinition]">
            <iso:assert test="false()" diagnostics="id">
                EXT.149|Only messageEventDefinitions, escalationEventDefinitions, compensationEventDefinitions,
                linkEventDefinitions and signalEventDefinitions are allowed for intermediate throw events.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:diagnostics>
        <iso:diagnostic id="id"><value-of select="current()/@id" /></iso:diagnostic>
        <iso:diagnostic id="sourceRef"><value-of select="current()/@sourceRef" /></iso:diagnostic>
        <iso:diagnostic id="targetRef"><value-of select="current()/@targetRef" /></iso:diagnostic>
    </iso:diagnostics>    
</iso:schema> 
