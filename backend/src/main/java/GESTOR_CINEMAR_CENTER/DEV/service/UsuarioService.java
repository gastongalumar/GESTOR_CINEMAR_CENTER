package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.ForgotPasswordRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.RegistroRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.auth.ResetPasswordRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario.ActualizarNombreUsuarioRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.usuario.UsuarioResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UsuarioService {
    AuthResponse login(String email, String password);
    AuthResponse registrar(RegistroRequest request);
    AuthResponse registrarAdministrador(RegistroRequest request);

    /** Solicita recuperación: genera token y lo loguea en consola (simula mail). */
    MensajeResponse solicitarRecuperacionPassword(ForgotPasswordRequest request);

    /** Restablece la contraseña con un token válido. */
    MensajeResponse restablecerPassword(ResetPasswordRequest request);
    Usuario findByEmail(String email);
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO actualizarMiNombre(String email, ActualizarNombreUsuarioRequestDTO request);
    void eliminarUsuario(Long id, String emailAdminAutenticado);
    UsuarioResponseDTO darDeBaja(Long id, String emailAdminAutenticado);
    UsuarioResponseDTO reactivar(Long id, String emailAdminAutenticado);
    Usuario findById(Long id);
    UsuarioResponseDTO buscarPorId(Long id);
    List<UsuarioResponseDTO> buscarPorNombre(String nombre);
    List<UsuarioResponseDTO> listarPorRol(TipoUsuario tipo);

    /** Perfil del usuario autenticado (email del JWT). */
    UsuarioResponseDTO obtenerMiPerfil(String emailAutenticado);

    /** Sube/reemplaza la foto de perfil del usuario autenticado. */
    UsuarioResponseDTO actualizarFotoPerfil(String emailAutenticado, MultipartFile file);

    /** Elimina la foto de perfil del usuario autenticado. */
    UsuarioResponseDTO eliminarFotoPerfil(String emailAutenticado);
}