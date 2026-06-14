package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FechasPeliculaValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FechasPeliculaValidas {

    String message() default "La fecha de salida debe ser posterior a la fecha de estreno";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
