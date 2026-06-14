package GESTOR_CINEMAR_CENTER.DEV.validation.interfaces;

import GESTOR_CINEMAR_CENTER.DEV.validation.impl.RangoFuncionesValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RangoFuncionesValidatorImpl.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RangoFuncionesValido {

    String message() default "El rango de fechas u horarios de las funciones es inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
