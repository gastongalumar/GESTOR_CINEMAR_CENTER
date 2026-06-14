package GESTOR_CINEMAR_CENTER.DEV.validation.interfaces;

import GESTOR_CINEMAR_CENTER.DEV.validation.impl.ValidMetodoPagoValidatorImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidMetodoPagoValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMetodoPago {

    String message() default "Método de pago no permitido. Valores válidos: EFECTIVO, TARJETA, MERCADO_PAGO, TRANSFERENCIA";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
