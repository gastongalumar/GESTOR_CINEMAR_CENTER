package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody CrearReservaRequestDTO request) {
        return ResponseEntity.ok(reservaService.crear(request));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.listarPorCliente(clienteId));
    }

    @GetMapping("/ticket/{numeroTicket}")
    public ResponseEntity<ReservaResponseDTO> buscarPorTicket(@PathVariable String numeroTicket) {
        return ResponseEntity.ok(reservaService.buscarReservaPorTicket(numeroTicket));
    }

    @GetMapping("/ticket/{numeroTicket}/pago")
    public ResponseEntity<PagoResponseDTO> buscarPagoPorTicket(@PathVariable String numeroTicket) {
        return ResponseEntity.ok(reservaService.buscarPagoPorTicket(numeroTicket));
    }

    @PostMapping("/validar/{numeroTicket}")
    public ResponseEntity<ValidacionTicketResponseDTO> validarTicket(
            @PathVariable String numeroTicket,
            @RequestParam(defaultValue = "true") boolean marcarUsado) {
        return ResponseEntity.ok(reservaService.validarTicket(numeroTicket, marcarUsado));
    }

    @PutMapping("/ticket/{numeroTicket}/pago")
    public ResponseEntity<ReservaResponseDTO> actualizarMetodoPago(
            @PathVariable String numeroTicket,
            @Valid @RequestBody ActualizarMetodoPagoRequestDTO request) {
        return ResponseEntity.ok(reservaService.actualizarMetodoPago(numeroTicket, request));
    }

    @DeleteMapping("/ticket/{numeroTicket}")
    public ResponseEntity<MensajeResponse> cancelar(@PathVariable String numeroTicket) {
        reservaService.cancelar(numeroTicket);
        return ResponseEntity.ok(new MensajeResponse("Reserva cancelada correctamente"));
    }

    @GetMapping("/funcion/{funcionId}/ocupados")
    public ResponseEntity<List<String>> obtenerAsientosOcupados(@PathVariable Long funcionId) {
        return ResponseEntity.ok(reservaService.obtenerAsientosOcupados(funcionId));
    }
}
