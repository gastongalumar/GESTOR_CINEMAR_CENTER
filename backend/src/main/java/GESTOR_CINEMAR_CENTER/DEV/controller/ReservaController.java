package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.ReservaService;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones de reserva de entradas y validación de tickets")
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "Obtener mis reservas", description = "Retorna las reservas del cliente autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/mis")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(reservaService.listarReservasPorEmail(email));
    }

    @Operation(
            summary = "Crear una reserva",
            description = "Reserva asientos para una función y genera un número de ticket",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CrearReservaRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva creada correctamente",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o asientos no disponibles"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            Authentication authentication,
            @Valid @RequestBody CrearReservaRequestDTO request) {
        return ResponseEntity.ok(reservaService.crear(request, authentication.getName()));
    }

    @Operation(summary = "Listar reservas por cliente", description = "Retorna todas las reservas de un cliente",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente", required = true) @Positive @PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.listarPorCliente(clienteId));
    }

    @Operation(summary = "Buscar reserva por número de ticket", description = "Obtiene los detalles de una reserva a partir de su ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @GetMapping("/ticket/{numeroTicket}")
    public ResponseEntity<ReservaResponseDTO> buscarPorTicket(
            @Parameter(description = "Número de ticket de la reserva", required = true)
            @NotBlank @Pattern(regexp = "^TK\\d+$", message = "Formato de ticket inválido")
            @PathVariable String numeroTicket) {
        return ResponseEntity.ok(reservaService.buscarReservaPorTicket(numeroTicket));
    }

    @Operation(summary = "Validar ticket de entrada", description = "Verifica la validez de un ticket y opcionalmente lo marca como usado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la validación",
                    content = @Content(schema = @Schema(implementation = ValidacionTicketResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @PostMapping("/validar/{numeroTicket}")
    public ResponseEntity<ValidacionTicketResponseDTO> validarTicket(
            @Parameter(description = "Número de ticket a validar", required = true)
            @NotBlank @Pattern(regexp = "^TK\\d+$", message = "Formato de ticket inválido")
            @PathVariable String numeroTicket,
            @Parameter(description = "Si es true, marca el ticket como usado al validar") @RequestParam(defaultValue = "true") boolean marcarUsado) {
        return ResponseEntity.ok(reservaService.validarTicket(numeroTicket, marcarUsado));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente por número de ticket",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada correctamente",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "400", description = "Reserva no cancelable (ya cancelada, validada o con menos de 24 h de anticipación)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/ticket/{numeroTicket}")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> cancelar(
            @Parameter(description = "Número de ticket de la reserva", required = true)
            @NotBlank @Pattern(regexp = "^TK\\d+$", message = "Formato de ticket inválido")
            @PathVariable String numeroTicket) {
        reservaService.cancelar(numeroTicket);
        return ResponseEntity.ok(new MensajeResponse("Reserva cancelada correctamente"));
    }

    @Operation(summary = "Listar todas las reservas", description = "Retorna todas las reservas registradas en el sistema",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/admin/todas")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarTodasReservas() {
        return ResponseEntity.ok(reservaService.listarTodasReservas());
    }

    @Operation(summary = "Filtrar reservas por función", description = "Retorna todas las reservas de una función específica",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/admin/funcion/{funcionId}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> filtrarPorFuncion(
            @Parameter(description = "ID de la función", required = true) @Positive @PathVariable Long funcionId) {
        return ResponseEntity.ok(reservaService.filtrarReservasPorFuncion(funcionId));
    }

    @Operation(summary = "Filtrar reservas por sala", description = "Retorna todas las reservas de una sala específica",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/admin/sala/{salaId}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> filtrarPorSala(
            @Parameter(description = "ID de la sala", required = true) @Positive @PathVariable Long salaId) {
        return ResponseEntity.ok(reservaService.filtrarReservasPorSala(salaId));
    }

    @Operation(summary = "Filtrar reservas por rango de fechas", description = "Retorna reservas dentro de un rango de fechas específico",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/admin/fechas")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> filtrarPorFecha(
            @Parameter(description = "Fecha y hora inicial (ISO 8601)", required = true) @RequestParam LocalDateTime desde,
            @Parameter(description = "Fecha y hora final (ISO 8601)", required = true) @RequestParam LocalDateTime hasta) {
        return ResponseEntity.ok(reservaService.filtrarReservasPorFecha(desde, hasta));
    }
}
