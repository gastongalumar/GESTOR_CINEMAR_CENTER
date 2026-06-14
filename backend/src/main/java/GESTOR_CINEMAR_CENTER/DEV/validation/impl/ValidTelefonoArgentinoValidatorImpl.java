package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidTelefonoArgentino;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTelefonoArgentinoValidatorImpl implements ConstraintValidator<ValidTelefonoArgentino, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return TelefonoArgentinoValidatorImpl.esValido(value);
    }
}
