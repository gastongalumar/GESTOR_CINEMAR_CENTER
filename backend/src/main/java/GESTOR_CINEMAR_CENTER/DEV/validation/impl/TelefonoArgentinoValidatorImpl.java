package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

public final class TelefonoArgentinoValidatorImpl {

    private TelefonoArgentinoValidatorImpl() {
    }

    /**
     * Acepta teléfono nacional de 10 dígitos (código de área sin 0 + número sin 15).
     * Ejemplo UI: área 0223 + número 5678912 → 2235678912.
     * También acepta 549 + 10 dígitos por compatibilidad.
     */
    public static boolean esValido(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return false;
        }

        String numeroNacional = aNumeroNacional(telefono);
        if (numeroNacional == null || numeroNacional.length() != 10) {
            return false;
        }

        if (numeroNacional.matches("^(.)\\1+$")) {
            return false;
        }

        return !numeroNacional.equals("1234567890") && !numeroNacional.equals("9876543210");
    }

    public static String normalizar(String telefono) {
        String numeroNacional = aNumeroNacional(telefono);
        return numeroNacional != null ? numeroNacional : telefono.replaceAll("\\D", "");
    }

    private static String aNumeroNacional(String telefono) {
        String n = telefono.replaceAll("\\D", "");

        if (n.startsWith("549") && n.length() == 13) {
            return n.substring(3);
        }
        // Si viene con 0 de área (02235678912) → sacar el 0
        if (n.startsWith("0") && n.length() == 11) {
            return n.substring(1);
        }
        if (n.length() == 10) {
            return n;
        }
        return null;
    }
}
