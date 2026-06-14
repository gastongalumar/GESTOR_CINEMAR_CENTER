package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.enums.GeneroPeliculaHelper;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidGeneroPelicula;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidGeneroPeliculaValidatorImpl implements ConstraintValidator<ValidGeneroPelicula, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return GeneroPeliculaHelper.esValido(value);
    }
}
