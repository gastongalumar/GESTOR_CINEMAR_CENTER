package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidTelefonoArgentino;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTelefonoArgentinoValidatorImpl implements ConstraintValidator<ValidTelefonoArgentino, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Vacío = válido a nivel de anotación; @NotBlank decide si es obligatorio.
        if (value == null || value.isBlank()) {
            return true;
        }
        return TelefonoArgentinoValidatorImpl.esValido(value);
    }
}
