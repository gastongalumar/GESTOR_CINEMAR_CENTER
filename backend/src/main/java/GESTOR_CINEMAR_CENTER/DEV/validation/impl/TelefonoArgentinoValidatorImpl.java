package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

public final class TelefonoArgentinoValidatorImpl {

    private TelefonoArgentinoValidatorImpl() {
    }

    public static boolean esValido(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return false;
        }

        String normalizado = telefono.replaceAll("\\D", "");

        if (!normalizado.startsWith("549") || normalizado.length() != 13) {
            return false;
        }

        String numeroNacional = normalizado.substring(3);

        if (numeroNacional.matches("^(.)\\1+$")) {
            return false;
        }

        return !numeroNacional.equals("1234567890") && !numeroNacional.equals("9876543210");
    }

    public static String normalizar(String telefono) {
        return telefono.replaceAll("\\D", "");
    }
}
