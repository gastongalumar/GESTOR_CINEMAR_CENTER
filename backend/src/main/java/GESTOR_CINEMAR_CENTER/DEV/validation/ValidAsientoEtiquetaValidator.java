package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidAsientoEtiquetaValidator implements ConstraintValidator<ValidAsientoEtiqueta, String> {

    private static final String ASIENTO_PATTERN = "^[A-T][1-9][0-9]?$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.trim().toUpperCase().matches(ASIENTO_PATTERN);
    }
}
