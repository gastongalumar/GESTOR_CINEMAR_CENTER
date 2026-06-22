package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

public final class EmailFormatoValidatorImpl {

    private EmailFormatoValidatorImpl() {
    }

    public static boolean esValido(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String normalizado = email.trim();
        int arroba = normalizado.lastIndexOf('@');
        if (arroba <= 0 || arroba == normalizado.length() - 1) {
            return false;
        }

        String local = normalizado.substring(0, arroba);
        String dominio = normalizado.substring(arroba + 1);

        return esParteLocalValida(local) && esDominioValido(dominio);
    }

    private static boolean esParteLocalValida(String local) {
        if (local.length() > 64 || local.contains("..")) {
            return false;
        }

        return local.matches("^[a-zA-Z0-9]([a-zA-Z0-9._+-]*[a-zA-Z0-9])?$");
    }

    private static boolean esDominioValido(String dominio) {
        if (dominio.length() > 255 || dominio.contains("..")) {
            return false;
        }

        String[] etiquetas = dominio.split("\\.");
        if (etiquetas.length < 2) {
            return false;
        }

        String tld = etiquetas[etiquetas.length - 1];
        if (!tld.matches("^[a-zA-Z]{2,}$")) {
            return false;
        }

        for (String etiqueta : etiquetas) {
            if (etiqueta.isEmpty() || etiqueta.length() > 63) {
                return false;
            }
            if (!etiqueta.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?$")) {
                return false;
            }
        }

        return true;
    }
}
