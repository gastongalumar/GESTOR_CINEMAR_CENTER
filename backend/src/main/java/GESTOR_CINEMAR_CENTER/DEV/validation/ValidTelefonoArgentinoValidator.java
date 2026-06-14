package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTelefonoArgentinoValidator implements ConstraintValidator<ValidTelefonoArgentino, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return TelefonoArgentinoValidator.esValido(value);
    }
}
