package GESTOR_CINEMAR_CENTER.DEV.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidGeneroPeliculaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGeneroPelicula {

    String message() default "Género no permitido. Valores válidos: ACCION, AVENTURA, ANIMACION, COMEDIA, DRAMA, DOCUMENTAL, FANTASIA, MUSICAL, ROMANCE, CIENCIA_FICCION, TERROR, THRILLER, WESTERN, BIOGRAFIA, FAMILIAR";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
