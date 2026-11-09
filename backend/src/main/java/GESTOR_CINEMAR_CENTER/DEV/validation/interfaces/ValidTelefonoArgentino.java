package GESTOR_CINEMAR_CENTER.DEV.validation.interfaces;

import GESTOR_CINEMAR_CENTER.DEV.validation.impl.ValidTelefonoArgentinoValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidTelefonoArgentinoValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefonoArgentino {

    String message() default "Teléfono inválido. Usá código de área + número sin 15. Ejemplo: 0223 / 5678912";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
