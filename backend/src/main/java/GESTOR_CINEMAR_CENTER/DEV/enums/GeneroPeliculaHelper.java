package GESTOR_CINEMAR_CENTER.DEV.enums;

public class GeneroPeliculaHelper {

    private GeneroPeliculaHelper() {
    }

    public static boolean esValido(String valor) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        try {
            fromString(valor);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static GeneroPelicula fromString(String valor) {
        String normalizado = valor.trim()
                .toUpperCase()
                .replace(' ', '_')
                .replace('-', '_');
        return GeneroPelicula.valueOf(normalizado);
    }
}
