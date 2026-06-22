package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import java.util.regex.Pattern;

public final class NombrePersonaValidatorImpl {

    private static final Pattern NOMBRE_PERSONA = Pattern.compile(
            "^(?=.*\\p{L})[\\p{L}]+([\\s.'-][\\p{L}]+)*$",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    private NombrePersonaValidatorImpl() {
    }

    public static boolean esValido(String valor) {
        if (valor == null) {
            return false;
        }

        String normalizado = valor.trim();
        if (normalizado.isEmpty()) {
            return false;
        }

        return NOMBRE_PERSONA.matcher(normalizado).matches();
    }
}
