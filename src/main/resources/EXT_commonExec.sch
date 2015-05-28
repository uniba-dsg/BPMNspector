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

    <iso:pattern name="EXT.011">
        <iso:rule context="bpmn:intermediateThrowEvent[bpmn:escalationEventDefinition and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:escalationEventDefinition/@escalationRef and //bpmn:escalation[@id=current()/bpmn:escalationEventDefinition/@escalationRef]/@escalationCode" diagnostics="id">
                EXT.011|An escalationCode must be present if the escalation is used in an EndEvent or in an intermediate
                throw Event if the trigger is an Escalation.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent[bpmn:escalationEventDefinition and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:escalationEventDefinition/@escalationRef and //bpmn:escalation[@id=current()/bpmn:escalationEventDefinition/@escalationRef]/@escalationCode" diagnostics="id">
                EXT.011|An escalationCode must be present if the escalation is used in an EndEvent or in an intermediate
                throw Event if the trigger is an Escalation.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.012">
        <iso:rule context="bpmn:adHocSubProcess[bpmn:completionCondition and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:completionCondition/@xsi:type='tFormalExpression' or substring-after(bpmn:completionCondition/@xsi:type, ':')='tFormalExpression'" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:assignment[ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="(bpmn:from/@xsi:type='tFormalExpression' or substring-after(bpmn:from/@xsi:type, ':')='tFormalExpression')
                    and (bpmn:to/@xsi:type='tFormalExpression' or substring-after(bpmn:completionCondition/@xsi:type, ':')='tFormalExpression')" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:complexGateway[bpmn:activationCondition and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:activationCondition/@xsi:type='tFormalExpression' or substring-after(bpmn:activationCondition/@xsi:type, ':')='tFormalExpression'" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:multiInstanceLoopCharacteristics[bpmn:loopCardinality and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:loopCardinality/@xsi:type='tFormalExpression' or substring-after(bpmn:loopCardinality/@xsi:type, ':')='tFormalExpression'" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:multiInstanceLoopCharacteristics[bpmn:completionCondition and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:completionCondition/@xsi:type='tFormalExpression' or substring-after(bpmn:completionCondition/@xsi:type, ':')='tFormalExpression'" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:sequenceFlow[bpmn:conditionExpression and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:conditionExpression/@xsi:type='tFormalExpression' or substring-after(bpmn:conditionExpression/@xsi:type, ':')='tFormalExpression'" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:standardLoopCharacteristics[bpmn:loopCondition and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:loopCondition/@xsi:type='tFormalExpression' or substring-after(bpmn:loopCondition/@xsi:type, ':')='tFormalExpression'" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:timerEventDefinition[(bpmn:timeDate or bpmn:timeDuration or bpmn:timeCycle) and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="(bpmn:timeDate/@xsi:type='tFormalExpression' or substring-after(bpmn:timeDate/@xsi:type, ':')='tFormalExpression')
                    or (bpmn:timeDuration/@xsi:type='tFormalExpression' or substring-after(bpmn:timeDuration/@xsi:type, ':')='tFormalExpression')
                    or (bpmn:timeCycle/@xsi:type='tFormalExpression' or substring-after(bpmn:timeCycle/@xsi:type, ':')='tFormalExpression')" diagnostics="id">
                EXT.012|If natural-language expressions are used the process is not executable. A FormalExpression has
                to be provided or the process must be marked as isExecutable='false'.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.013">
        <iso:rule context="bpmn:*[contains(@*[local-name()='type'],'tFormalExpression')]">
            <iso:assert test="text()" diagnostics="id">
                EXT.013|According to constraint CARD.064 (see Sec. 3.1) body is a mandatory attribute
                of a FormalExpression. Therefore the element must contain any Expression as
                a string content.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:condition[parent::bpmn:complexBehaviorDefinition]">
            <iso:assert test="text()" diagnostics="id">
                EXT.013|According to constraint CARD.064 (see Sec. 3.1) body is a mandatory attribute
                of a FormalExpression. Therefore the element must contain any Expression as
                a string content.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:dataPath">
            <iso:assert test="text()" diagnostics="id">
                EXT.013|According to constraint CARD.064 (see Sec. 3.1) body is a mandatory attribute
                of a FormalExpression. Therefore the element must contain any Expression as
                a string content.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:messagePath">
            <iso:assert test="text()" diagnostics="id">
                EXT.013|According to constraint CARD.064 (see Sec. 3.1) body is a mandatory attribute
                of a FormalExpression. Therefore the element must contain any Expression as
                a string content.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:transformation">
            <iso:assert test="text()" diagnostics="id">
                EXT.013|According to constraint CARD.064 (see Sec. 3.1) body is a mandatory attribute
                of a FormalExpression. Therefore the element must contain any Expression as
                a string content.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.017">
        <iso:rule context="bpmn:*[@gatewayDirection='Converging']">
            <iso:assert test="count(bpmn:outgoing)&lt;2" diagnostics="id">
                EXT.017|A converging Gateway must not have more than one outgoing Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.018">
        <iso:rule context="bpmn:*[@gatewayDirection='Diverging']">
            <iso:assert test="count(bpmn:incoming)&lt;2" diagnostics="id">
                EXT.018|A diverging Gateway must not have more than one incoming Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.019">
        <iso:rule context="bpmn:*[@gatewayDirection='Mixed']">
            <iso:assert test="count(bpmn:incoming)>=2 and count(bpmn:outgoing)>=2" diagnostics="id">
                EXT.019|A mixed Gateway must have more than one incoming and more than one outgoing Sequence Flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.020">
        <iso:rule context="bpmn:dataObject[@itemSubjectRef]">
            <iso:assert test="(@isCollection=//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection)
                    or (@isCollection='false' and not(//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection))
                    or (not(@isCollection) and //bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection='false')
                    or (not(@isCollection) and not(//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection))" diagnostics="id">
                EXT.020|The attribute isCollection must match the definition of the referenced itemDefinition.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:dataOutput[@itemSubjectRef]">
            <iso:assert test="(@isCollection=//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection)
                    or (@isCollection='false' and not(//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection))
                    or (not(@isCollection) and //bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection='false')
                    or (not(@isCollection) and not(//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection))" diagnostics="id">
                EXT.020|The attribute isCollection must match the definition of the referenced itemDefinition.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:dataInput[@itemSubjectRef]">
            <iso:assert test="(@isCollection=//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection)
                    or (@isCollection='false' and not(//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection))
                    or (not(@isCollection) and //bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection='false')
                    or (not(@isCollection) and not(//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@isCollection))" diagnostics="id">
                EXT.020|The attribute isCollection must match the definition of the referenced itemDefinition.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.024">
        <iso:rule context="bpmn:sequenceFlow[@isImmediate='false']">
            <iso:assert test="ancestor::bpmn:process/@isExecutable='false'" diagnostics="id">
                EXT.024|The optional attribute isImmediate must not be 'false' for executable processes.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.035">
        <iso:rule context="bpmn:process[@processType='Public']">
            <iso:assert test="@isExecutable='false'" diagnostics="id">
                EXT.024|A Public process may not be marked as executable.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:diagnostics>
        <iso:diagnostic id="id"><value-of select="current()/@id" /></iso:diagnostic>
        <iso:diagnostic id="sourceRef"><value-of select="current()/@sourceRef" /></iso:diagnostic>
        <iso:diagnostic id="targetRef"><value-of select="current()/@targetRef" /></iso:diagnostic>
    </iso:diagnostics>    
</iso:schema> 
