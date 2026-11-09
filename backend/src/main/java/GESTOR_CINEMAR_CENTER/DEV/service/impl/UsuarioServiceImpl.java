package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.ForgotPasswordRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.RegistroRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.ResetPasswordRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario.ActualizarNombreUsuarioRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.usuario.UsuarioResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.exception.ConflictoRecursoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.UsuarioMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.UsuarioRepository;
import GESTOR_CINEMAR_CENTER.DEV.security.JwtUtil;
import GESTOR_CINEMAR_CENTER.DEV.service.ImagenesService;
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import GESTOR_CINEMAR_CENTER.DEV.validation.impl.TelefonoArgentinoValidatorImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("usuarioService")
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    /** Admin sembrado en DataInitializer: no se puede borrar ni dar de baja. */
    public static final String EMAIL_ADMIN_PROTEGIDO = "admin@mail.com";

    private static final String MENSAJE_RECUPERACION_GENERICO =
            "Si el correo está registrado, te enviamos el enlace de recuperación. En esta demo mirá la consola del backend.";

    private static final int RESET_TOKEN_MINUTOS = 30;

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;
    private final ImagenesService imagenesService;

    @Value("${mercadopago.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    private static final List<EstadoReserva> ESTADOS_RESERVA_ACTIVA = List.of(
            EstadoReserva.PENDIENTE,
            EstadoReserva.CONFIRMADA,
            EstadoReserva.VALIDADA
    );

    @Override
    public AuthResponse login(String email, String password) {
        String emailNormalizado = normalizarEmail(email);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailNormalizado, password)
        );

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "email", emailNormalizado));

        validarEstadoActivo(usuario);

        usuario.setFechaUltimoAcceso(LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        return usuarioMapper.toAuthResponse(usuario, generarToken(usuario));
    }

    public String validarTelefono(String telefono) {
        return validarTelefono(telefono, null);
    }

    public String validarTelefono(String telefono, String telefonoActualDelUsuario) {
        if (telefono == null || telefono.isBlank()) {
            return null;
        }

        if (!TelefonoArgentinoValidatorImpl.esValido(telefono)) {
            throw new ReglaNegocioException(
                    "Teléfono inválido. Usá código de área + número sin 15. Ejemplo: 0223 / 5678912");
        }

        String normalizado = TelefonoArgentinoValidatorImpl.normalizar(telefono);

        if (telefonoActualDelUsuario != null
                && telefonoActualDelUsuario.equals(normalizado)) {
            return normalizado;
        }

        if (usuarioRepository.existsByTelefono(normalizado)) {
            throw new ConflictoRecursoException("El teléfono ingresado ya se encuentra registrado");
        }

        return normalizado;
    }

    public AuthResponse registrar(RegistroRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new ConflictoRecursoException("El email ya está registrado");
        }

        String telefonoNormalizado = validarTelefono(request.getTelefono());
        String passwordHash = passwordEncoder.encode(request.getPassword());

        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre().trim());
        cliente.setApellido(request.getApellido().trim());
        cliente.setEmail(emailNormalizado);
        cliente.setPassword(passwordHash);
        cliente.setTelefono(telefonoNormalizado);
        cliente.setTipo(TipoUsuario.CLIENTE);
        cliente.setEstado(EstadoUsuario.ACTIVO);
        cliente = usuarioRepository.save(cliente);
        return usuarioMapper.toAuthResponse(cliente, generarToken(cliente));
    }

    public AuthResponse registrarAdministrador(RegistroRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new ConflictoRecursoException("El email ya está registrado");
        }

        TipoUsuario rolStaff = resolverRolStaff(request.getTipo());

        String telefonoNormalizado = validarTelefono(request.getTelefono());
        String passwordHash = passwordEncoder.encode(request.getPassword());

        Administrador admin = new Administrador();
        admin.setNombre(request.getNombre().trim());
        admin.setApellido(request.getApellido().trim());
        admin.setEmail(emailNormalizado);
        admin.setPassword(passwordHash);
        admin.setTelefono(telefonoNormalizado);
        admin.setTipo(rolStaff);
        admin.setEstado(EstadoUsuario.ACTIVO);

        admin = usuarioRepository.save(admin);
        return usuarioMapper.toAuthResponse(admin, generarToken(admin));
    }

    @Override
    @Transactional
    public MensajeResponse solicitarRecuperacionPassword(ForgotPasswordRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());
        usuarioRepository.findByEmailIgnoreCase(emailNormalizado).ifPresent(usuario -> {
            if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
                return;
            }
            String token = UUID.randomUUID().toString().replace("-", "");
            usuario.setResetToken(token);
            usuario.setResetTokenExpira(LocalDateTime.now().plusMinutes(RESET_TOKEN_MINUTOS));
            usuarioRepository.save(usuario);

            String link = frontendUrl.replaceAll("/$", "") + "/reset-password?token=" + token;
            log.info("[RESET] {} → {} (válido {} min)", emailNormalizado, link, RESET_TOKEN_MINUTOS);
            System.out.println("======================================================");
            System.out.println("[RESET PASSWORD] " + emailNormalizado);
            System.out.println("Abrí este link (simula el mail):");
            System.out.println(link);
            System.out.println("Válido " + RESET_TOKEN_MINUTOS + " minutos.");
            System.out.println("======================================================");
        });

        return new MensajeResponse(MENSAJE_RECUPERACION_GENERICO);
    }

    @Override
    @Transactional
    public MensajeResponse restablecerPassword(ResetPasswordRequest request) {
        String token = request.getToken() == null ? "" : request.getToken().trim();
        if (token.isBlank()) {
            throw new ReglaNegocioException("El token es inválido o expiró");
        }

        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new ReglaNegocioException("El token es inválido o expiró"));

        if (usuario.getResetTokenExpira() == null
                || usuario.getResetTokenExpira().isBefore(LocalDateTime.now())) {
            usuario.setResetToken(null);
            usuario.setResetTokenExpira(null);
            usuarioRepository.save(usuario);
            throw new ReglaNegocioException("El token es inválido o expiró");
        }

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new DisabledException("La cuenta de usuario no está activa");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuario.setResetToken(null);
        usuario.setResetTokenExpira(null);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        return new MensajeResponse("Contraseña actualizada. Ya podés iniciar sesión.");
    }

    /** Solo ADMINISTRADOR o SUPERADMIN; default ADMINISTRADOR. */
    private TipoUsuario resolverRolStaff(TipoUsuario solicitado) {
        if (solicitado == null || solicitado == TipoUsuario.ADMINISTRADOR) {
            return TipoUsuario.ADMINISTRADOR;
        }
        if (solicitado == TipoUsuario.SUPERADMIN) {
            return TipoUsuario.SUPERADMIN;
        }
        throw new ReglaNegocioException("Solo se pueden crear roles ADMINISTRADOR o SUPERADMIN");
    }

    private String generarToken(Usuario usuario) {
        String tipoStr = usuario.getTipo() != null
                ? usuario.getTipo().name()
                : (usuario instanceof Administrador ? "ADMINISTRADOR" : "CLIENTE");
        return jwtUtil.generateToken(
                usuario.getEmail(),
                tipoStr,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getFotoPerfilUrl()
        );
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            throw new ReglaNegocioException("El email es obligatorio");
        }
        return email.trim().toLowerCase();
    }

    private void validarEstadoActivo(Usuario usuario) {
        if (usuario.getEstado() != null && usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new DisabledException("La cuenta de usuario no está activa");
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "email", email));
    }

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioMapper.toDTOList(usuarioRepository.findAll());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarMiNombre(String email, ActualizarNombreUsuarioRequestDTO request) {
        Usuario usuario = findByEmail(email);

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new DisabledException("La cuenta de usuario no está activa");
        }

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setTelefono(validarTelefono(request.getTelefono(), usuario.getTelefono()));
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id, String emailAdminAutenticado) {
        Usuario usuario = findById(id);
        validarNoEsAdminProtegido(usuario);
        validarNoEsUnoMismo(usuario, emailAdminAutenticado);

        if (!esStaff(usuario.getTipo())) {
            throw new ReglaNegocioException(
                    "Solo se pueden eliminar cuentas de administrador o superadmin. Para clientes usá dar de baja.");
        }

        usuarioRepository.delete(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO darDeBaja(Long id, String emailAdminAutenticado) {
        Usuario usuario = findById(id);
        validarNoEsAdminProtegido(usuario);
        validarNoEsUnoMismo(usuario, emailAdminAutenticado);

        if (esStaff(usuario.getTipo())) {
            throw new ReglaNegocioException(
                    "No se puede dar de baja a administradores o superadmins. Usá eliminar (solo SUPERADMIN).");
        }

        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new ReglaNegocioException("El usuario ya está dado de baja");
        }

        if (reservaRepository.existsReservasActivasFuturasPorCliente(
                id, ESTADOS_RESERVA_ACTIVA, LocalDateTime.now())) {
            throw new ReglaNegocioException(
                    "No se puede dar de baja el usuario porque tiene reservas activas con funciones futuras");
        }

        usuario.setEstado(EstadoUsuario.INACTIVO);
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    private static boolean esStaff(TipoUsuario tipo) {
        return tipo == TipoUsuario.ADMINISTRADOR || tipo == TipoUsuario.SUPERADMIN;
    }

    @Override
    public UsuarioResponseDTO reactivar(Long id, String emailAdminAutenticado) {
        Usuario usuario = findById(id);
        validarNoEsAdminProtegido(usuario);

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            throw new ReglaNegocioException("El usuario ya está activo");
        }

        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setIntentosFallidos(0);
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    private void validarNoEsAdminProtegido(Usuario usuario) {
        if (EMAIL_ADMIN_PROTEGIDO.equalsIgnoreCase(usuario.getEmail())) {
            throw new ReglaNegocioException("No se puede modificar ni eliminar el administrador del sistema");
        }
    }

    private void validarNoEsUnoMismo(Usuario usuario, String emailAdminAutenticado) {
        if (emailAdminAutenticado != null
                && usuario.getEmail() != null
                && usuario.getEmail().equalsIgnoreCase(emailAdminAutenticado.trim())) {
            throw new ReglaNegocioException("No podés aplicar esta acción sobre tu propia cuenta");
        }
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioMapper.toDTO(findById(id));
    }

    @Override
    public List<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        List<Usuario> usuarios = usuarioRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre);
        return usuarioMapper.toDTOList(usuarios);
    }

    @Override
    public List<UsuarioResponseDTO> listarPorRol(TipoUsuario tipo) {
        List<Usuario> usuarios = usuarioRepository.findByTipo(tipo);
        return usuarioMapper.toDTOList(usuarios);
    }

    @Override
    public UsuarioResponseDTO obtenerMiPerfil(String emailAutenticado) {
        return usuarioMapper.toDTO(findByEmail(emailAutenticado));
    }

    @Override
    public UsuarioResponseDTO actualizarFotoPerfil(String emailAutenticado, MultipartFile file) {
        Usuario usuario = findByEmail(emailAutenticado);
        String anterior = usuario.getFotoPerfilUrl();
        String nueva = imagenesService.guardarImagenPerfil(file);
        usuario.setFotoPerfilUrl(nueva);
        usuarioRepository.save(usuario);

        if (anterior != null && !anterior.isBlank() && !anterior.equals(nueva)) {
            try {
                imagenesService.eliminarImagen(anterior);
            } catch (Exception ignored) {
                // No bloquea el flujo si falla borrar la imagen vieja.
            }
        }

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO eliminarFotoPerfil(String emailAutenticado) {
        Usuario usuario = findByEmail(emailAutenticado);
        String anterior = usuario.getFotoPerfilUrl();
        usuario.setFotoPerfilUrl(null);
        usuarioRepository.save(usuario);

        if (anterior != null && !anterior.isBlank()) {
            try {
                imagenesService.eliminarImagen(anterior);
            } catch (Exception ignored) {
                // No bloquea el flujo si falla borrar el archivo.
            }
        }

        return usuarioMapper.toDTO(usuario);
    }
}
