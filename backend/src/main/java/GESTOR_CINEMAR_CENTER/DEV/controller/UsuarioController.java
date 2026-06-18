package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario.ActualizarNombreUsuarioRequestDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones de administración y consulta de usuarios. Requiere rol ADMINISTRADOR")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(
            summary = "Actualizar mi nombre y apellido",
            description = "Modifica el nombre y apellido del usuario autenticado según el token JWT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ActualizarNombreUsuarioRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre y apellido actualizados correctamente",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Cuenta inactiva")
    })
    @PutMapping("/mi-nombre")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> actualizarMiNombre(
            Authentication authentication,
            @Valid @RequestBody ActualizarNombreUsuarioRequestDTO request) {
        return ResponseEntity.ok(usuarioService.actualizarMiNombre(authentication.getName(), request));
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

    @Operation(summary = "Desactivar usuario", description = "Realiza un borrado lógico del usuario. No se permite desactivar la propia cuenta ni si tiene reservas activas con funciones futuras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Regla de negocio incumplida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminar(
            Authentication authentication,
            @Parameter(description = "ID del usuario", required = true) @Positive @PathVariable Long id) {
        usuarioService.eliminarUsuario(id, authentication.getName());
        return ResponseEntity.ok(new MensajeResponse("Usuario desactivado correctamente"));
    }
}
