package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, List<String>> {

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        Set<String> vistos = new HashSet<>();
        for (String elemento : value) {
            if (elemento == null) {
                continue;
            }
            String normalizado = elemento.trim().toUpperCase();
            if (!vistos.add(normalizado)) {
                return false;
            }
        }
        return true;
    }
}
