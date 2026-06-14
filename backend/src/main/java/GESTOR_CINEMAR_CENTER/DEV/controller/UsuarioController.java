package GESTOR_CINEMAR_CENTER.DEV.controller;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
}
