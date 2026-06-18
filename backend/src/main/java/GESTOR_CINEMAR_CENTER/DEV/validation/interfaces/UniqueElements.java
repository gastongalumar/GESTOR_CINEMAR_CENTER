package GESTOR_CINEMAR_CENTER.DEV.validation.interfaces;

import GESTOR_CINEMAR_CENTER.DEV.validation.impl.UniqueElementsValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UniqueElementsValidatorImpl.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueElements {

    String message() default "No se permiten elementos duplicados";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
