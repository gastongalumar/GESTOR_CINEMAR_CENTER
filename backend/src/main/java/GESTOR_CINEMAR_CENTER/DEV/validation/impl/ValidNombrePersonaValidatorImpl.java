package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidNombrePersona;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNombrePersonaValidatorImpl implements ConstraintValidator<ValidNombrePersona, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return NombrePersonaValidatorImpl.esValido(value);
    }
}
