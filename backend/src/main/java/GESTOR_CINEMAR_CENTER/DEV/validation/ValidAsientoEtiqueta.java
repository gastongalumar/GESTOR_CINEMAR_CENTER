package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidAsientoEtiquetaValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAsientoEtiqueta {

    String message() default "Formato de asiento inválido. Ejemplo: A1, B12";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
