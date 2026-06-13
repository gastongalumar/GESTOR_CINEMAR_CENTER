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

    @Override
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ReglaNegocioException("El email ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        Usuario usuario;
        if (request.isEsAdministrador()) {
            Administrador admin = new Administrador();
            admin.setNombre(request.getNombre());
            admin.setApellido(request.getApellido());
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordHash);
            admin.setTelefono(request.getTelefono());
            admin.setTipo(TipoUsuario.ADMINISTRADOR);
            usuario = admin;
        } else {
            Cliente cliente = new Cliente();
            cliente.setNombre(request.getNombre());
            cliente.setApellido(request.getApellido());
            cliente.setEmail(request.getEmail());
            cliente.setPassword(passwordHash);
            cliente.setTelefono(request.getTelefono());
            cliente.setTipo(TipoUsuario.CLIENTE);
            usuario = cliente;
        }

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toAuthResponse(usuario, generarToken(usuario));
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
