package GESTOR_CINEMAR_CENTER.DEV.enums;

/**
 * Valores permitidos para customizacion.efectoFestivo.
 * Se persisten como String para coincidir 1:1 con el JSON del front.
 */
public enum EfectoFestivo {
    NINGUNO("ninguno"),
    NAVIDAD("navidad"),
    HALLOWEEN("halloween"),
    ANO_NUEVO("ano-nuevo");

    public static final String REGEX = "ninguno|navidad|halloween|ano-nuevo";
    public static final String DEFAULT = "ninguno";

    private final String valor;

    EfectoFestivo(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static boolean esValido(String valor) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        for (EfectoFestivo efecto : values()) {
            if (efecto.valor.equals(valor)) {
                return true;
            }
        }
        return false;
    }

    public static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return DEFAULT;
        }
        return esValido(valor.trim()) ? valor.trim() : DEFAULT;
    }
}
