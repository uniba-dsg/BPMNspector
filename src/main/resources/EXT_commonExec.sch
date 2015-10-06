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
                    and (bpmn:to/@xsi:type='tFormalExpression' or substring-after(bpmn:to/@xsi:type, ':')='tFormalExpression')" diagnostics="id">
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
                EXT.035|A Public process may not be marked as executable.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.042">
        <iso:rule context="bpmn:serviceTask[@operationRef]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:inputSet)=1" diagnostics="id">
                EXT.042|If a ServiceTask references an operation, exactly one InputSet must be defined in the ioSpecification.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.043">
        <iso:rule context="bpmn:serviceTask[@operationRef]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:outputSet)&lt;2" diagnostics="id">
                EXT.043|If a ServiceTask references an operation, at most one OutputSet can be defined in the ioSpecification.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.044">
        <iso:rule context="bpmn:serviceTask[@operationRef and bpmn:ioSpecification/bpmn:dataInput]">
            <iso:assert test="(bpmn:ioSpecification/bpmn:dataInput/@itemSubjectRef =
                            //bpmn:message[@id=//bpmn:inMessageRef[parent::bpmn:operation[@id=current()/@operationRef]]]/@itemRef)
                    or (//bpmn:itemDefinition[@id = current()/bpmn:ioSpecification/bpmn:dataInput/@itemSubjectRef]/@structureRef =
                        //bpmn:itemDefinition[@id = //bpmn:message[@id=//bpmn:inMessageRef[parent::bpmn:operation[@id=current()/@operationRef]]]/@itemRef]/@structureRef)" diagnostics="id">
                EXT.044|The ItemDefinition of the DataInput of the ServiceTask and the inMessage itemDefinition of the referenced
                Operation must be equal.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.045">
        <iso:rule context="bpmn:serviceTask[@operationRef]">
            <iso:assert test="not(//bpmn:operation[@id=current()/@operationRef]/bpmn:outMessageRef)
                    or ((bpmn:ioSpecification/bpmn:dataOutput/@itemSubjectRef =
                            //bpmn:message[@id=//bpmn:outMessageRef[parent::bpmn:operation[@id=current()/@operationRef]]]/@itemRef)
                    or (//bpmn:itemDefinition[@id = current()/bpmn:ioSpecification/bpmn:dataOutput/@itemSubjectRef]/@structureRef =
                        //bpmn:itemDefinition[@id = //bpmn:message[@id=//bpmn:outMessageRef[parent::bpmn:operation[@id=current()/@operationRef]]]/@itemRef]/@structureRef))" diagnostics="id">
                EXT.045|If the operation defines a outMessage, the ItemDefinition of the DataOutput of the ServiceTask
                and the outMessage of the operation must be equal.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.046">
        <iso:rule context="bpmn:sendTask[@messageRef]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:inputSet)&lt;2" diagnostics="id">
                EXT.046|If a SendTask references a message, at most one InputSet must be defined in the ioSpecification.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.047">
        <iso:rule context="bpmn:sendTask[@messageRef]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:dataInput)&lt;2" diagnostics="id">
                EXT.047|If a SendTask references a message, at most one DataInput must be defined in the ioSpecification.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.048">
        <iso:rule context="bpmn:sendTask[bpmn:ioSpecification/bpmn:dataInput]">
            <iso:assert test="bpmn:ioSpecification/bpmn:dataInput/@itemSubjectRef
                    and ((bpmn:ioSpecification/bpmn:dataInput/@itemSubjectRef=//bpmn:message[@id=current()/@messageRef]/@itemRef)
                        or (//bpmn:itemDefinition[@id = current()/bpmn:ioSpecification/bpmn:dataInput/@itemSubjectRef]/@structureRef =
                        //bpmn:itemDefinition[@id = //bpmn:message[@id=current()/@messageRef]/@itemRef]/@structureRef)
                    )
                    " diagnostics="id">
                EXT.048|An Item must be referenced which must be declared in referenced Message definition.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.049">
        <iso:rule context="bpmn:receiveTask[@instantiate='true']">
            <iso:assert test="not(bpmn:incoming)" diagnostics="id">
                EXT.049|A ReceiveTask with attribute instantiate set to true must not have any incoming sequence flow.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.050">
        <iso:rule context="bpmn:receiveTask[@messageRef]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:outputSet)&lt;2" diagnostics="id">
                EXT.050|If a ReceiveTask references a message, at most one OutputSet must be defined in the ioSpecification.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.051">
        <iso:rule context="bpmn:receiveTask[@messageRef]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:dataOutput)&lt;2" diagnostics="id">
                EXT.051|If a ReceiveTask references a message, at most one DataOutput must be defined in the ioSpecification.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.052">
        <iso:rule context="bpmn:receiveTask[bpmn:ioSpecification/bpmn:dataOutput]">
            <iso:assert test="bpmn:ioSpecification/bpmn:dataOutput/@itemSubjectRef
                    and ((bpmn:ioSpecification/bpmn:dataOutput/@itemSubjectRef=//bpmn:message[@id=current()/@messageRef]/@itemRef)
                        or (//bpmn:itemDefinition[@id = current()/bpmn:ioSpecification/bpmn:dataOutput/@itemSubjectRef]/@structureRef =
                        //bpmn:itemDefinition[@id = //bpmn:message[@id=current()/@messageRef]/@itemRef]/@structureRef)
                    )
                    " diagnostics="id">
                EXT.052|An Item must be referenced which must be declared in referenced Message definition.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.053">
        <iso:rule context="bpmn:scriptTask[bpmn:script]">
            <iso:assert test="@scriptFormat" diagnostics="id">
                EXT.053|If a script is present the script type must be defined.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.063">
        <!-- For each InputSet which is a child of a callActivity applies: -->
        <!-- The structureRef of the (indirectly) referenced ItemDefinition must be the same as the structureRef of the
        (indirectly) referenced itemDefinition of the called Process/GlobalTask -->
        <iso:rule context="bpmn:inputSet[ancestor::bpmn:callActivity and node()]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:dataInput[@id=current()/bpmn:dataInputRefs]/@itemSubjectRef]/@structureRef =
        //bpmn:itemDefinition[@id=//bpmn:dataInput[@id=//bpmn:*[@id=//bpmn:callActivity[bpmn:ioSpecification/bpmn:inputSet=current()]
                /@calledElement]/bpmn:ioSpecification/bpmn:inputSet/bpmn:dataInputRefs]/@itemSubjectRef]/@structureRef" diagnostics="id">
                EXT.063|A Call Activity MUST fulfill the data requirements, as well as return the data produced by the
                CallableElement being invoked. This means that the elements contained in the Call Activity's
                InputOutputSpecification MUST exactly match the elements contained in the referenced CallableElement.
                This includes DataInputs, DataOutputs, InputSets, and OutputSets.
            </iso:assert>
        </iso:rule>
        <!-- For each OutputSet which is a child of a callActivity applies: -->
        <!-- The structureRef of the (indirectly) referenced ItemDefinition must be the same as the structureRef of the
        (indirectly) referenced itemDefinition of the called Process/GlobalTask -->
        <iso:rule context="bpmn:outputSet[ancestor::bpmn:callActivity and node()]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:dataOutput[@id=current()/bpmn:dataOutputRefs]/@itemSubjectRef]/@structureRef =
        //bpmn:itemDefinition[@id=//bpmn:dataOutput[@id=//bpmn:*[@id=//bpmn:callActivity[bpmn:ioSpecification/bpmn:outputSet=current()]
                /@calledElement]/bpmn:ioSpecification/bpmn:outputSet/bpmn:dataOutputRefs]/@itemSubjectRef]/@structureRef" diagnostics="id">
                EXT.063|A Call Activity MUST fulfill the data requirements, as well as return the data produced by the
                CallableElement being invoked. This means that the elements contained in the Call Activity's
                InputOutputSpecification MUST exactly match the elements contained in the referenced CallableElement.
                This includes DataInputs, DataOutputs, InputSets, and OutputSets.
            </iso:assert>
        </iso:rule>
        <!-- For each dataInput which is a child of a callActivity applies: -->
        <!-- The structureRef of the (indirectly) referenced ItemDefinition must be the same as the structureRef of the
        (indirectly) referenced itemDefinition of the called Process/GlobalTask -->
        <iso:rule context="bpmn:dataInput[ancestor::bpmn:callActivity]">
            <iso:assert test="//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@structureRef =
        //bpmn:itemDefinition[@id=
                //bpmn:*[@id=//bpmn:callActivity[bpmn:ioSpecification/bpmn:dataInput=current()]/@calledElement]
                    /bpmn:ioSpecification/bpmn:dataInput/@itemSubjectRef]/@structureRef" diagnostics="id">
                EXT.063|A Call Activity MUST fulfill the data requirements, as well as return the data produced by the
                CallableElement being invoked. This means that the elements contained in the Call Activity's
                InputOutputSpecification MUST exactly match the elements contained in the referenced CallableElement.
                This includes DataInputs, DataOutputs, InputSets, and OutputSets.
            </iso:assert>
        </iso:rule>
        <!-- For each dataOutput which is a child of a callActivity applies: -->
        <!-- The structureRef of the (indirectly) referenced ItemDefinition must be the same as the structureRef of the
        (indirectly) referenced itemDefinition of the called Process/GlobalTask -->
        <iso:rule context="bpmn:dataOutput[ancestor::bpmn:callActivity]">
            <iso:assert test="//bpmn:itemDefinition[@id=current()/@itemSubjectRef]/@structureRef =
        //bpmn:itemDefinition[@id=
                //bpmn:*[@id=//bpmn:callActivity[bpmn:ioSpecification/bpmn:dataOutput=current()]/@calledElement]
                    /bpmn:ioSpecification/bpmn:dataOutput/@itemSubjectRef]/@structureRef" diagnostics="id">
                EXT.063|A Call Activity MUST fulfill the data requirements, as well as return the data produced by the
                CallableElement being invoked. This means that the elements contained in the Call Activity's
                InputOutputSpecification MUST exactly match the elements contained in the referenced CallableElement.
                This includes DataInputs, DataOutputs, InputSets, and OutputSets.
            </iso:assert>
        </iso:rule>
        <!-- The number of dataInputs, dataOutputs, InputSets and OutputSets (and there dataInput/dataOutputRefs must be equal) -->
        <iso:rule context="bpmn:callActivity[@calledElement]">
            <iso:assert test="count(bpmn:ioSpecification/bpmn:dataInput)=count(//bpmn:ioSpecification[parent::bpmn:*[@id=current()/@calledElement]]/bpmn:dataInput)
            and count(bpmn:ioSpecification/bpmn:dataOutput)=count(//bpmn:ioSpecification[parent::bpmn:*[@id=current()/@calledElement]]/bpmn:dataOutput)
            and count(bpmn:ioSpecification/bpmn:inputSet)=count(//bpmn:ioSpecification[parent::bpmn:*[@id=current()/@calledElement]]/bpmn:inputSet)
            and count(bpmn:ioSpecification/bpmn:outputSet)=count(//bpmn:ioSpecification[parent::bpmn:*[@id=current()/@calledElement]]/bpmn:outputSet)
            and count(bpmn:ioSpecification/bpmn:inputSet/bpmn:dataInputRefs)=count(//bpmn:ioSpecification[parent::bpmn:*[@id=current()/@calledElement]]/bpmn:inputSet/bpmn:dataInputRefs)
            and count(bpmn:ioSpecification/bpmn:outputSet/bpmn:dataOutputRefs)=count(//bpmn:ioSpecification[parent::bpmn:*[@id=current()/@calledElement]]/bpmn:outputSet/bpmn:dataOutputRefs)" diagnostics="id">
                EXT.063|A Call Activity MUST fulfill the data requirements, as well as return the data produced by the
                CallableElement being invoked. This means that the elements contained in the Call Activity's
                InputOutputSpecification MUST exactly match the elements contained in the referenced CallableElement.
                This includes DataInputs, DataOutputs, InputSets, and OutputSets.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.064">
        <iso:rule context="bpmn:*[bpmn:supportedInterfaceRef]">
            <iso:assert test="count(bpmn:ioBinding)>0" diagnostics="id">
                EXT.064|At least one InputOutputBinding must be defined as the Callable Element is exposed as a Service.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.065">
        <iso:rule context="bpmn:*[bpmn:supportedInterfaceRef and bpmn:ioBinding]">
            <iso:assert test="//bpmn:itemDefinition[@id=current()/bpmn:ioSpecification/bpmn:dataInput[current()/bpmn:ioBinding/@inputDataRef]/@itemSubjectRef]/@structureRef
            = //bpmn:itemDefinition[@id=//bpmn:message[@id=//bpmn:operation[@id=current()/bpmn:ioBinding/@operationRef]/bpmn:inMessageRef]/@itemRef]/@structureRef" diagnostics="id">
                EXT.065|An InputOutputBinding element must correctly bind one Input and one Output of the InputOutputSpecification to an Operation of a Service Interface.
            </iso:assert>
            <iso:assert test="//bpmn:itemDefinition[@id=current()/bpmn:ioSpecification/bpmn:dataOutput[current()/bpmn:ioBinding/@outputDataRef]/@itemSubjectRef]/@structureRef
            = //bpmn:itemDefinition[@id=//bpmn:message[@id=//bpmn:operation[@id=current()/bpmn:ioBinding/@operationRef]/bpmn:outMessageRef]/@itemRef]/@structureRef" diagnostics="id">
                EXT.065|An InputOutputBinding element must correctly bind one Input and one Output of the InputOutputSpecification to an Operation of a Service Interface.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.067">
        <iso:rule context="bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="(bpmn:loopCardinality) or (bpmn:loopDataInputRef)" diagnostics="id">
                EXT.067|If a multiInstance marker is used in an executable process either a loopCardinality or a loopDataInputRef must be present.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.068">
        <iso:rule context="bpmn:multiInstanceLoopCharacteristics[bpmn:loopDataInputRef and not(parent::bpmn:subProcess) and ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="parent::bpmn:*[bpmn:ioSpecification/bpmn:dataInput[@id=current()/bpmn:loopDataInputRef]]" diagnostics="id">
                EXT.068|If a multiInstance task is used in an executable process loopDataInputReference must be
                resolvable to a DataInput defined in the InputOutputSpecification of the Task.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.069">
        <iso:rule context="bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true'] and bpmn:loopDataOutputRef and bpmn:outputDataItem]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:dataOutput[@id=current()/bpmn:loopDataOutputRef]/@itemSubjectRef]/@structureRef =
                    //bpmn:itemDefinition[@id=current()/bpmn:outputDataItem/@itemSubjectRef and not(@isCollection='true')]/@structureRef" diagnostics="id">
                EXT.069|Type of DataOutput must be the scalar of the loopDataOutput type.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.070">
        <iso:rule context="bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true'] and bpmn:loopDataInputRef and bpmn:inputDataItem]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:dataInput[@id=current()/bpmn:loopDataInputRef]/@itemSubjectRef]/@structureRef =
                    //bpmn:itemDefinition[@id=current()/bpmn:inputDataItem/@itemSubjectRef and not(@isCollection='true')]/@structureRef" diagnostics="id">
                EXT.070|Type of DataInput must be the scalar of the loopDataInput type.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.085">
        <iso:rule context="bpmn:inputSet[bpmn:optionalInputRefs]">
            <iso:assert test="bpmn:dataInputRefs[text()=current()/bpmn:optionalInputRefs/text()]" diagnostics="id">
                EXT.085|An optionalInputRef must be listed as dataInputRef.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.086">
        <iso:rule context="bpmn:inputSet[bpmn:whileExecutingInputRefs]">
            <iso:assert test="bpmn:dataInputRefs[text()=current()/bpmn:whileExecutingInputRefs/text()]" diagnostics="id">
                EXT.086|A whileExecutingInputRef must be listed as dataInputRef.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.087">
        <iso:rule context="bpmn:inputSet[bpmn:outputSetRefs]">
            <iso:assert test="parent::bpmn:*/bpmn:outputSet[@id=current()/bpmn:outputSetRefs]/bpmn:inputSetRefs=current()/@id" diagnostics="id">
                EXT.085|If an inputSet references an outputSet using the outputSetRefs element it must be referenced by
                the outputSet using the inputSetRefs element and vice versa.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:outputSet[bpmn:inputSetRefs]">
            <iso:assert test="parent::bpmn:*/bpmn:inputSet[@id=current()/bpmn:inputSetRefs]/bpmn:outputSetRefs=current()/@id" diagnostics="id">
                EXT.085|If an inputSet references an outputSet using the outputSetRefs element it must be referenced by
                the outputSet using the inputSetRefs element and vice versa.
            </iso:assert>
        </iso:rule>
    </iso:pattern>


    <iso:pattern name="EXT.089">
        <iso:rule context="bpmn:outputSet[bpmn:optionalOutputRefs]">
            <iso:assert test="bpmn:dataOutputRefs[text()=current()/bpmn:optionalOutputRefs/text()]" diagnostics="id">
                EXT.089|An optionalOutputRef must be listed as dataOutputRef.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.090">
        <iso:rule context="bpmn:outputSet[bpmn:whileExecutingOutputRefs]">
            <iso:assert test="bpmn:dataOutputRefs[text()=current()/bpmn:whileExecutingOutputRefs/text()]" diagnostics="id">
                EXT.090|A whileExecutingOutputRef must be listed as dataOutputRef.
            </iso:assert>
        </iso:rule>
    </iso:pattern>
    
    <iso:pattern name="EXT.093">
        <iso:rule context="bpmn:intermediateThrowEvent[bpmn:dataInput]">
            <iso:assert test="count(bpmn:dataInput)=count($eventDefinitions)" diagnostics="id">
                EXT.093|If dataInputs are used in an intermediateThrowEvent for each eventDefinition a DataInput must be defined.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:endEvent[bpmn:dataInput]">
            <iso:assert test="count(bpmn:dataInput)=count($eventDefinitions)" diagnostics="id">
                EXT.093|If dataInputs are used in an EndEvent for each eventDefinition a DataInput must be defined.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:startEvent[bpmn:dataOutput]">
            <iso:assert test="count(bpmn:dataOutput)=count($eventDefinitions)" diagnostics="id">
                EXT.093|If dataOutputs are used in a StartEvent for each eventDefinition a DataOutput must be defined.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:intermediateCatchEvent[bpmn:dataOutput]">
            <iso:assert test="count(bpmn:dataOutput)=count($eventDefinitions)" diagnostics="id">
                EXT.093|If dataOutputs are used in an IntermediateCatchEvent for each eventDefinition a DataOutput must be defined.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:boundaryEvent[bpmn:dataOutput]">
            <iso:assert test="count(bpmn:dataOutput)=count($eventDefinitions)" diagnostics="id">
                EXT.093|If dataOutputs are used in a BoundaryEvent for each eventDefinition a DataOutput must be defined.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <let name="precedingEventDefs" value="preceding-sibling::bpmn:messageEventDefinition | preceding-sibling::bpmn:errorEventDefinition | preceding-sibling::bpmn:escalationEventDefinition | preceding-sibling::bpmn:signalEventDefinition" />

    <iso:pattern name="EXT.094">
        <iso:rule context="bpmn:messageEventDefinition[preceding-sibling::bpmn:dataOutput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:message[@id=current()/@messageRef]/@itemRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataOutput[count(preceding-sibling::bpmn:dataOutput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:messageEventDefinition[preceding-sibling::bpmn:dataInput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:message[@id=current()/@messageRef]/@itemRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataInput[count(preceding-sibling::bpmn:dataInput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:errorEventDefinition[preceding-sibling::bpmn:dataOutput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:error[@id=current()/@errorRef]/@structureRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataOutput[count(preceding-sibling::bpmn:dataOutput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:errorEventDefinition[preceding-sibling::bpmn:dataInput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:error[@id=current()/@errorRef]/@structureRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataInput[count(preceding-sibling::bpmn:dataInput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:escalationEventDefinition[preceding-sibling::bpmn:dataOutput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:escalation[@id=current()/@escalationRef]/@structureRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataOutput[count(preceding-sibling::bpmn:dataOutput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:escalationEventDefinition[preceding-sibling::bpmn:dataInput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:escalation[@id=current()/@escalationRef]/@structureRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataInput[count(preceding-sibling::bpmn:dataInput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:signalEventDefinition[preceding-sibling::bpmn:dataOutput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:signal[@id=current()/@signalRef]/@structureRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataOutput[count(preceding-sibling::bpmn:dataOutput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
        <iso:rule context="bpmn:signalEventDefinition[preceding-sibling::bpmn:dataInput]">
            <iso:assert test="//bpmn:itemDefinition[@id=//bpmn:signal[@id=current()/@signalRef]/@structureRef]/@structureRef
= //bpmn:itemDefinition[@id=//bpmn:dataInput[count(preceding-sibling::bpmn:dataInput) = count($precedingEventDefs)]/@itemSubjectRef]/@structureRef " diagnostics="id">
                EXT.094|The itemDefinition for each eventDefinition must be equivalent to the itemDefinition of the corresponding dataInput/dataOutput.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.127">
        <iso:rule context="bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="@messageRef" diagnostics="id">
                EXT.127|A messageRef must be present if the process should be executable.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:pattern name="EXT.128">
        <iso:rule context="bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']]">
            <iso:assert test="bpmn:operationRef" diagnostics="id">
                EXT.128|An operationRef must be present if the process should be executable.
            </iso:assert>
        </iso:rule>
    </iso:pattern>

    <iso:diagnostics>
        <iso:diagnostic id="id"><value-of select="current()/@id" /></iso:diagnostic>
        <iso:diagnostic id="sourceRef"><value-of select="current()/@sourceRef" /></iso:diagnostic>
        <iso:diagnostic id="targetRef"><value-of select="current()/@targetRef" /></iso:diagnostic>
    </iso:diagnostics>    
</iso:schema> 
