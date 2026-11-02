package GESTOR_CINEMAR_CENTER.DEV.security.oauth2;

import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import GESTOR_CINEMAR_CENTER.DEV.repository.UsuarioRepository;
import GESTOR_CINEMAR_CENTER.DEV.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Emite el JWT propio de la aplicación después de un login exitoso con Google
 * y redirige al frontend con el token en el query string.
 *
 * <p>El token se genera con el {@link JwtUtil} existente, usando el email y el
 * rol ({@code tipo}) del usuario de la base local. Es exactamente el mismo
 * token que emite el login tradicional, por lo que el interceptor y los guards
 * del frontend siguen funcionando sin cambios.</p>
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final String redirectUri;
    private final String failureUri;

    public OAuth2AuthenticationSuccessHandler(
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil,
            @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}") String redirectUri,
            @Value("${app.oauth2.failure-uri:http://localhost:4200/login?error=google}") String failureUri) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.redirectUri = redirectUri;
        this.failureUri = failureUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
            throw new ServletException("El principal de Google no es un OAuth2User.");
        }

        String email = normalizarEmail(oAuth2User.getAttribute("email"));
        Usuario usuario = email == null
                ? null
                : usuarioRepository.findByEmailIgnoreCase(email).orElse(null);

        if (usuario == null) {
            log.warn("Login con Google sin usuario local para el email provisto.");
            response.sendRedirect(failureUri);
            return;
        }

        String tipo = usuario.getTipo() != null
                ? usuario.getTipo().name()
                : (usuario instanceof Administrador ? "ADMINISTRADOR" : "CLIENTE");

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                tipo,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getFotoPerfilUrl()
        );

        if (log.isDebugEnabled()) {
            String prefijo = token.substring(0, Math.min(12, token.length()));
            log.debug("JWT emitido para {} (rol {}): {}...", email, tipo, prefijo);
        }

        String separador = redirectUri.contains("?") ? "&" : "?";
        String destino = redirectUri + separador + "token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        response.sendRedirect(destino);
    }

    private String normalizarEmail(Object value) {
        if (value instanceof String email && !email.isBlank()) {
            return email.trim().toLowerCase(Locale.ROOT);
        }
        return null;
    }
}
