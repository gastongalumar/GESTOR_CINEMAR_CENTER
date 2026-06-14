package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CapacidadSalaValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CapacidadSalaValida {

    String message() default "La capacidad máxima de la sala es de 320 asientos (filas x columnas)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
