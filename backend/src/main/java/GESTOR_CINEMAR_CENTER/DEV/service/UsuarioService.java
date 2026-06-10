package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.RegistroRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.usuario.UsuarioResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;

import java.util.List;

public interface UsuarioService {
    AuthResponse login(String email, String password);
    AuthResponse registrar(RegistroRequest request);
    Usuario findByEmail(String email);
    List<UsuarioResponseDTO> listarTodos();
    void eliminarUsuario(Long id);
    Usuario findById(Long id);
    UsuarioResponseDTO buscarPorId(Long id);
    List<UsuarioResponseDTO> buscarPorNombre(String nombre);
    List<UsuarioResponseDTO> listarPorRol(TipoUsuario tipo);
}