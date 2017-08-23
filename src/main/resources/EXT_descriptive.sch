<?xml version="1.0" encoding="utf-8"?>
<iso:schema xmlns="http://purl.oclc.org/dsdl/schematron"
            xmlns:iso="http://purl.oclc.org/dsdl/schematron"
            queryBinding="xslt2" schemaVersion="ISO19757-3">
    <iso:title>ISO schematron validation file for descriptive extended constraints</iso:title>
    <iso:ns prefix='bpmn' uri='http://www.omg.org/spec/BPMN/20100524/MODEL'/>
    <let name="eventDefinitions" value="bpmn:eventDefinitionRef | bpmn:messageEventDefinition | bpmn:timerEventDefinition | bpmn:escalationEventDefinition | bpmn:conditionalEventDefinition | bpmn:linkEventDefinition | bpmn:errorEventDefinition | bpmn:cancelEventDefinition | bpmn:compensateEventDefinition | bpmn:signalEventDefinition | bpmn:terminateEventDefinition" />
    
    <!-- Artifacts -->
    <let name="associations" value="//bpmn:association" />
    <let name="textAnnotations" value="//bpmn:textAnnotation" />
    <let name="groups" value="//bpmn:group" />
    
    <!-- Flows -->
    <let name="sequenceFlows" value="//bpmn:sequenceFlow"/>
    <let name="messageFlows" value="//bpmn:messageFlow"/>
    
    <!-- Activities -->
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
    
    <!-- All -->
    <let name="allElements" value="//bpmn:*"/>

    <!-- Your constraints go here -->
    <iso:pattern name="EXT.006">
        <iso:rule context="bpmn:sequenceFlow[@targetRef]">
            <iso:assert test="not((./@targetRef = $associations/@id) or (./@targetRef = $textAnnotations/@id) or (./@targetRef = $groups/@id))" diagnostics="id">EXT.006|An Artifact MUST NOT be a target for a Sequence Flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.007">
        <iso:rule context="bpmn:sequenceFlow[@sourceRef]">
            <iso:assert test="not((./@sourceRef = $associations/@id) or (./@sourceRef = $textAnnotations/@id) or (./@sourceRef = $groups/@id))" diagnostics="id">EXT.007|An Artifact MUST NOT be a source for a Sequence Flow</iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.008">
        <iso:rule context="bpmn:messageFlow[@targetRef]">
            <iso:assert test="not((./@targetRef = $associations/@id) or (./@targetRef = $textAnnotations/@id) or (./@targetRef = $groups/@id))" diagnostics="targetRef">EXT.008|An Artifact MUST NOT be a target for a Message Flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.009">
        <iso:rule context="bpmn:messageFlow[@sourceRef]">
            <iso:assert test="not((./@sourceRef = $associations/@id) or (./@sourceRef = $textAnnotations/@id) or (./@sourceRef = $groups/@id))" diagnostics="sourceRef">EXT.009|An Artifact MUST NOT be a source for a Message Flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.021">
        <iso:rule context="bpmn:*[./@id = $sequenceFlows/@sourceRef and ancestor::bpmn:process]">
            <iso:assert test="(./@id = $tasks/@id or ./@id = $sendTasks/@id or ./@id = $receiveTasks/@id or ./@id = $serviceTasks/@id or ./@id = $userTasks/@id or ./@id = $manualTasks/@id or ./@id = $scriptTasks/@id or ./@id = $businessTasks/@id or ./@id = $subProcesses/@id or ./@id = $transactions/@id or ./@id = $adHocSubProcesses/@id or ./@id = $callActivities/@id or ./@id = $startEvents/@id or ./@id = $boundaryEvents/@id or ./@id = $intermediateCatchEvents/@id or ./@id = $intermediateThrowEvents/@id or ./@id = $endEvents/@id or ./@id = $exclusiveGateways/@id or ./@id = $parallelGateways/@id or ./@id = $inclusiveGateways/@id or ./@id = $complexGateways/@id or ./@id = $eventBasedGateways/@id) and not(./@id = $subProcesses[@triggeredByEvent = 'true']/@id)" diagnostics="id">EXT.021|For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.022">
        <iso:rule context="bpmn:*[./@id = $sequenceFlows/@targetRef and ancestor::bpmn:process]">
            <iso:assert test="(./@id = $tasks/@id or ./@id = $sendTasks/@id or ./@id = $receiveTasks/@id or ./@id = $serviceTasks/@id or ./@id = $userTasks/@id or ./@id = $manualTasks/@id or ./@id = $scriptTasks/@id or ./@id = $businessTasks/@id or ./@id = $subProcesses/@id or ./@id = $transactions/@id or ./@id = $adHocSubProcesses/@id or ./@id = $callActivities/@id or ./@id = $startEvents/@id or ./@id = $boundaryEvents/@id or ./@id = $intermediateCatchEvents/@id or ./@id = $intermediateThrowEvents/@id or ./@id = $endEvents/@id or ./@id = $exclusiveGateways/@id or ./@id = $parallelGateways/@id or ./@id = $inclusiveGateways/@id or ./@id = $complexGateways/@id or ./@id = $eventBasedGateways/@id) and not(./@id = $subProcesses[@triggeredByEvent = 'true']/@id)" diagnostics="id">EXT.022|For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.023">
        <iso:rule context="bpmn:sequenceFlow[@targetRef]">
            <iso:assert test="$allElements/@id = ./@targetRef and ./@id = $allElements/bpmn:incoming" diagnostics="id">EXT.023|The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:sequenceFlow[@sourceRef]">
            <iso:assert test="$allElements/@id =./@sourceRef and ./@id = $allElements/bpmn:outgoing" diagnostics="id">EXT.023|The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.025">
        <iso:rule context="bpmn:sequenceFlow[bpmn:conditionExpression] [not(@sourceRef = $exclusiveGateways/@id)] [not(@sourceRef = $parallelGateways/@id)] [not(@sourceRef = $inclusiveGateways/@id)] [not(@sourceRef = $complexGateways/@id)] [not(@sourceRef = $eventBasedGateways/@id)]">
            <iso:assert test="$tasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $sendTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $receiveTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $serviceTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $userTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $manualTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $scriptTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $businessTasks[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $callActivities[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $subProcesses[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $transactions[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1] or $adHocSubProcesses[bpmn:outgoing=(current()/@id)] [count(bpmn:outgoing) > 1]" diagnostics="id">EXT.025|An Activity must not have only one outgoing conditional sequence flow if conditionExpression is present</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.026">
        <iso:rule context="bpmn:exclusiveGateway[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:parallelGateway[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:inclusiveGateway[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:complexGateway[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:eventBasedGateway[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:task[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:sendTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:receiveTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:serviceTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:userTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:manualTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:scriptTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:businessRuleTask[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:subProcess[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transaction[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:adHocSubProcess[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:callActivity[@default]">
            <iso:assert test="$sequenceFlows[@id = current()/@default]/@sourceRef=./@id" diagnostics="id">EXT.026|If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef</iso:assert>
        </iso:rule>        
    </iso:pattern>
    
    <iso:pattern name="EXT.028 and EXT.152">
        <iso:rule context="bpmn:sequenceFlow">
            <iso:assert test="//bpmn:*[@id = current()/@sourceRef and parent::*/@id=//bpmn:*[@id = current()/@targetRef]/../@id]" diagnostics="id">EXT.028,EXT.152|A Sequence Flow must not cross the border of a Pool</iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.031">
        <iso:rule context="bpmn:messageFlow">
        	<iso:assert test="@sourceRef!=@targetRef" diagnostics="sourceRef">EXT.031|A message flow must connect 'InteractionNodes' from different Pools</iso:assert>
        	<iso:assert test="count(//bpmn:process[@id=//bpmn:participant[@id=current()/@sourceRef]/@processRef]/descendant::*[@id=current()/@targetRef])=0" diagnostics="sourceRef">EXT.031|A message flow must connect 'InteractionNodes' from different Pools</iso:assert>
        	<iso:assert test="count(//bpmn:process[@id=//bpmn:participant[@id=current()/@targetRef]/@processRef]/descendant::*[@id=current()/@sourceRef])=0" diagnostics="sourceRef">EXT.031|A message flow must connect 'InteractionNodes' from different Pools</iso:assert>
			<iso:assert test="not(//bpmn:process[*[@id=current()/@sourceRef]]=//bpmn:process[*[@id=current()/@targetRef]])" diagnostics="sourceRef">EXT.031|A message flow must connect 'InteractionNodes' from different Pools</iso:assert>
        </iso:rule>
    </iso:pattern>
         
    <iso:pattern name="EXT.036">
        <iso:rule context="bpmn:process">
            <iso:assert test="not(bpmn:choreographyTask or bpmn:subChoreography or bpmn:callChoreography)" diagnostics="id">EXT.036|A Process must not contain Choreography Activities</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.056">
        <iso:rule context="bpmn:subProcess">
            <iso:assert test="not(bpmn:choreographyTask or bpmn:subChoreography or bpmn:callChoreography)" diagnostics="id">EXT.056|A SubProcess must not contain Choreography Activities</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transaction">
            <iso:assert test="not(bpmn:choreographyTask or bpmn:subChoreography or bpmn:callChoreography)" diagnostics="id">EXT.056|A SubProcess must not contain Choreography Activities</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:adHocSubProcess">
            <iso:assert test="not(bpmn:choreographyTask or bpmn:subChoreography or bpmn:callChoreography)" diagnostics="id">EXT.056|A SubProcess must not contain Choreography Activities</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.076">
        <let name="dataObjects" value="//bpmn:dataObject"/>
        <iso:rule context="bpmn:dataObjectReference[@name]">
            <iso:assert test="$dataObjects/@id = ./@dataObjectRef and ./@name = concat($dataObjects/@name,' [', ./bpmn:dataState/@name, ']')" diagnostics="id">EXT.076|Naming Convention: name = Data Object Name [Data Object Reference State]</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.079">
        <iso:rule context="bpmn:subProcess">
            <iso:assert test="not(bpmn:ioSpecification)" diagnostics="id">EXT.079|InputOutputSpecifications are not allowed in SubProcesses</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transaction">
            <iso:assert test="not(bpmn:ioSpecification)" diagnostics="id">EXT.079|InputOutputSpecifications are not allowed in SubProcesses</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:adHocSubProcess">
            <iso:assert test="not(bpmn:ioSpecification)" diagnostics="id">EXT.079|InputOutputSpecifications are not allowed in SubProcesses</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.084">
        <let name="inputSets" value="//bpmn:inputSet"/>
        <iso:rule context="bpmn:dataInput">
            <iso:assert test="$inputSets/bpmn:dataInputRefs = ./@id" diagnostics="id">EXT.084|A DataInput must be referenced by at least one InputSet</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.088">
        <let name="outputSets" value="//bpmn:outputSet"/>
        <iso:rule context="bpmn:dataOutput">
            <iso:assert test="$outputSets/bpmn:dataOutputRefs = ./@id" diagnostics="id">EXT.088|A DataOutput must be referenced by at least one OutputSet</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.095">
        <iso:rule context="bpmn:endEvent/bpmn:messageEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent/bpmn:escalationEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent/bpmn:errorEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent/bpmn:cancelEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent/bpmn:compensationEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent/bpmn:signalEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent/bpmn:terminateEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent/bpmn:messageEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent/bpmn:escalationEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent/bpmn:linkEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent/bpmn:compensationEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent/bpmn:signalEventDefinition">
            <iso:assert test="not(./@id = $eventDefinitionRefs)" diagnostics="id">EXT.095|EventDefinitions defined in a throw event are not allowed to be used somewhere else</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.096">
        <iso:rule context="bpmn:startEvent">
            <iso:assert test="not(bpmn:incoming)" diagnostics="id">EXT.096|A Start Event must not have an incoming sequence flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.097">
        <iso:rule context="bpmn:endEvent">
            <iso:assert test="parent::*/bpmn:startEvent or parent::*/bpmn:receiveTask[@instantiate='true']" diagnostics="id">EXT.097|A Start event (or an instantiating ReceiveTask) must be present when an End event is used in the same process level</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.098">
        <iso:rule context="bpmn:startEvent[parent::bpmn:process]">
            <iso:assert test="not(./bpmn:linkEventDefinition or ./bpmn:eventDefinitionRef = $linkEventDefinitions/@id)" diagnostics="id">EXT.098|Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events</iso:assert>
            <iso:assert test="not(./bpmn:escalationEventDefinition or ./bpmn:eventDefinitionRef = $escalationEventDefinitions/@id)" diagnostics="id">EXT.098|Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events</iso:assert>
            <iso:assert test="not(./bpmn:errorEventDefinition or ./bpmn:eventDefinitionRef = $errorEventDefinitions/@id)" diagnostics="id">EXT.098|Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events</iso:assert>
            <iso:assert test="not(./bpmn:cancelEventDefinition or ./bpmn:eventDefinitionRef = $cancelEventDefinitions/@id)" diagnostics="id">EXT.098|Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events</iso:assert>
            <iso:assert test="not(./bpmn:compensateEventDefinition or ./bpmn:eventDefinitionRef = $compensateEventDefinitions/@id)" diagnostics="id">EXT.098|Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events</iso:assert>
            <iso:assert test="not(./bpmn:terminateEventDefinition or ./bpmn:eventDefinitionRef = $terminateEventDefinitions/@id)" diagnostics="id">EXT.098|Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.099">
        <iso:rule context="bpmn:process[./@id = //bpmn:callActivity/@calledElement]">
            <iso:assert test="not(count(./bpmn:startEvent) = (count(./bpmn:startEvent/bpmn:eventDefinitionRef/..) + count(./bpmn:startEvent/bpmn:messageEventDefinition/..) + count(./bpmn:startEvent/bpmn:timerEventDefinition/..) + count(./bpmn:startEvent/bpmn:escalationEventDefinition/..) + count(./bpmn:startEvent/bpmn:conditionalEventDefinition/..) + count(./bpmn:startEvent/bpmn:linkEventDefinition/..) + count(./bpmn:startEvent/bpmn:errorEventDefinition/..) + count(./bpmn:startEvent/bpmn:cancelEventDefinition/..) + count(./bpmn:startEvent/bpmn:compensationEventDefinition/..) + count(./bpmn:startEvent/bpmn:signalEventDefinition/..) + count(./bpmn:startEvent/bpmn:terminateEventDefinition/..)))" diagnostics="id">EXT.099|Referenced process must have at least one None Start Event</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.100">
        <iso:rule context="bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent">
            <iso:assert test="not(./$eventDefinitions)" diagnostics="id">EXT.100|No EventDefinition is allowed for Start Events in Sub-Process definitions</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transaction/bpmn:startEvent">
            <iso:assert test="not(./$eventDefinitions)" diagnostics="id">EXT.100|No EventDefinition is allowed for Start Events in Sub-Process definitions</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:adHocSubProcess/bpmn:startEvent">
            <iso:assert test="not(./$eventDefinitions)" diagnostics="id">EXT.100|No EventDefinition is allowed for Start Events in Sub-Process definitions</iso:assert>
        </iso:rule>
    </iso:pattern>
        
    <iso:pattern name="EXT.101">
        <iso:rule context="bpmn:startEvent">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.101|A startEvent must have a outgoing subelement</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.102">
        <iso:rule context="bpmn:messageFlow[@sourceRef]">
            <iso:assert test="not(./@sourceRef = $startEvents/@id)" diagnostics="sourceRef">EXT.102|A Start Event MUST NOT be a source for a message flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.103">
        <iso:rule context="bpmn:startEvent[@id = $messageFlows/@targetRef]">
            <iso:assert test="./bpmn:messageEventDefinition or ./bpmn:eventDefinitionRef = $messageEventDefinitions/@id" diagnostics="id">EXT.103|If a Start Event is target of a MessageFlow definition, at least one messageEventDefinition must be present</iso:assert>
        </iso:rule>
    </iso:pattern>
       
    <iso:pattern name="EXT.104">
        <iso:rule context="bpmn:endEvent">
            <iso:assert test="not(bpmn:outgoing)" diagnostics="id">EXT.104|An End Event must not have an outgoing sequence flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.105">
        <iso:rule context="bpmn:startEvent">
            <iso:assert test="parent::*/bpmn:endEvent" diagnostics="id">EXT.105|An end event must be present when a start event is used in the same process level</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.106">
        <iso:rule context="bpmn:endEvent[./bpmn:cancelEventDefinition]">
            <iso:assert test="ancestor::bpmn:transaction" diagnostics="id">EXT.106|A cancel EndEvent is only allowed in a transaction sub-process</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.107">
        <iso:rule context="bpmn:endEvent">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.107|An End Event MUST have at least one incoming Sequence Flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.108">
        <iso:rule context="bpmn:messageFlow[@targetRef]">
            <iso:assert test="not(./@targetRef = $endEvents/@id)" diagnostics="targetRef">EXT.108|An End Event MUST NOT be a target for a message flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.109">
        <iso:rule context="bpmn:endEvent[@id = $messageFlows/@sourceRef]">
            <iso:assert test="./bpmn:messageEventDefinition or ./bpmn:eventDefinitionRef = $messageEventDefinitions/@id" diagnostics="id">EXT.109|If an end event is source of a MessageFlow definition, at least one messageEventDefinition must be present</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.135">
        <iso:rule context="bpmn:parallelGateway">
            <iso:assert test="count(bpmn:incoming) > 1 or count(bpmn:outgoing) > 1" diagnostics="id">EXT.135|A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:exclusiveGateway">
            <iso:assert test="count(bpmn:incoming) > 1 or count(bpmn:outgoing) > 1" diagnostics="id">EXT.135|A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:inclusiveGateway">
            <iso:assert test="count(bpmn:incoming) > 1 or count(bpmn:outgoing) > 1" diagnostics="id">EXT.135|A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:complexGateway">
            <iso:assert test="count(bpmn:incoming) > 1 or count(bpmn:outgoing) > 1" diagnostics="id">EXT.135|A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:eventBasedGateway">
            <iso:assert test="count(bpmn:incoming) > 1 or count(bpmn:outgoing) > 1" diagnostics="id">EXT.135|A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows</iso:assert>
        </iso:rule>
        
    </iso:pattern>
    
    <iso:pattern name="EXT.146">
        <iso:rule context="bpmn:endEvent">
            <iso:assert test="not(./bpmn:linkEventDefinition or ./bpmn:eventDefinitionRef = $linkEventDefinitions/@id)" diagnostics="id">EXT.146|Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events</iso:assert>
            <iso:assert test="not(./bpmn:conditionalEventDefinition or ./bpmn:eventDefinitionRef = $conditionalEventDefinitions/@id)" diagnostics="id">EXT.146|Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events</iso:assert>
            <iso:assert test="not(./bpmn:timerEventDefinition or ./bpmn:eventDefinitionRef = $timerEventDefinitions/@id)" diagnostics="id">EXT.146|Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.150">
        <iso:rule context="bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:sendTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:receiveTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:serviceTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:userTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:manualTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:scriptTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:businessRuleTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transaction[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:adHocSubProcess[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:callActivity[@isForCompensation = 'false'] [parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:exclusiveGateway[parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:parallelGateway[parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:inclusiveGateway[parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:complexGateway[parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:eventBasedGatewayGateway[parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent[parent::*/bpmn:startEvent]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[parent::*/bpmn:startEvent] [not(./bpmn:linkEventDefinition or ./bpmn:eventDefinitionRef = $linkEventDefinitions/@id)]">
            <iso:assert test="bpmn:incoming" diagnostics="id">EXT.150|If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.151">
        <iso:rule context="bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:sendTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:receiveTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:serviceTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:userTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:manualTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:scriptTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:businessRuleTask[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transaction[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:adHocSubProcess[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:callActivity[@isForCompensation = 'false'] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[./bpmn:linkEventDefinition or ./bpmn:eventDefinitionRef = $linkEventDefinitions/@id] [parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:exclusiveGateway[parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:parallelGateway[parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:inclusiveGateway[parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:complexGateway[parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:eventBasedGatewayGateway[parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[parent::*/bpmn:endEvent]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateThrowEvent[parent::*/bpmn:endEvent] [not(./bpmn:linkEventDefinition or ./bpmn:eventDefinitionRef = $linkEventDefinitions/@id)]">
            <iso:assert test="bpmn:outgoing" diagnostics="id">EXT.151|If end events are used, all flow nodes must have an outgoing sequence flow</iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:diagnostics>
        <iso:diagnostic id="id"><value-of select="current()/@id" /></iso:diagnostic>
        <iso:diagnostic id="sourceRef"><value-of select="current()/@sourceRef" /></iso:diagnostic>
        <iso:diagnostic id="targetRef"><value-of select="current()/@targetRef" /></iso:diagnostic>
    </iso:diagnostics>    
</iso:schema> 
