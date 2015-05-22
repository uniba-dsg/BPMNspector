# BPMNspector <img align="right" src="src/main/resources/reporting/res/logo-h100.png" height="100" width="217"/>

**Static analysis for BPMN 2.0 process models**

## What is it? || Description

Creating valid, standard compliant BPMN 2.0 process models is not trivial. Even when state-of-art modeling tools are used, model often violate some of the constraints stated in the standard document.

BPMNspector checks single files - or complete directories - of BPMN files and reports violations of BPMN 2.0 constraints.

**BPMNspector currently supports:**
- **Schema validation:** Ensures correctness regarding the official OMG XSD files
- **Reference Checking:** Ensures that all used references exist and that only valid types are used
- **Check of Advanced Constraints:**
    - 40 (of 52) constraints defined for the "descriptive conformance sub class" are checked
    - all additional "analytic conformance sub class" constraints are checked
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

After completion a HTML report will be opened automatically - all reports are stored in ```{BPMNspector.home}/reports```.

Available options are listed by calling:
```
$ BPMNspector -h
```

or here:

```
usage: BPMNspector <file or directory> [-c <[opt1[,opt2]...>] [-d] [-h]
       [-o] [-r <NONE | XML | HTML | BOTH>]
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
 -o,--open                       open the report file upon completion
 -r <NONE | XML | HTML | BOTH>   defines which report type should be
                                 generated.
                                 Allowed values:
                                 ALL - create both report types
                                 XML - create XML reports
                                 HTML - create HTML reports (default)
                                 NONE - No report file should be created

Examples:
		BPMNspector myfile.bpmn
		BPMNspector c:\absolute\path\to\folder -c REF -d
		BPMNspector c:\absolute\path\to\file.bpmn -o -r HTML
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
- Philipp created the Schematron validation part of BPMNspector ```de.uniba.dsg.bpmnspector.schematron``` - the standalone version is [SchematronBPMNValidator](https://github.com/philippneugebauer/SchematronBPMNValidator)

## Found a bug?
Report your issue here at GitHub!

## Contribute?
Just Fork and send a Pull request.

## Related Work
* [BPMNspector-fixSeqFlow](https://github.com/matthiasgeiger/BPMNspector-fixSeqFlow): Tool to fix the most common mistake in BPMN processes: EXT.023 violations

----
[![Build Status](https://travis-ci.org/uniba-dsg/BPMNspector.svg?branch=master)](https://travis-ci.org/uniba-dsg/BPMNspector) [![Dependency Status](https://www.versioneye.com/user/projects/54d48c473ca0840b190002f7/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54d48c473ca0840b190002f7)
