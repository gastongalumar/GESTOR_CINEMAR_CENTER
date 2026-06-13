package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;

import java.util.List;

public interface PagoService {
    List<PagoResponseDTO> listarTodos();
    PagoResponseDTO buscarPorId(Long id);
    PagoResponseDTO buscarPorReserva(String numeroTicket);
    Pago buscarPagoPorReserva(Reserva reserva);
    Pago crearPago(Reserva reserva, double monto, String metodoPago);
}
