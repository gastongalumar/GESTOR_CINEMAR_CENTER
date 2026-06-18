package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.RegistroRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario.ActualizarNombreUsuarioRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
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
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import GESTOR_CINEMAR_CENTER.DEV.validation.impl.TelefonoArgentinoValidatorImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("usuarioService")
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

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
        if (telefono == null || telefono.isBlank()) {
            throw new ReglaNegocioException("El teléfono es obligatorio");
        }

        if (!TelefonoArgentinoValidatorImpl.esValido(telefono)) {
            throw new ReglaNegocioException(
                    "El teléfono debe estar en formato internacional argentino. Ejemplo: +54 9 351 1234567");
        }

        String normalizado = TelefonoArgentinoValidatorImpl.normalizar(telefono);

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

        String telefonoNormalizado = validarTelefono(request.getTelefono());
        String passwordHash = passwordEncoder.encode(request.getPassword());

        Administrador admin = new Administrador();
        admin.setNombre(request.getNombre().trim());
        admin.setApellido(request.getApellido().trim());
        admin.setEmail(emailNormalizado);
        admin.setPassword(passwordHash);
        admin.setTelefono(telefonoNormalizado);
        admin.setTipo(TipoUsuario.ADMINISTRADOR);
        admin.setEstado(EstadoUsuario.ACTIVO);

        admin = usuarioRepository.save(admin);
        return usuarioMapper.toAuthResponse(admin, generarToken(admin));
    }

    private String generarToken(Usuario usuario) {
        return jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getTipo().name(),
                usuario.getId()
        );
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            throw new ReglaNegocioException("El email es obligatorio");
        }
        return email.trim().toLowerCase();
    }

    private void validarEstadoActivo(Usuario usuario) {
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
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
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id, String emailAdministrador) {
        Usuario administrador = findByEmail(emailAdministrador);

        if (administrador.getId().equals(id)) {
            throw new ReglaNegocioException("No puede desactivar su propia cuenta");
        }

        Usuario usuario = findById(id);

        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new ReglaNegocioException("El usuario ya está inactivo");
        }

        if (reservaRepository.existsReservasActivasFuturasPorCliente(
                id, ESTADOS_RESERVA_ACTIVA, LocalDateTime.now())) {
            throw new ReglaNegocioException(
                    "No se puede desactivar el usuario porque tiene reservas activas con funciones futuras");
        }

        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);
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
}
