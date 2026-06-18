package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPagoHelper;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidMetodoPago;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidMetodoPagoValidatorImpl implements ConstraintValidator<ValidMetodoPago, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return MetodoPagoHelper.esValido(value);
    }
}
