package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Pagos", description = "Operaciones de consulta de pagos. Requiere rol ADMINISTRADOR")
@SecurityRequirement(name = "bearerAuth")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Listar todos los pagos", description = "Retorna el historial completo de pagos registrados")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PagoResponseDTO.class))))
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @Operation(summary = "Obtener pago por ID", description = "Busca un pago específico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PagoResponseDTO> buscarPorId(
            @Parameter(description = "ID del pago", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar pago por número de ticket", description = "Obtiene el pago asociado a una reserva por su ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/reserva/{numeroTicket}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PagoResponseDTO> buscarPorReserva(
            @Parameter(description = "Número de ticket de la reserva", required = true) @PathVariable String numeroTicket) {
        return ResponseEntity.ok(pagoService.buscarPorReserva(numeroTicket));
    }
}
