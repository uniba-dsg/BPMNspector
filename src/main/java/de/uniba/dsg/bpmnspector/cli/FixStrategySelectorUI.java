package de.uniba.dsg.bpmnspector.cli;

import api.Violation;
import de.uniba.dsg.bpmnspector.autofix.FixingStrategy;
import de.uniba.dsg.bpmnspector.autofix.ViolationFixer;

import java.util.List;
import java.util.Scanner;

public class FixStrategySelectorUI {

    private static final int IGNORE_OPTION = 0;

    public FixingStrategy determineStragegyForViolation(Violation violation, List<ViolationFixer> availableFixers) {

        displayViolationInformation(violation);
        int result = askForStrategy(availableFixers);
        if (result == IGNORE_OPTION) {
            return FixingStrategy.IGNORE;
        } else {
            return availableFixers.get(result-1).getSupportedStrategy();
        }
    }

    private int askForStrategy(List<ViolationFixer> availableFixers) {
        int optionCounter = 1;

        System.out.println("Choose fix option:");
        System.out.println("[" + IGNORE_OPTION + "] IGNORE violation: do not fix the violation");

        for (ViolationFixer fixer : availableFixers) {
            System.out.println("[" + optionCounter + "] " + fixer.getSupportedStrategy()+": "+fixer.getDescription());
            optionCounter++;
        }

        return readSingleIntFromSystemIn(0, availableFixers.size());
    }

    private void displayViolationInformation(Violation violation) {
        System.out.println("Violation to fix: "+violation.toString());
    }

    private int readSingleIntFromSystemIn(int start, int end) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int readInt = scanner.nextInt();
                if (readInt < start || readInt > end) {
                    System.out.println("Invalid value - must be in range of " + start + " to " + end);
                } else {
                    return readInt;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Not a valid integer value!");
            }
        }
    }
}
