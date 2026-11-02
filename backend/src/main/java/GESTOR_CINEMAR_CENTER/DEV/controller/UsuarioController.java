package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.usuario.UsuarioResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Consulta de usuarios (admin) y gestión del propio perfil")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Obtener mi perfil", description = "Retorna los datos del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Perfil obtenido",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> miPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerMiPerfil(authentication.getName()));
    }

    @Operation(summary = "Subir foto de perfil", description = "JPEG, PNG o WEBP hasta 5MB")
    @ApiResponse(responseCode = "200", description = "Foto actualizada",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @PostMapping(value = "/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> subirFotoPerfil(
            Authentication authentication,
            @RequestParam("foto") MultipartFile foto) {
        return ResponseEntity.ok(usuarioService.actualizarFotoPerfil(authentication.getName(), foto));
    }

    @Operation(summary = "Eliminar foto de perfil")
    @ApiResponse(responseCode = "200", description = "Foto eliminada",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @DeleteMapping("/me/foto")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> eliminarFotoPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.eliminarFotoPerfil(authentication.getName()));
    }

    @Operation(summary = "Listar todos los usuarios", description = "Retorna el listado completo de usuarios en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @Parameter(description = "ID del usuario", required = true) @Positive @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(summary = "Buscar usuarios por nombre o apellido", description = "Filtra usuarios cuyo nombre o apellido contenga la cadena proporcionada")
    @ApiResponse(responseCode = "200", description = "Usuarios filtrados correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(
            @Parameter(description = "Nombre o apellido a buscar", required = true) @RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Listar usuarios por rol", description = "Retorna el listado de usuarios que poseen un rol determinado (CLIENTE o ADMINISTRADOR)")
    @ApiResponse(responseCode = "200", description = "Usuarios filtrados por rol correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(
            @Parameter(description = "Rol del usuario (CLIENTE o ADMINISTRADOR)", required = true)
            @Pattern(regexp = "CLIENTE|ADMINISTRADOR", message = "Rol inválido")
            @PathVariable String rol) {
        TipoUsuario tipo = TipoUsuario.valueOf(rol.toUpperCase());
        return ResponseEntity.ok(usuarioService.listarPorRol(tipo));
    }

    @Operation(summary = "Dar de baja un usuario", description = "Pasa el estado a INACTIVO. No aplica al admin del sistema ni a la propia cuenta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario dado de baja",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Acción no permitida")
    })
    @PutMapping("/{id}/baja")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> darDeBaja(
            @Parameter(description = "ID del usuario", required = true) @Positive @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(usuarioService.darDeBaja(id, authentication.getName()));
    }

    @Operation(summary = "Reactivar un usuario", description = "Pasa el estado a ACTIVO")
    @ApiResponse(responseCode = "200", description = "Usuario reactivado",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @PutMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> reactivar(
            @Parameter(description = "ID del usuario", required = true) @Positive @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(usuarioService.reactivar(id, authentication.getName()));
    }

    @Operation(summary = "Eliminar administrador", description = "Borra un administrador. El admin del sistema (admin@mail.com) no puede eliminarse.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador eliminado"),
            @ApiResponse(responseCode = "400", description = "Acción no permitida")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminar(
            @Parameter(description = "ID del administrador", required = true) @Positive @PathVariable Long id,
            Authentication authentication) {
        usuarioService.eliminarUsuario(id, authentication.getName());
        return ResponseEntity.ok(new MensajeResponse("Administrador eliminado correctamente"));
    }
}
