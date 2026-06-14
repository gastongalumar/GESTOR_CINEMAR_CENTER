package GESTOR_CINEMAR_CENTER.DEV.validation;

import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPagoHelper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidMetodoPagoValidator implements ConstraintValidator<ValidMetodoPago, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return MetodoPagoHelper.esValido(value);
    }
}
