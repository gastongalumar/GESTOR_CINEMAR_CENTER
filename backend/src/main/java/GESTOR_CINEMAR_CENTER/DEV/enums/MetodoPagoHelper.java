package GESTOR_CINEMAR_CENTER.DEV.enums;

public class MetodoPagoHelper {

    public static boolean esValido(String valor) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        try {
            MetodoPago.valueOf(valor.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static MetodoPago fromString(String valor) {
        return MetodoPago.valueOf(valor.trim().toUpperCase());
    }
}
