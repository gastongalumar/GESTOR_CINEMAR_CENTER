package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidEmailFormato;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEmailFormatoValidatorImpl implements ConstraintValidator<ValidEmailFormato, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return EmailFormatoValidatorImpl.esValido(value);
    }
}
