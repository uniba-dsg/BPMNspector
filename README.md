# What is it? || Description

This tool validates BPMN models against the offically provided XSDs of the OMG and 
a subset of the descriptive extended constraints from [Geiger (2013)](http://www.uni-bamberg.de/fileadmin/uni/fakultaeten/wiai_lehrstuehle/praktische_informatik/Dateien/Publikationen/techrep-bpmn-serialization-constraints.pdf).

Additionally, the tool checks the schema validity of all in a BPMN file imported WSDL and XML files.
Namely, the following constraints of Geiger identified by their ids can be checked through this tool:

1, 2, 6-9, 21-23, 25, 26, 28, 31, 36, 56, 76, 79, 84, 88, 95-109, 135, 146, 150, 151, 152 

# Requirements and Installation

First, at least Java 7 is required to use the program.

Next, `JAVA_HOME` must be set, included into your `PATH` and point to your JAVA installation for the console usage.

Lastly, this tool supports Gradle usage.
  
# Validation

You can use the validator in two ways, either stand-alone or as API.

## Via Console

The stand-alone validator must be used via console. It prints debug loggings
if `--debug` is added to the JAR invoking. Additionally, the validator needs a number of files, which
should be validated. Their paths have to be added as parameters to the JAR call and
must be separated by a whitespace, so that the validation works correctly. The results
of every ﬁle validation is saved as `validation_result_name_of_the_validated_file.xml` in the
folder of the validated ﬁle.

## Via JAR

Alternatively, you can include the validator into your project and use it as API. The JAR can be easily created by `gradle build` or `gradle jar` and is then located in `\build\libs`. Next, you have
to get a reference to the validator instance through `BpmnValidatorFactory.getValidatorInstance()`
and then you can call on this object the methods shown below:

	/**
 	* Interface for the implementation of the validator. Allows the usage of the
 	* validator in other projects. The loglevel is set default to info. If you need
 	* another log level, change the log level before the validation process.
 	* 
 	* @author Philipp Neugebauer
 	* @version 1.0
 	* 
 	*/
	public interface BpmnValidator {

		/**
	 	* returns the set loglevel of all loggers
	 	* 
	 	* @return the set log level {@link ch.qos.logback.classic.Level}
	 	*/
		Level getLogLevel();

		/**
	 	* Sets the loglevel of all loggers of the bpmn validator to the given level
	 	* 
	 	* @param logLevel
	 	*            possible levels: {@link ch.qos.logback.classic.Level}
	 	*/
		void setLogLevel(Level logLevel);

		/**
	 	* checks the given xmlFile for bpmn constraint violations
	 	* 
	 	* @param xmlFile
	 	* @return ValidationResult including all checked files and found violations
	 	* @throws BpmnValidationException
	 	*             if something fails during validation process
	 	*/
		ValidationResult validate(File xmlFile) throws BpmnValidationException;

		/**
	 	* checks the given xmlFiles for bpmn constraint violations
	 	* 
	 	* @param xmlFiles
	 	* @return List<ValidationResult> including all checked files and found
	 	*         violations for each file
	 	* @throws BpmnValidationException
	 	*             if something fails during validation process
	 	*/
		List<ValidationResult> validateFiles(List<File> xmlFiles)
			throws BpmnValidationException;

	}

Therefore, code including this tool will look like the following:

	BpmnValidator v = BpmnValidatorFactory.getValidatorInstance();
	v.setLogLevel(Level.INFO);
	try {
		v.validate(new File("FilePath"));
		//or
		v.validateFiles(new ArrayList<File>());
	} catch (BpmnValidationException e) {
		// handle
	}

# Documentation

The documentation of the tool can be easily created by executing `gradle javadoc` and is then located in `\build\docs\javadoc`.

# Structure of Software/Repository

The repository is structured in the following way:

	|- gradle: contains the gradle wapper
	|- lib: all libs required for the tool which aren't uploaded to maven central
	|- src
	|-- main
	|--- java: contains all java classes
	|---- de.uniba.dsg.ppn.ba: contains the main class for API usage
	|----- api: contains the factory class
	|----- preprocessing: contains the classes performing the preprocessing step
	|----- validation: contains the classes performing the validation steps
	|--- resources: contains all schema validation files
	|-- test
	|--- java: contains all test classes named by their test case
	|--- resources: contains all test files sorted in folders by their test case

# Licensing

# Authors

# Developing Helpers

There are different helper classes to debug and simplify developing:

`PrintHelper.java`: prints the results in a nice way

`TestMain.java`: allows the simplified testing and presentation of validation results

```
import java.io.File;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.PrintHelper;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class TestMain {

	public static void main(String[] args) throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "002" + File.separator + 
			"success_import.bpmn");
		SchematronBPMNValidator validator = new SchematronBPMNValidator();
		ValidationResult result = validator.validate(f);
		System.out.println("Is File " + f.getName() + " valid? "+ result.isValid());

		if (!result.isValid()) {
			PrintHelper.printViolations(result.getViolations());
		}

	}
}
```


# Contribute

