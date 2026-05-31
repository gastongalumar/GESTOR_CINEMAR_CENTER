package DEV.enums;

public enum MetodoPago {
    EFECTIVO,
    TARJETA,
    MERCADO_PAGO,
    TRANSFERENCIA;

    public static boolean esValido(String valor) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        try {
            valueOf(valor.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static MetodoPago fromString(String valor) {
        return valueOf(valor.trim().toUpperCase());
    }
}
