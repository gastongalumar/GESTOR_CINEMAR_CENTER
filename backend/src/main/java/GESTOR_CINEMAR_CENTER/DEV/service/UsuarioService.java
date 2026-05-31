package GESTOR_CINEMAR_CENTER.DEV.service;


import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.RegistroRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
import GESTOR_CINEMAR_CENTER.DEV.mapper.UsuarioMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import GESTOR_CINEMAR_CENTER.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;



    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        usuario.setFechaUltimoAcceso(LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        return usuarioMapper.toAuthResponse(usuario, generarToken(usuario));
    }

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        Usuario usuario;
        if (request.isEsAdministrador()) {
            usuario = new Administrador(
                    request.getNombre(),
                    request.getApellido(),
                    request.getEmail(),
                    passwordHash,
                    request.getTelefono()
            );
        } else {
            usuario = new Cliente(
                    request.getNombre(),
                    request.getApellido(),
                    request.getEmail(),
                    passwordHash,
                    request.getTelefono()
            );
        }

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toAuthResponse(usuario, generarToken(usuario));
    }

    public String generarToken(Usuario usuario) {
        return jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getTipo().name(),
                usuario.getId()
        );
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        usuarioRepository.delete(usuario);
    }
}
