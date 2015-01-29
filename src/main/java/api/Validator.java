package api;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Validator {

    ValidationResult validate(Path file) throws ValidationException;

    default ValidationResult validate(String file) throws ValidationException {
        return validate(Paths.get(file));
    }

}
