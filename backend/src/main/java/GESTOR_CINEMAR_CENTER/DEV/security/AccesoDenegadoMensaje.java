package GESTOR_CINEMAR_CENTER.DEV.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AccesoDenegadoMensaje {

    private static final String MSG_DEFAULT = "No tiene permisos para realizar esta operación.";

    public String resolver(HttpServletRequest request, Authentication authentication) {
        if (authentication == null) {
            return MSG_DEFAULT;
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String method = request.getMethod();
        String path = request.getRequestURI();

        if (roles.contains("ADMINISTRADOR")) {
            return resolverParaAdministrador(method, path);
        }
        if (roles.contains("CLIENTE")) {
            return resolverParaCliente(method, path);
        }

        return MSG_DEFAULT;
    }

    private String resolverParaAdministrador(String method, String path) {
        if ("POST".equals(method) && path.equals("/api/reservas")) {
            return "Solo los clientes pueden crear reservas.";
        }
        if ("POST".equals(method) && path.startsWith("/api/pagos/")) {
            return "Solo los clientes pueden registrar pagos.";
        }
        return MSG_DEFAULT;
    }

    private String resolverParaCliente(String method, String path) {
        if ("POST".equals(method) && path.equals("/api/auth/registro-admin")) {
            return "Solo los administradores pueden registrar nuevos administradores.";
        }
        if (coincideCrudAdministrador(method, path, "/api/peliculas")) {
            return "Solo los administradores pueden gestionar películas.";
        }
        if (coincideCrudAdministrador(method, path, "/api/funciones")) {
            return "Solo los administradores pueden gestionar funciones.";
        }
        if (coincideCrudAdministrador(method, path, "/api/salas")) {
            return "Solo los administradores pueden gestionar salas.";
        }
        if (coincideCrudAdministrador(method, path, "/api/customizacion")) {
            return "Solo los administradores pueden modificar la personalización del sitio.";
        }
        if ("GET".equals(method) && path.startsWith("/api/estadisticas")) {
            return "Solo los administradores pueden consultar estadísticas.";
        }
        if ("GET".equals(method) && (path.equals("/api/pagos") || path.startsWith("/api/pagos/"))
                && !path.equals("/api/pagos/mis")
                && !path.matches("/api/pagos/ticket/[^/]+/pago")) {
            return "Solo los administradores pueden consultar pagos.";
        }
        if (path.equals("/api/usuarios") || path.startsWith("/api/usuarios/")) {
            return "Solo los administradores pueden gestionar usuarios.";
        }
        if (path.startsWith("/api/reservas/admin/")
                || path.startsWith("/api/reservas/cliente/")
                || ("POST".equals(method) && path.startsWith("/api/reservas/validar/"))) {
            return "Solo los administradores pueden acceder a la gestión de reservas.";
        }
        return MSG_DEFAULT;
    }

    private boolean coincideCrudAdministrador(String method, String path, String basePath) {
        if (!path.startsWith(basePath)) {
            return false;
        }
        return switch (method) {
            case "POST", "PUT", "DELETE" -> true;
            default -> false;
        };
    }
}
