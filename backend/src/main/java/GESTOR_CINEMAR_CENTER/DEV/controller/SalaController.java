package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.ActualizarSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.CrearSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento.AsientoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.sala.SalaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Salas", description = "Operaciones de consulta y gestión de salas de proyección")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @Operation(summary = "Listar todas las salas", description = "Retorna el listado completo de salas disponibles")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SalaResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(salaService.listarTodas());
    }

    @Operation(summary = "Obtener sala por ID", description = "Busca una sala específica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala encontrada",
                    content = @Content(schema = @Schema(implementation = SalaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la sala", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @Operation(summary = "Listar asientos de una sala", description = "Retorna todos los asientos configurados en la sala")
    @ApiResponse(responseCode = "200", description = "Asientos obtenidos correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AsientoResponseDTO.class))))
    @GetMapping("/{id}/asientos")
    public ResponseEntity<List<AsientoResponseDTO>> listarAsientos(
            @Parameter(description = "ID de la sala", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(salaService.listarAsientosPorSala(id));
    }

    @Operation(
            summary = "Crear una nueva sala",
            description = "Registra una sala con su capacidad y configuración de asientos",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CrearSalaRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala creada correctamente",
                    content = @Content(schema = @Schema(implementation = SalaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<SalaResponseDTO> crear(@Valid @RequestBody CrearSalaRequestDTO request) {
        return ResponseEntity.ok(salaService.crear(request));
    }

    @Operation(
            summary = "Actualizar una sala",
            description = "Modifica los datos de una sala existente",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ActualizarSalaRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = SalaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> actualizar(
            @Parameter(description = "ID de la sala", required = true) @PathVariable Long id,
            @Valid @RequestBody ActualizarSalaRequestDTO request) {
        return ResponseEntity.ok(salaService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una sala", description = "Elimina una sala del sistema",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala eliminada correctamente",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(
            @Parameter(description = "ID de la sala", required = true) @PathVariable Long id) {
        salaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Sala eliminada correctamente"));
    }
}
