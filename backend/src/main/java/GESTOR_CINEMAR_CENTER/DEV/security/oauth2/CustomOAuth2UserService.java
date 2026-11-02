package GESTOR_CINEMAR_CENTER.DEV.security.oauth2;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import GESTOR_CINEMAR_CENTER.DEV.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de usuarios para el login con Google.
 *
 * <p>Google sólo confirma la identidad. La cuenta y el rol se resuelven
 * siempre contra la base de datos local:</p>
 *
 * <ul>
 *   <li>Si el email ya existe, se usa el usuario tal cual (conserva su rol,
 *       incluso {@code ADMINISTRADOR}).</li>
 *   <li>Si no existe, se crea un {@link Cliente} con rol {@code CLIENTE}.
 *       La columna {@code password} es {@code NOT NULL}, por lo que se guarda
 *       una contraseña aleatoria con BCrypt que nadie conoce (el usuario
 *       ingresa por Google, no por contraseña).</li>
 * </ul>
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = normalizarEmail(attributes.get("email"));
        if (email == null) {
            throw error("email_no_disponible", "Google no devolvió un email para la cuenta.");
        }

        if (!emailVerificado(attributes.get("email_verified"))) {
            throw error("email_no_verificado", "El email de la cuenta de Google no está verificado.");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);

        if (usuario == null) {
            usuario = crearCliente(email, attributes);
            log.info("Cuenta creada desde Google: {} (rol {})", email, usuario.getTipo());
        } else {
            if (usuario.getEstado() != null && usuario.getEstado() != EstadoUsuario.ACTIVO) {
                throw error("cuenta_inactiva", "La cuenta de usuario no está activa.");
            }
            actualizarNombreDesdeGoogle(usuario, attributes);
            usuario.setFechaUltimoAcceso(LocalDateTime.now());
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
            log.info("Login con Google: {} (rol {})", email, usuario.getTipo());
        }

        // El success handler vuelve a buscar el usuario y genera el JWT local.
        return oAuth2User;
    }

    private Cliente crearCliente(String email, Map<String, Object> attributes) {
        String[] nombreApellido = resolverNombreApellido(email, attributes);

        Cliente cliente = new Cliente();
        cliente.setNombre(nombreApellido[0]);
        cliente.setApellido(nombreApellido[1]);
        cliente.setEmail(email);
        // Password aleatoria: el usuario sólo puede ingresar por Google.
        cliente.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        cliente.setTipo(TipoUsuario.CLIENTE);
        cliente.setEstado(EstadoUsuario.ACTIVO);

        return usuarioRepository.save(cliente);
    }

    /** Actualiza nombre/apellido con los datos de Google cuando vienen informados. */
    private void actualizarNombreDesdeGoogle(Usuario usuario, Map<String, Object> attributes) {
        String[] nombreApellido = resolverNombreApellido(usuario.getEmail(), attributes);
        String nombre = nombreApellido[0];
        String apellido = nombreApellido[1];

        boolean cambio = false;
        if (nombre != null && !nombre.isBlank() && !nombre.equals(usuario.getNombre())) {
            usuario.setNombre(nombre);
            cambio = true;
        }
        if (apellido != null && !apellido.isBlank() && !apellido.equals(usuario.getApellido())) {
            // Evita pisar un apellido real con el fallback "Google" si Google no lo mandó.
            boolean esFallbackGoogle = "Google".equalsIgnoreCase(apellido);
            boolean apellidoActualEsFallback =
                    usuario.getApellido() == null
                            || usuario.getApellido().isBlank()
                            || "Google".equalsIgnoreCase(usuario.getApellido());
            if (!esFallbackGoogle || apellidoActualEsFallback) {
                usuario.setApellido(apellido);
                cambio = true;
            }
        }
        if (cambio) {
            log.info("Nombre actualizado desde Google para {}: {} {}", usuario.getEmail(), nombre, apellido);
        }
    }

    private String[] resolverNombreApellido(String email, Map<String, Object> attributes) {
        String nombre = texto(attributes.get("given_name"));
        String apellido = texto(attributes.get("family_name"));

        if (nombre == null || apellido == null) {
            String nombreCompleto = texto(attributes.get("name"));
            if (nombreCompleto != null) {
                String[] partes = nombreCompleto.split("\\s+", 2);
                if (nombre == null) {
                    nombre = partes[0];
                }
                if (apellido == null) {
                    apellido = partes.length > 1 ? partes[1] : null;
                }
            }
        }

        if (nombre == null) {
            int arroba = email != null ? email.indexOf('@') : -1;
            nombre = arroba > 0 ? email.substring(0, arroba) : (email != null ? email : "Usuario");
        }
        if (apellido == null || apellido.isBlank()) {
            apellido = "Google";
        }

        return new String[]{nombre, apellido};
    }

    private String normalizarEmail(Object value) {
        if (value instanceof String email && !email.isBlank()) {
            return email.trim().toLowerCase(Locale.ROOT);
        }
        return null;
    }

    private boolean emailVerificado(Object value) {
        if (value instanceof Boolean verificado) {
            return verificado;
        }
        if (value instanceof String verificado) {
            return "true".equalsIgnoreCase(verificado.trim());
        }
        return false;
    }

    private String texto(Object value) {
        if (value instanceof String texto && !texto.isBlank()) {
            return texto.trim();
        }
        return null;
    }

    private OAuth2AuthenticationException error(String code, String description) {
        return new OAuth2AuthenticationException(new OAuth2Error(code, description, null));
    }
}
