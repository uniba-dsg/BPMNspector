# BPMNspector <img align="right" src="src/main/resources/logo-h100.png" height="100" width="217"/>

**Statical analysis for BPMN 2.0 process models**

## What is it? || Description

Creating valid, standard compliant BPMN 2.0 process models is not trivial. Even when state-of-art modeling tools are used, model often violate some of the constraints stated in the standard document.

BPMNspector checks single files - or complete directories - of BPMN files and reports violations of BPMN 2.0 constraints.

**BPMNspector currently supports:**
- **Schema validation:** Ensures correctness regarding the official OMG XSD files
- **Reference Checking:** Ensures that all used references exist and that only valid types are used
- **Check of Advanced Constraints:** 40 (of 52) constraints defined for the "descriptive conformance sub class" are checked
- **Support of imports:** (resolvable) imports of processes, WSDL and XSD files are also checked

This software is licensed under the LGPL Version 3 Open Source License.

For further information visit http://www.uni-bamberg.de/pi/bpmn-constraints.

## What do I need? || Requirements 
As BPMNspector uses gradlew only a Java 8 installation is needed - download  and configuration of needed libraries is performed on the fly.

 - JDK 1.8.0 (or higher)
    - JAVA_HOME should point to the jdk directory
    - PATH should include JAVA_HOME/bin

  
## How do I use BPMNspector? || Usage

To use BPMNspector simply run the start script:

```
$ BPMNspector fileToValidate.bpmn
```

Available options are listed by calling:
```
$ BPMNspector -h
```

or here:

```
usage: BPMNspector <file or directory> [-c <[opt1[,opt2]...>] [-d] [-h]
Options:
 -c,--checks <[opt1[,opt2]...>   defines which checks should be performed.
                                 Allowed values:
                                 EXT - checks conformance to EXT rules
                                 ALL - performs all checks (default)
                                 REF - checks the correctness of
                                 references
                                 XSD - performs an XML schema validation
 -d,--debug                      run BPMNspector in debug mode
 -h,--help                       prints this usage information

Examples:
                BPMNspector myfile.bpmn
                BPMNspector c:\absolute\path\to\folder -c REF -d
```

## How can I use BPMNspector as a developer? || Development

Run...
```
$ gradlew idea
# or
$ gradlew eclipse
```
... to create project files for your favorite IDE.

### Structure of Software/Repository

The repository is structured in the following way:

	|- gradle: contains the gradle wapper
	|- lib: all libs required for the tool which aren't uploaded to maven central
	|- src
	|-- main
	|--- java: contains all java classes
    |---- api: contains the API files needed for integration in other tools
	|---- de.uniba.dsg.bpmnspector: implementation of BPMNspector
	|--- resources: contains all needed resources such as schema validation files
	|-- test
	|--- java: contains all test classes named by their test case
	|--- resources: contains all test files sorted in folders by their test case

### Technical Documentation / Javadoc
Run...
```
$ gradlew javadoc
```
... to generate the Javadoc documentation.

## Licensing
LGPL Version 3: http://www.gnu.org/licenses/lgpl-3.0.html

## Authors
[Matthias Geiger](http://www.uni-bamberg.de/en/pi/team/geiger-matthias/), [Philipp Neugebauer](https://github.com/philippneugebauer) and [Andreas Vorndran](https://github.com/andy-x)

BPMNspector is partly based on the practical part of two Bachelor theses:
- Andreas has developed the reference checking mechanism ```de.uniba.dsg.bpmnspector.refcheck``` - this part has already been published [here](https://github.com/uniba-dsg/BPMN-Reference-Validator)
- Philipp created the Schematron validation part of BPMNspector ```de.uniba.dsg.bpmnspector.schematron```

## Found a bug?
Report your issue here at GitHub!

## Contribute?
Just Fork and send a Pull request.
