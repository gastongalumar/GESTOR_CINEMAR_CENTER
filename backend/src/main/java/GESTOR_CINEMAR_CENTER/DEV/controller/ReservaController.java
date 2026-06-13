package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.ReservaService;
import GESTOR_CINEMAR_CENTER.DEV.service.impl.ReservaServiceImpl;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones de reserva de entradas, validación de tickets y gestión de pagos")
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
    //@PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody CrearReservaRequestDTO request) {
        return ResponseEntity.ok(reservaService.crear(request));
    }

    @Operation(summary = "Listar reservas por cliente", description = "Retorna todas las reservas de un cliente",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Long clienteId) {
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
            @Parameter(description = "Número de ticket de la reserva", required = true) @PathVariable String numeroTicket) {
        return ResponseEntity.ok(reservaService.buscarReservaPorTicket(numeroTicket));
    }

    @Operation(summary = "Buscar pago por número de ticket", description = "Obtiene la información de pago asociada a un ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ticket o pago no encontrado")
    })
    @GetMapping("/ticket/{numeroTicket}/pago")
    public ResponseEntity<PagoResponseDTO> buscarPagoPorTicket(
            @Parameter(description = "Número de ticket de la reserva", required = true) @PathVariable String numeroTicket) {
        return ResponseEntity.ok(reservaService.buscarPagoPorTicket(numeroTicket));
    }

    @Operation(summary = "Validar ticket de entrada", description = "Verifica la validez de un ticket y opcionalmente lo marca como usado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la validación",
                    content = @Content(schema = @Schema(implementation = ValidacionTicketResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @PostMapping("/validar/{numeroTicket}")
    public ResponseEntity<ValidacionTicketResponseDTO> validarTicket(
            @Parameter(description = "Número de ticket a validar", required = true) @PathVariable String numeroTicket,
            @Parameter(description = "Si es true, marca el ticket como usado al validar") @RequestParam(defaultValue = "true") boolean marcarUsado) {
        return ResponseEntity.ok(reservaService.validarTicket(numeroTicket, marcarUsado));
    }

    @Operation(
            summary = "Actualizar método de pago",
            description = "Modifica el método de pago de una reserva existente",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ActualizarMetodoPagoRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Método de pago actualizado",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PutMapping("/ticket/{numeroTicket}/pago")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ReservaResponseDTO> actualizarMetodoPago(
            @Parameter(description = "Número de ticket de la reserva", required = true) @PathVariable String numeroTicket,
            @Valid @RequestBody ActualizarMetodoPagoRequestDTO request) {
        return ResponseEntity.ok(reservaService.actualizarMetodoPago(numeroTicket, request));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente por número de ticket",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada correctamente",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/ticket/{numeroTicket}")
    @PreAuthorize("hasAuthority('CLIENTE') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> cancelar(
            @Parameter(description = "Número de ticket de la reserva", required = true) @PathVariable String numeroTicket) {
        reservaService.cancelar(numeroTicket);
        return ResponseEntity.ok(new MensajeResponse("Reserva cancelada correctamente"));
    }

    @Operation(summary = "Obtener asientos ocupados", description = "Retorna los asientos ya reservados para una función")
    @ApiResponse(responseCode = "200", description = "Asientos ocupados obtenidos correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @GetMapping("/funcion/{funcionId}/ocupados")
    public ResponseEntity<List<String>> obtenerAsientosOcupados(
            @Parameter(description = "ID de la función", required = true) @PathVariable Long funcionId) {
        return ResponseEntity.ok(reservaService.obtenerAsientosOcupados(funcionId));
    }
}
