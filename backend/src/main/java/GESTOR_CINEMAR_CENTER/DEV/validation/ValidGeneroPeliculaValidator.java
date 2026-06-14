package GESTOR_CINEMAR_CENTER.DEV.validation;

import GESTOR_CINEMAR_CENTER.DEV.enums.GeneroPeliculaHelper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidGeneroPeliculaValidator implements ConstraintValidator<ValidGeneroPelicula, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return GeneroPeliculaHelper.esValido(value);
    }
}
