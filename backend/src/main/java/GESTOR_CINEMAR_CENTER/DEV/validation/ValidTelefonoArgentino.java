package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidTelefonoArgentinoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefonoArgentino {

    String message() default "El teléfono debe estar en formato internacional argentino. Ejemplo: +54 9 351 1234567";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
