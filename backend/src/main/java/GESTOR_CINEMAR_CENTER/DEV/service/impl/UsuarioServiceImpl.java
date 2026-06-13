package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.RegistroRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.usuario.UsuarioResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.UsuarioMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import GESTOR_CINEMAR_CENTER.DEV.repository.UsuarioRepository;
import GESTOR_CINEMAR_CENTER.DEV.security.JwtUtil;
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("usuarioService")
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    @Override
    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "email", email));
        usuario.setFechaUltimoAcceso(LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        return usuarioMapper.toAuthResponse(usuario, generarToken(usuario));
    }

    public String validarTelefono(String telefono) {

        if (telefono == null || telefono.isBlank()) {
            throw new ReglaNegocioException(
                    "El teléfono es obligatorio"
            );
        }

        // Quitamos espacios, +, guiones, paréntesis, etc.
        String normalizado = telefono.replaceAll("\\D", "");

        // Formato internacional argentino obligatorio: 549 + 10 dígitos
        if (!normalizado.startsWith("549")) {
            throw new ReglaNegocioException(
                    "El teléfono debe estar en formato internacional argentino. Ejemplo: +54 9 351 1234567"
            );
        }


        if (normalizado.length() != 13) {
            throw new ReglaNegocioException(
                    "El teléfono debe tener 13 dígitos en formato internacional. Formato esperado: 549XXXXXXXXX"
            );
        }


        // Sacamos el prefijo internacional 549
        String numeroNacional = normalizado.substring(3);


        // Evitar números tipo 5491111111111, 5492222222222, etc.
        if (numeroNacional.matches("^(.)\\1+$")) {
            throw new ReglaNegocioException(
                    "El teléfono no es válido porque el número no puede tener los 10 dígitos iguales"
            );
        }


        // Evitar algunos casos absurdos de secuencia
        if (numeroNacional.equals("1234567890")
                || numeroNacional.equals("9876543210")) {

            throw new ReglaNegocioException(
                    "El teléfono no es válido porque contiene una secuencia inválida"
            );
        }


        // Comprobar duplicado (en BD ya está guardado normalizado)
        boolean telefonoExistente = usuarioRepository.findAll()
                .stream()
                .map(Usuario::getTelefono)
                .anyMatch(t -> t != null && t.equals(normalizado));


        if (telefonoExistente) {
            throw new ReglaNegocioException(
                    "El teléfono ingresado ya se encuentra registrado"
            );
        }


        return normalizado;
    }
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ReglaNegocioException("El email ya está registrado");
        }

        String telefonoNormalizado = validarTelefono(request.getTelefono());
        String passwordHash = passwordEncoder.encode(request.getPassword());
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        cliente.setPassword(passwordHash);
        cliente.setTelefono(telefonoNormalizado);
        cliente.setTipo(TipoUsuario.CLIENTE);
        cliente= usuarioRepository.save(cliente);
        return usuarioMapper.toAuthResponse(cliente, generarToken(cliente));
    }


    public AuthResponse registrarAdministrador(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ReglaNegocioException("El email ya está registrado");
        }

        // Validar y normalizar teléfono (es obligatorio)
        String telefonoNormalizado = validarTelefono(request.getTelefono());

        String passwordHash = passwordEncoder.encode(request.getPassword());

        // Crear Administrador
        Administrador admin = new Administrador();
        admin.setNombre(request.getNombre());
        admin.setApellido(request.getApellido());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordHash);
        admin.setTelefono(telefonoNormalizado);
        admin.setTipo(TipoUsuario.ADMINISTRADOR);

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

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", "email", email));
    }

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioMapper.toDTOList(usuarioRepository.findAll());
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
        usuarioRepository.delete(usuario);
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
