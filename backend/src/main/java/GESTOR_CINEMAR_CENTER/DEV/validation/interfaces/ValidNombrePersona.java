package GESTOR_CINEMAR_CENTER.DEV.validation.interfaces;

import GESTOR_CINEMAR_CENTER.DEV.validation.impl.ValidNombrePersonaValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidNombrePersonaValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNombrePersona {

    String message() default "Debe contener al menos una letra y solo puede incluir letras, espacios, puntos, apóstrofes o guiones entre palabras";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
